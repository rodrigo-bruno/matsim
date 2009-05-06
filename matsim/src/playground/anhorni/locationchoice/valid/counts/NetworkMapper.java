package playground.anhorni.locationchoice.valid.counts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.utils.geometry.CoordImpl;

public class NetworkMapper {
	
	private final static Logger log = Logger.getLogger(NetworkMapper.class);
	private List<CountStation> countStations = new Vector<CountStation>();
	
	public void map(TreeMap<String, Vector<RawCount>> rawCounts, final String mappingFile) {
		
		TreeMap<String, CountStation> stationsTree = new TreeMap<String, CountStation>();
		this.readMappingFile(mappingFile, stationsTree);
		
		Iterator<Vector<RawCount>> station_it = rawCounts.values().iterator();
		while (station_it.hasNext()) {
			Vector<RawCount>  stationCounts = station_it.next();
			Iterator<RawCount> rawCount_it = stationCounts.iterator();
			while (rawCount_it.hasNext()) {
				RawCount rawCount = rawCount_it.next();
				CountStation station = stationsTree.get(rawCount.getId());
								
				if (station != null) {
					station.addCount(rawCount);
				}	
			}
		}				
		this.removeEmptyStations(stationsTree);	
	}
	
	private void removeEmptyStations(TreeMap<String, CountStation> stationsTree) {
		Iterator<CountStation> station_it = stationsTree.values().iterator();
		while (station_it.hasNext()) {
			CountStation  station = station_it.next();
			
			if (station.getCounts().size() > 0) {
				countStations.add(station);
			}
		}
	}
	
	private void readMappingFile(final String mappingFile, TreeMap<String, CountStation> stationsTree) {
		
		try {
			FileReader fileReader = new FileReader(mappingFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String curr_line = bufferedReader.readLine(); // Skip header
			while ((curr_line = bufferedReader.readLine()) != null) {
				
				String[] entries = curr_line.split("\t", -1);											
				String dataset = entries[0].trim();
							
				String nr = entries[1].trim();
				if (nr.length() == 1) {
					nr = "00" + nr;
				}
				else if (nr.length()==2) {
					nr = "0" + nr;
				}
				
				String direction = entries[2].trim();
				String linkidTeleatlas = entries[4].trim();
				String linkidNavteq = entries[5].trim();
				String linkidAre = entries[6].trim();
				String linkidIVTCH = entries[7].trim();
				
				String xs = entries[8].trim();
				String ys = entries[9].trim();
				
				if (xs.equals("-")) xs = "-1";
				if (ys.equals("-")) ys = "-1";
				CoordImpl coord = new CoordImpl(xs, ys);
				
				LinkInfo link = new LinkInfo(direction, linkidTeleatlas, linkidNavteq, linkidAre, linkidIVTCH);

				if (!stationsTree.containsKey(dataset + nr)) {
					CountStation station = new CountStation(dataset + nr, coord);
					station.setLink0(link);
					stationsTree.put(dataset + nr, station);
				}
				else {
					stationsTree.get(dataset + nr).setLink1(link);
				}
				
			}	
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {
			Gbl.errorMsg(e);
		}
	}

	public List<CountStation> getCountStations() {
		return countStations;
	}

	public void setCountStations(List<CountStation> countStations) {
		this.countStations = countStations;
	}
}
