package de.hshannover.f4.trust.visitmeta.network;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.f4.trust.visitmeta.datawrapper.ConfigParameter;
import de.hshannover.f4.trust.visitmeta.datawrapper.PropertiesManager;
import de.hshannover.f4.trust.visitmeta.gui.util.DataserviceConnection;

public class DataserviceConnectionFactory {


	public static List<DataserviceConnection> getDataserviceConnectionsFromProperties(){
		ArrayList<DataserviceConnection> dataserviceList = new ArrayList<DataserviceConnection>();
		int connectionCount = Integer.valueOf(PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, "0"));

		for(int i=1; i<=connectionCount; i++){
			String name = PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_NAME(i), "localhost");
			String url = PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_URL(i), "http://localhost:8000");
			boolean rawXml = Boolean.valueOf(PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_RAWXML(i), "true").toLowerCase());

			DataserviceConnection tmpConnection = new DataserviceConnection(name, url, rawXml);
			tmpConnection.setProtertyIndex(i);

			dataserviceList.add(tmpConnection);
		}
		return dataserviceList;
	}

	public static void saveDataServiceConnection(DataserviceConnection dataService){
		if(dataService.getProtertyIndex() == 0){
			int currentCountFromProperty = Integer.valueOf(PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, "0"));
			dataService.setProtertyIndex(currentCountFromProperty + 1);
			PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, String.valueOf(currentCountFromProperty + 1));
		}

		int protertyIndex = dataService.getProtertyIndex();

		PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_NAME(protertyIndex), dataService.getName());
		PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_URL(protertyIndex), dataService.getUrl());
		PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_RAWXML(protertyIndex), String.valueOf(dataService.isRawXml()));
	}

	//das zum löschen umschreiben
	public static void persistDataServiceConnections(List<DataserviceConnection> listModelDataService){
		int propertyCount = Integer.valueOf(PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, "0"));
		for(int i=0; i<propertyCount; i++){
			removeDataServiceConnection(i);
		}

		int count = listModelDataService.size();
		PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, String.valueOf(count));

		for(int i=0; i<count; i++){
			PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_NAME(i), listModelDataService.get(i).getName());
			PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_URL(i), listModelDataService.get(i).getUrl());
			PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_RAWXML(i), String.valueOf(listModelDataService.get(i).isRawXml()));
		}
	}

	public static void removeDataServiceConnection(int index){
		int currentCountFromProperty = Integer.valueOf(PropertiesManager.getProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, "0"));
		if(index == currentCountFromProperty){
			PropertiesManager.removeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_NAME(index));
			PropertiesManager.removeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_URL(index));
			PropertiesManager.removeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT_RAWXML(index));
			PropertiesManager.storeProperty("application", ConfigParameter.VISUALIZATION_USER_CONNECTION_DATASERVICE_COUNT, String.valueOf((currentCountFromProperty == 0) ? 0 : currentCountFromProperty - 1));
		}else{

		}
	}
}
