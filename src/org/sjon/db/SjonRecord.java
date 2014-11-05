package org.sjon.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Representation of a record (column group). Columns can be referenced either by index or name.
 * 
 * @author Andreas Tasoulas
 *
 */
public class SjonRecord {
	
	private List<String> orderedValues = new ArrayList<String>();
	private Map<String, String> values = new HashMap<String, String>();
	
	/**
	 * 
	 * Get the value of a column by name
	 * 
	 * @param fieldName the name of the column
	 * @return The value of the column. Null if a column with the requested name does not exist.
	 */
	public String getValue(String fieldName) {
		return this.values.get(fieldName);
	}
	
	/**
	 * 
	 * Get the value of a column by index
	 * 
	 * @param index The index of the column based a predefined order within the column group. 
	 * @return The value of the column. Null if the index is out of bounds.
	 */
	public String getValue(int index) {
		
		if (index > this.orderedValues.size() - 1) 
			return null;
		
		return this.orderedValues.get(index);
	}
	
	/**
	 * 
	 * Adds a value to the record
	 * 
	 * Values can be either ordered or named. 
	 * By default all values are added as ordered, besides being added as named ones, matched with a specific key (corresponding to the column name).
	 * 
	 * @param key The column name of the value added. If null the value is referenced only by its order.
	 * @param value The value added. Identified by its order from an index or from its column name, if available.
	 */
	public void addValue(String key, String value) {
		orderedValues.add(value);
		if (key != null) {
			this.values.put(key, value);
		}
	}
	
	public List<String> getOrderedValues() {
		return this.orderedValues;
	}
	
	public Collection<String> getFieldNames() {
		return this.values.keySet();
	}
}
