package de.hshannover.f4.trust.visitmeta.interfaces.data;

import java.util.List;

public interface DataserviceData extends Data {

	@Override
	public String getName();

	@Override
	public void setName(String name);

	public String getUrl();

	public void setUrl(String url);

	public boolean isRawXml();

	public void setRawXml(boolean rawXml);

	public void addMapServerData(MapServerData connection);

	public void setMapServerData(List<MapServerData> connection);

	public List<MapServerData> getMapServerData();

	public void removeMapServerData(MapServerData connection);

	public void removeMapServerData(int index);

	/**
	 * Only the connection properties. For MapServerConnections use MapServerConnectionData.changeData().
	 * 
	 * @param newData
	 */
	public void changeData(DataserviceData newData);

	@Override
	public DataserviceData copy();

	@Override
	public DataserviceData clone();
}
