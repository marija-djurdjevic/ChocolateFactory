package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.Factory;
import beans.Location;

public class LocationDAO {
private ArrayList<Location> locations = new ArrayList<>();
	
	public LocationDAO(String contextPath) {
		loadLocations(contextPath);
		}

	public ArrayList<Location> findAll() {
		return locations;
	}

	public Location findLocation(int id) {
		for (Location location : locations) {
			if (location.getId() == id) {
				return location;
			}
		}
		return null;
	}
	
	public Location updateLocation(int id, Location location) {
		Location l = findLocation(id);
		if (l == null) {
			return save(location);
		} else {
			l.setLatitude(location.getLatitude());
			l.setLongitude(location.getLongitude());
			l.setAddress(location.getAddress());
			return l;
		}
	}
	
	public Location save(Location location) {
		int maxId = -1;
		for (Location l : locations) {
			if (l.getId() > maxId) {
				maxId = l.getId();
			}
		}
		maxId++;
		location.setId(maxId);
		locations.add(location);
		return location;
	}
	
	public Location deleteLocationById(int id) {
        Location locationToRemove = null;
        for (Location location : locations) {
            if (location.getId() == id) {
                locationToRemove = location;
                break;
            }
        }
        if (locationToRemove != null) {
            locations.remove(locationToRemove);
        }
        return locationToRemove;
    }

	private void loadLocations(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/locations.txt");
			System.out.println(file.getCanonicalPath());
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				String latitude = st.nextToken().trim();
				String longitude = st.nextToken().trim();
				String address = st.nextToken().trim();
				locations.add(new Location(id, latitude, longitude, address));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
