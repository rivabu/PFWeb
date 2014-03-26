package org.rients.com.constants;

import java.util.Map;
import java.util.TreeMap;

public class SimpleCache {
	private static SimpleCache instance = null;
    private Map<String, Object> data = new TreeMap<String, Object>();


	protected SimpleCache() {
		// Exists only to defeat instantiation.
	}

	public static SimpleCache getInstance() {
		if (instance == null) {
			instance = new SimpleCache();
		}
		return instance;
	}
	
	public boolean doesKeyExist(String key) {
		return data.containsKey(key);
	}
	
	public void addObject(String key, Object object) {
		//System.out.println("add object: " + key);
		data.put(key, object);
	}
	
	public Object getObject(String key) {
		//System.out.println("get object: " + key);
		if (data.containsKey(key)) {
			return data.get(key);
		}
		return null;
	}

}
