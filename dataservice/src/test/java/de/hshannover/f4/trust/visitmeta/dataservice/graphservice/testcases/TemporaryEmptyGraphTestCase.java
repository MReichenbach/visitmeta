/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of visitmeta-dataservice, version 0.4.2,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2015 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.visitmeta.dataservice.graphservice.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.hshannover.f4.trust.visitmeta.interfaces.Delta;
import de.hshannover.f4.trust.visitmeta.interfaces.IdentifierGraph;

public class TemporaryEmptyGraphTestCase extends AbstractTestCase {

	private final String TESTCASE_FILENAME = TESTCASES_DIRECTORY + File.separator + "TemporaryEmptyGraph.yml";

	@Override
	public String getTestcaseFilename() {
		return TESTCASE_FILENAME;
	}

	@Override
	public void getInitialGraph() throws JSONException {
		List<IdentifierGraph> initialGraph = mService.getInitialGraph();
		assertEquals(2, initialGraph.get(0).getIdentifiers().size());

		JSONArray actual = toJson(initialGraph);
		JSONArray expected = new JSONArray();
		{
			JSONObject idPair1 = createJSONIdentifierMetadataConnection("device1", "ip-address1", "device-ip1");
			JSONObject subGraph = createJSON(0l, idPair1);
			expected.put(subGraph);
		}
		assertTrue(jsonsEqual(actual, expected));
	}

	@Override
	public void getGraphAt() throws JSONException {
		getGraphAt0();
		getGraphAt1();
		getGraphAt2();
	}

	private void getGraphAt0() throws JSONException {
		long timestamp = 0;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);
		assertEquals(2, graphAt.get(0).getIdentifiers().size());

		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();
		{
			JSONObject idPair1 = createJSONIdentifierMetadataConnection("device1", "ip-address1", "device-ip1");
			JSONObject subGraph = createJSON(0l, idPair1);
			expected.put(subGraph);
		}
		assertTrue(jsonsEqual(actual, expected));
	}

	private void getGraphAt1() throws JSONException {
		long timestamp = 1;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);
		assertEquals(0, graphAt.size());

		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();
		assertTrue(jsonsEqual(actual, expected));
	}

	private void getGraphAt2() throws JSONException {
		long timestamp = 2;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);
		assertEquals(2, graphAt.get(0).getIdentifiers().size());

		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();
		{
			JSONObject idPair = createJSONIdentifierMetadataConnection("device1", "ip-address1", "device-ip2");
			JSONObject subGraph1 = createJSON(2l, idPair);
			expected.put(subGraph1);
		}
		assertTrue(jsonsEqual(actual, expected));
	}

	@Override
	public void getCurrentGraph() throws JSONException {
		List<IdentifierGraph> currentGraph = mService.getCurrentGraph();
		assertEquals(2, currentGraph.get(0).getIdentifiers().size());

		JSONArray actual = toJson(currentGraph);
		JSONArray expected = new JSONArray();
		{
			JSONObject idPair1 = createJSONIdentifierMetadataConnection("device1", "ip-address1", "device-ip2");
			JSONObject subGraph1 = createJSON(2l, idPair1);
			expected.put(subGraph1);
		}
		assertTrue(jsonsEqual(actual, expected));
	}

	@Override
	public void getDelta() throws JSONException {
		getDelta0to1();
		getDelta1to2();
		getDelta0to2();
	}

	private void getDelta0to1() throws JSONException {
		long t1 = 0;
		long t2 = 1;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> updates = delta.getUpdates();
		List<IdentifierGraph> deletes = delta.getDeletes();

		assertEquals(0, updates.size());
		assertEquals(1, deletes.size());

		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();
		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));

		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();
		{
			JSONObject idPair1 = createJSONIdentifierMetadataConnection("device1", "ip-address1", "device-ip1");
			JSONObject subGraph1 = createJSON(1l, idPair1);
			expectedDeletes.put(subGraph1);
		}
		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));
	}

	private void getDelta1to2() throws JSONException {
		long t1 = 1;
		long t2 = 2;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> updates = delta.getUpdates();
		List<IdentifierGraph> deletes = delta.getDeletes();

		assertEquals(1, updates.size());
		assertEquals(0, deletes.size());

		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();
		{
			JSONObject idPair1 = createJSONIdentifierMetadataConnection("device1", "ip-address1", "device-ip1");
			JSONObject subGraph1 = createJSON(2l, idPair1);
			expectedUpdates.put(subGraph1);
		}
		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));

		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();
		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));
	}

	private void getDelta0to2() throws JSONException {
		long t1 = 0;
		long t2 = 2;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> updates = delta.getUpdates();
		List<IdentifierGraph> deletes = delta.getDeletes();
		assertEquals(0, updates.size());
		assertEquals(0, deletes.size());

		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();
		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));

		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();
		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));
	}

	@Override
	public void getChangesMap() throws JSONException {
		SortedMap<Long, Long> changesMap = mService.getChangesMap();
		SortedMap<Long, Long> expected = new TreeMap<Long, Long>();
		expected.put(0l, 1l);
		expected.put(1l, 1l);
		expected.put(2l, 1l);

		testChangesMap(expected, changesMap);
		testChangesMapJSON(expected, changesMap);
	}

}
