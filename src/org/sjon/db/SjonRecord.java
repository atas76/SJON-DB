package org.sjon.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SjonRecord {
	
	private List<String> orderedValues = new ArrayList<String>();
	private Map<String, String> values = new HashMap<String, String>();
	
	public String getValue(String fieldName) {
		return this.values.get(fieldName);
	}
	
	public String getValue(int index) {
		return this.orderedValues.get(index);
	}
	
	public void addValue(String key, String value) {
		orderedValues.add(value);
		if (key != null) {
			this.values.put(key, value);
		}
	}
}
