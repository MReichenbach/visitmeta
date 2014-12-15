package de.hshannover.f4.trust.visitmeta.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.hshannover.f4.trust.visitmeta.datawrapper.TimeHolder;

public class PanelHistoryNavigation extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private TimeHolder mTimeHolder;
	private String mRestUrl;

	private int mCurrentMode;

	private static final String TIMEFORMAT = "yyyy-MM-FF HH:mm:ss";
	private DateFormat mDateFormatter;

	private static final int INDEX_LIVE_VIEW = 0;
	private static final int INDEX_HISTORY_VIEW = 1;
	private static final int INDEX_DELTA_VIEW = 2;

	private static final long TIMESTAMP_NOT_INITIALIZED = -1;
	private static final int INDEX_NOT_INITIALIZED = -1;

	private static final int DELTA_TIMESTAMP_START = 0;
	private static final int DELTA_TIMESTAMP_END = 1;

	private JTabbedPane mTabbedPane;

	private int mMaximumTimestampIndex;
	private int mMinimumTimestampIndex;

	private long mNewestTime;
	private long mOldestTime;
	private SortedMap<Long,Long> mChangesMap;

	private JLabel mLiveViewTimestampLabel;
	private JLabel mLiveViewRestUrlLabel;

	private long mHistoryViewSelectedTimestamp;
	private int mHistoryViewSelectedTimestampIndex;
	private JButton mHistoryViewForwardButton;
	private JButton mHistoryViewBackwardButton;
	private JSlider mHistoryViewSlider;
	private JLabel mHistoryViewSelectedTimestampLabel;
	private JLabel mHistoryViewSelectedTimestampRestUrlLabel;
	private ActionListener mHistoryViewBackwardButtonActionListener;
	private ActionListener mHistoryViewForwardButtonActionListener;
	private ChangeListener mHistoryViewSliderChangeListener;

	private ChangeListener mTabbedPaneChangeListener;

	private long mDeltaViewSelectedStartTimestamp;
	private int mDeltaViewSelectedStartTimestampIndex;
	private long mDeltaViewSelectedEndTimestamp;
	private int mDeltaViewSelectedEndTimestampIndex;
	private JLabel mDeltaViewSelectedStartTimestampLabel;
	private JLabel mDeltaViewSelectedEndTimestampLabel;
	private JLabel mDeltaViewRestUrlLabel;
	private JButton mDeltaViewStartBackwardButton;
	private JButton mDeltaViewStartForwardButton;
	private JButton mDeltaViewEndBackwardButton;
	private JButton mDeltaViewEndForwardButton;
	private int mChangesMapSize;

	public PanelHistoryNavigation(TimeHolder timeHolder, String restUrl) {
		super();
		//		this.setPreferredSize(new Dimension(400, 33));
		//		this.setMinimumSize(new Dimension(400, 33));
		this.setSize(600, 400);

		mDateFormatter = new SimpleDateFormat(TIMEFORMAT);

		mHistoryViewSelectedTimestamp = TIMESTAMP_NOT_INITIALIZED;
		mDeltaViewSelectedStartTimestamp = TIMESTAMP_NOT_INITIALIZED;
		mDeltaViewSelectedEndTimestamp = TIMESTAMP_NOT_INITIALIZED;

		mMinimumTimestampIndex = 0;
		mChangesMapSize = 0;

		mTimeHolder = timeHolder;
		mRestUrl = restUrl;

		mTabbedPane = new JTabbedPane();

		JPanel liveViewPanel = setUpLiveViewPanel();
		JPanel historyViewPanel = setUpHistoryViewPanel();
		JPanel deltaViewPanel = setUpDeltaViewPanel();

		mTabbedPane.insertTab("Live view", null, liveViewPanel, null, INDEX_LIVE_VIEW);
		mTabbedPane.insertTab("History view", null, historyViewPanel, null, INDEX_HISTORY_VIEW);
		mTabbedPane.insertTab("Delta view", null, deltaViewPanel, null, INDEX_DELTA_VIEW);
		mCurrentMode = INDEX_LIVE_VIEW;

		this.add(mTabbedPane);

		// initialize values for the first time and initialize the live view tab
		updateValues();
		switchTabEnableState();
		updateLiveView();

		createListeners();
		addListeners();
		mTimeHolder.addObserver(this);
	}

	private JPanel setUpLiveViewPanel() {
		JPanel liveViewPanel = new JPanel();
		liveViewPanel.setLayout(new BoxLayout(liveViewPanel, BoxLayout.Y_AXIS));

		mLiveViewTimestampLabel = new JLabel();
		mLiveViewTimestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mLiveViewTimestampLabel.setText(createLiveViewTimestampLabel());
		liveViewPanel.add(mLiveViewTimestampLabel);

		mLiveViewRestUrlLabel = new JLabel(createRestUrlLabel(INDEX_LIVE_VIEW));
		mLiveViewRestUrlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mLiveViewRestUrlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		registerLinkHandler(mLiveViewRestUrlLabel);
		liveViewPanel.add(mLiveViewRestUrlLabel);

		return liveViewPanel;
	}

	private JPanel setUpHistoryViewPanel() {
		JPanel historyViewPanel = new JPanel();
		historyViewPanel.setLayout(new BoxLayout(historyViewPanel, BoxLayout.Y_AXIS));

		mHistoryViewSelectedTimestampLabel = new JLabel();
		mHistoryViewSelectedTimestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mHistoryViewSelectedTimestampLabel.setText(createHistoryViewSelectedTimestampLabel());
		historyViewPanel.add(mHistoryViewSelectedTimestampLabel);

		mHistoryViewSelectedTimestampRestUrlLabel = new JLabel(createRestUrlLabel(INDEX_HISTORY_VIEW));
		mHistoryViewSelectedTimestampRestUrlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mHistoryViewSelectedTimestampRestUrlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		registerLinkHandler(mHistoryViewSelectedTimestampRestUrlLabel);
		historyViewPanel.add(mHistoryViewSelectedTimestampRestUrlLabel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mHistoryViewBackwardButton = new JButton("previous");
		mHistoryViewForwardButton = new JButton("next");
		buttonPanel.add(mHistoryViewBackwardButton);
		buttonPanel.add(mHistoryViewForwardButton);
		historyViewPanel.add(buttonPanel);

		mHistoryViewSlider = new JSlider(0, 1, 0);
		mHistoryViewSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		mHistoryViewSlider.setMajorTickSpacing(5);
		mHistoryViewSlider.setMinorTickSpacing(1);
		mHistoryViewSlider.setPaintTicks(true);
		mHistoryViewSlider.setPaintLabels(true);
		mHistoryViewSlider.setSnapToTicks(true);
		historyViewPanel.add(mHistoryViewSlider);

		return historyViewPanel;
	}

	private JPanel setUpDeltaViewPanel() {
		JPanel deltaViewPanel = new JPanel();
		deltaViewPanel.setLayout(new BoxLayout(deltaViewPanel, BoxLayout.Y_AXIS));

		mDeltaViewSelectedStartTimestampLabel = new JLabel();
		mDeltaViewSelectedStartTimestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mDeltaViewSelectedStartTimestampLabel.setText(createDeltaViewSelectedTimestampLabel(DELTA_TIMESTAMP_START));
		deltaViewPanel.add(mDeltaViewSelectedStartTimestampLabel);

		mDeltaViewSelectedEndTimestampLabel = new JLabel();
		mDeltaViewSelectedEndTimestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mDeltaViewSelectedEndTimestampLabel.setText(createDeltaViewSelectedTimestampLabel(DELTA_TIMESTAMP_END));
		deltaViewPanel.add(mDeltaViewSelectedEndTimestampLabel);

		mDeltaViewRestUrlLabel = new JLabel(createRestUrlLabel(INDEX_DELTA_VIEW));
		mDeltaViewRestUrlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mDeltaViewRestUrlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		registerLinkHandler(mDeltaViewRestUrlLabel);
		deltaViewPanel.add(mDeltaViewRestUrlLabel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		mDeltaViewStartBackwardButton = new JButton("<");
		mDeltaViewStartForwardButton = new JButton(">");
		buttonPanel.add(new JLabel("Start: "));
		buttonPanel.add(mDeltaViewStartBackwardButton);
		buttonPanel.add(mDeltaViewStartForwardButton);

		mDeltaViewEndBackwardButton = new JButton("<");
		mDeltaViewEndForwardButton = new JButton(">");
		buttonPanel.add(new JLabel("End: "));
		buttonPanel.add(mDeltaViewEndBackwardButton);
		buttonPanel.add(mDeltaViewEndForwardButton);
		deltaViewPanel.add(buttonPanel);

		return deltaViewPanel;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof TimeHolder) {
			int selectedIndex = mTabbedPane.getSelectedIndex();

			removeListeners();
			updateValues();
			switchTabEnableState();

			switch (selectedIndex) {
			case INDEX_LIVE_VIEW:
				updateLiveView();
				break;
			case INDEX_HISTORY_VIEW:
				updateHistoryView();
				break;
			case INDEX_DELTA_VIEW:
				updateDeltaView();
				break;
			default:
				break;
			}

			addListeners();
		}
	}

	private void updateValues() {
		mChangesMap = mTimeHolder.getChangesMap();

		if (mChangesMap != null) {
			int newChangesMapSize = mChangesMap.size();

			if (mChangesMapSize < newChangesMapSize) {
				mChangesMapSize = newChangesMapSize;

				mNewestTime = mChangesMap.lastKey();
				mOldestTime = mChangesMap.firstKey();

				mMaximumTimestampIndex = mChangesMap.size();

				if (mHistoryViewSelectedTimestamp == TIMESTAMP_NOT_INITIALIZED) {
					mHistoryViewSelectedTimestamp = mOldestTime;
				}

				if (mDeltaViewSelectedStartTimestamp == TIMESTAMP_NOT_INITIALIZED) {
					mDeltaViewSelectedStartTimestamp = mOldestTime;
				}

				if (mDeltaViewSelectedEndTimestamp == TIMESTAMP_NOT_INITIALIZED) {
					mDeltaViewSelectedEndTimestamp = mNewestTime;
				}

				mHistoryViewSelectedTimestampIndex = findIndexToTimestamp(mHistoryViewSelectedTimestamp, mChangesMap);
				mDeltaViewSelectedStartTimestampIndex = findIndexToTimestamp(mDeltaViewSelectedStartTimestamp, mChangesMap);
				mDeltaViewSelectedEndTimestampIndex = findIndexToTimestamp(mDeltaViewSelectedEndTimestamp, mChangesMap);
			}
		} else {
			mMaximumTimestampIndex = 0;
			mHistoryViewSelectedTimestampIndex = INDEX_NOT_INITIALIZED;
			mDeltaViewSelectedStartTimestampIndex = INDEX_NOT_INITIALIZED;
			mDeltaViewSelectedEndTimestampIndex = INDEX_NOT_INITIALIZED;
		}
	}

	private void updateLiveView() {
		mTimeHolder.setLiveView(true, false);

		mLiveViewTimestampLabel.setText(createLiveViewTimestampLabel());
	}

	private void updateHistoryView() {
		mTimeHolder.setLiveView(false, false);

		if (mHistoryViewSelectedTimestamp != TIMESTAMP_NOT_INITIALIZED
				&& mHistoryViewSelectedTimestampIndex != INDEX_NOT_INITIALIZED) {
			mHistoryViewSelectedTimestampRestUrlLabel.setText(createRestUrlLabel(INDEX_HISTORY_VIEW));

			mHistoryViewSlider.setMinimum(mMinimumTimestampIndex + 1);
			mHistoryViewSlider.setMaximum(mMaximumTimestampIndex);
			mHistoryViewSlider.setValue(mHistoryViewSelectedTimestampIndex + 1);
			mHistoryViewSlider.setLabelTable(createHistoryViewSliderLabelTable());
			mHistoryViewSlider.setEnabled(true);

			mHistoryViewBackwardButton.setEnabled(true);
			mHistoryViewForwardButton.setEnabled(true);

			mHistoryViewSelectedTimestampLabel.setText(createHistoryViewSelectedTimestampLabel());

			mTimeHolder.setDeltaTimeStart(mHistoryViewSelectedTimestamp, false);
			mTimeHolder.setDeltaTimeEnd(mHistoryViewSelectedTimestamp, false);
			mTimeHolder.notifyObservers();
		} else {
			mHistoryViewSlider.setEnabled(false);
			mHistoryViewBackwardButton.setEnabled(false);
			mHistoryViewForwardButton.setEnabled(false);
		}
	}

	private void updateDeltaView() {
		mTimeHolder.setLiveView(false, false);

		if (mDeltaViewSelectedStartTimestamp != TIMESTAMP_NOT_INITIALIZED
				&& mDeltaViewSelectedEndTimestamp != TIMESTAMP_NOT_INITIALIZED
				&& mDeltaViewSelectedStartTimestampIndex != INDEX_NOT_INITIALIZED
				&& mDeltaViewSelectedEndTimestampIndex != INDEX_NOT_INITIALIZED) {
			mDeltaViewRestUrlLabel.setText(createRestUrlLabel(INDEX_DELTA_VIEW));

			mDeltaViewStartForwardButton.setEnabled(true);
			mDeltaViewStartBackwardButton.setEnabled(true);
			mDeltaViewEndForwardButton.setEnabled(true);
			mDeltaViewEndBackwardButton.setEnabled(true);

			mDeltaViewSelectedStartTimestampLabel.setText(createDeltaViewSelectedTimestampLabel(DELTA_TIMESTAMP_START));
			mDeltaViewSelectedEndTimestampLabel.setText(createDeltaViewSelectedTimestampLabel(DELTA_TIMESTAMP_END));

			mTimeHolder.setDeltaTimeStart(mDeltaViewSelectedStartTimestamp, false);
			mTimeHolder.setDeltaTimeEnd(mDeltaViewSelectedEndTimestamp, false);
			mTimeHolder.notifyObservers();
		} else {
			mDeltaViewStartForwardButton.setEnabled(false);
			mDeltaViewStartBackwardButton.setEnabled(false);
			mDeltaViewEndForwardButton.setEnabled(false);
			mDeltaViewEndBackwardButton.setEnabled(false);
		}
	}

	private void createListeners() {
		mTabbedPaneChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mCurrentMode = mTabbedPane.getSelectedIndex();

				switch (mCurrentMode) {
				case INDEX_LIVE_VIEW:
					updateLiveView();
					break;
				case INDEX_HISTORY_VIEW:
					updateHistoryView();
					break;
				case INDEX_DELTA_VIEW:
					updateDeltaView();
					break;
				default:
					break;
				}
			}
		};

		mHistoryViewForwardButtonActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				incrementSelectedHistoryTimestamp();
				updateHistoryView();
			}
		};

		mHistoryViewBackwardButtonActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decrementSelectedHistoryTimestamp();
				updateHistoryView();
			}
		};

		mHistoryViewSliderChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!mHistoryViewSlider.getValueIsAdjusting()) {
					mHistoryViewSelectedTimestampIndex = mHistoryViewSlider.getValue() - 1;
					mHistoryViewSelectedTimestamp = findTimestampToIndex(mHistoryViewSelectedTimestampIndex, mChangesMap);
					updateHistoryView();
				}
			}
		};
	}

	private void addListeners() {
		mTabbedPane.addChangeListener(mTabbedPaneChangeListener);
		mHistoryViewForwardButton.addActionListener(mHistoryViewForwardButtonActionListener);
		mHistoryViewBackwardButton.addActionListener(mHistoryViewBackwardButtonActionListener);
		mHistoryViewSlider.addChangeListener(mHistoryViewSliderChangeListener);
	}

	private void removeListeners() {
		mTabbedPane.removeChangeListener(mTabbedPaneChangeListener);
		mHistoryViewForwardButton.removeActionListener(mHistoryViewForwardButtonActionListener);
		mHistoryViewBackwardButton.removeActionListener(mHistoryViewBackwardButtonActionListener);
		mHistoryViewSlider.removeChangeListener(mHistoryViewSliderChangeListener);
	}

	private String createLiveViewTimestampLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<b>Newest timestamp:</b> ");
		sb.append(Long.toString(mNewestTime));
		sb.append(" (<i>");
		sb.append(mDateFormatter.format(new Date(mNewestTime)));
		sb.append("</i>), #");
		sb.append(Integer.toString(mMaximumTimestampIndex));
		sb.append("</html>");
		return sb.toString();
	}

	private String createHistoryViewSelectedTimestampLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<b>Selected timestamp:</b> ");
		sb.append(Long.toString(mHistoryViewSelectedTimestamp));
		sb.append(" (<i>");
		sb.append(mDateFormatter.format(new Date(mHistoryViewSelectedTimestamp)));
		sb.append("</i>), #");
		sb.append(Integer.toString(mHistoryViewSelectedTimestampIndex + 1));
		sb.append(" of ");
		sb.append(Integer.toString(mMaximumTimestampIndex));
		sb.append("</html>");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private Dictionary<Integer, JLabel> createHistoryViewSliderLabelTable() {
		Dictionary<Integer, JLabel> table;
		if (mMaximumTimestampIndex >= 5) {
			table = mHistoryViewSlider.createStandardLabels(5, 5);
		} else {
			table = new Hashtable<Integer, JLabel>();
		}

		table.put(new Integer(mMinimumTimestampIndex + 1), new JLabel(Integer.toString(mMinimumTimestampIndex + 1)));

		if (mMaximumTimestampIndex % 5 != 0) {
			table.put(new Integer(mMaximumTimestampIndex), new JLabel(Integer.toString(mMaximumTimestampIndex)));
		}
		return table;
	}

	private String createDeltaViewSelectedTimestampLabel(int mode) {
		String modeText;
		long timestamp;
		int index;

		if (mode == DELTA_TIMESTAMP_START) {
			modeText = "<b>Start";
			timestamp = mDeltaViewSelectedStartTimestamp;
			index = mDeltaViewSelectedStartTimestampIndex;
		} else {
			modeText = "<b>End";
			timestamp = mDeltaViewSelectedEndTimestamp;
			index = mDeltaViewSelectedEndTimestampIndex;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(modeText);
		sb.append(" timestamp:</b> ");
		sb.append(Long.toString(timestamp));
		sb.append(" (<i>");
		sb.append(mDateFormatter.format(new Date(timestamp)));
		sb.append("</i>), #");
		sb.append(Integer.toString(index + 1));
		sb.append(" of ");
		sb.append(Integer.toString(mMaximumTimestampIndex));
		sb.append("</html>");
		return sb.toString();
	}

	private String createRestUrlLabel(int mode) {
		return "<html> REST url: <a href=\"\">" + createRestCall(mode) + "</a></html>";
	}

	private String createRestCall(int mode) {
		switch (mode) {
		case INDEX_LIVE_VIEW:
			return mRestUrl + "/current";
		case INDEX_HISTORY_VIEW:
			return mRestUrl + "/" + mHistoryViewSelectedTimestamp;
		case INDEX_DELTA_VIEW:
			return mRestUrl + "/" + mDeltaViewSelectedStartTimestamp + "/" + mDeltaViewSelectedEndTimestamp;
		default:
			return null;
		}
	}

	private void registerLinkHandler(JLabel label) {
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String restCall = createRestCall(mCurrentMode);
					if (restCall != null) {
						URI uri = new URI(restCall);
						Desktop.getDesktop().browse(uri );
					}
				} catch (URISyntaxException | IOException ex) {
					//It looks like there's a problem
				}
			}
		});
	}

	private void switchTabEnableState() {
		if (mChangesMapSize == 0) {
			mTabbedPane.setEnabledAt(INDEX_HISTORY_VIEW, false);
			mTabbedPane.setTitleAt(INDEX_HISTORY_VIEW, "<html><font color=gray>History view</font></html>");
			mTabbedPane.setEnabledAt(INDEX_DELTA_VIEW, false);
			mTabbedPane.setTitleAt(INDEX_DELTA_VIEW, "<html><font color=gray>Delta view</font></html>");
		} else {
			mTabbedPane.setEnabledAt(INDEX_HISTORY_VIEW, true);
			mTabbedPane.setTitleAt(INDEX_HISTORY_VIEW, "History view");
			mTabbedPane.setEnabledAt(INDEX_DELTA_VIEW, true);
			mTabbedPane.setTitleAt(INDEX_DELTA_VIEW, "Delta view");
		}
	}

	private void incrementSelectedHistoryTimestamp() {
		if (mHistoryViewSelectedTimestampIndex != INDEX_NOT_INITIALIZED && mHistoryViewSelectedTimestampIndex < (mMaximumTimestampIndex - 1)) {
			mHistoryViewSelectedTimestampIndex++;
			mHistoryViewSelectedTimestamp = findTimestampToIndex(mHistoryViewSelectedTimestampIndex, mChangesMap);
		}
	}

	private void decrementSelectedHistoryTimestamp() {
		if (mHistoryViewSelectedTimestampIndex != INDEX_NOT_INITIALIZED && mHistoryViewSelectedTimestampIndex > mMinimumTimestampIndex) {
			mHistoryViewSelectedTimestampIndex--;
			mHistoryViewSelectedTimestamp = findTimestampToIndex(mHistoryViewSelectedTimestampIndex, mChangesMap);
		}
	}

	private int findIndexToTimestamp(long timestamp, SortedMap<Long,Long> changesMap) {
		int index = 0;

		if (changesMap != null) {
			for (Iterator<Long> iterator = changesMap.keySet().iterator(); iterator.hasNext();) {
				Long key = iterator.next();
				if (key == timestamp) {
					return index;
				}
				index++;
			}
		}

		return 0;
	}

	private long findTimestampToIndex(int index, SortedMap<Long,Long> changesMap) {
		long timestamp = 0l;

		int i = 0;
		if (changesMap != null) {
			for (Iterator<Long> iterator = changesMap.keySet().iterator(); iterator.hasNext();) {
				timestamp = iterator.next();
				if (i == index) {
					return timestamp;
				}
				i++;
			}
		}

		return timestamp;
	}
}
