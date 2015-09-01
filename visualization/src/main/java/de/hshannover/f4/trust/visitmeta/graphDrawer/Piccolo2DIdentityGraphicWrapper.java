package de.hshannover.f4.trust.visitmeta.graphDrawer;

import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hshannover.f4.trust.visitmeta.interfaces.Propable;
import de.hshannover.f4.trust.visitmeta.util.DocumentUtils;

public class Piccolo2DIdentityGraphicWrapper extends Piccolo2DGraphicWrapper implements IdentityGraphicWrapper {

	private static final String ELEMENT_ID_NAME = "name";

	public Piccolo2DIdentityGraphicWrapper(PPath node, PText text) {
		super(node, text);
	}

	/**
	 * For extendet-Identity don't deEscapeXml().
	 */
	@Override
	protected Document getDocument() {
		Object data = getData();

		if (data instanceof Propable) {
			Propable propable = (Propable) data;

			return DocumentUtils.parseXmlString(propable.getRawData());
		}
		return null;
	}

	@Override
	public String getName() {
		Element rootElement = super.getRootElement();

		String name = rootElement.getAttribute(ELEMENT_ID_NAME);

		return name;
	}

	@Override
	public String getExtendetNodeTypeName() {
		String nameValue = getName();
		
		if (nameValue == null) {
			return null;
		}

		Document extendetIdentifier = DocumentUtils.parseEscapedXmlString(nameValue);

		if (extendetIdentifier != null) {
			Element rootElement = extendetIdentifier.getDocumentElement();
			return rootElement.getNodeName();
		}
		return null;
	}
}
