package org.sjon.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.sjon.parser.SjonParsingException;
import org.sjon.parser.SjonRecordParser;
import org.sjon.parser.SjonScanningException;

public class SjonTable {
	
	private List<String> rawContent = new ArrayList<String>();
	private List<SjonRecord> records = null;
	
	public SjonTable(String path) throws FileNotFoundException, IOException {
		
		File file = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String line = br.readLine();
		// System.out.println(line);
		// br.skip(1); // skip BOM
		while ((line = br.readLine()) != null) {
			rawContent.add(line);
		}
		br.close();
	}
	
	public List<SjonRecord> getData() throws SjonParsingException, SjonScanningException {

		if (records == null) {
			
			this.records = new ArrayList<SjonRecord>();
			
			for (String rawLine: rawContent) {
				SjonRecordParser parser = new SjonRecordParser(rawLine);
				SjonRecord currentRecord = parser.parse();
				if (currentRecord != null) { // Valid record; null would mean valid SJON line (for example: blank line, comment line, etc.), otherwise throw exception
					records.add(currentRecord);
				}
			}
		}
		return this.records;
	}
	
	public SjonRecord unique(int index, String value) throws SjonParsingException, SjonScanningException {
		
		List<SjonRecord> records = this.getData();
		
		if (value == null) {
			return null;
		}
		
		for (SjonRecord record:records) {
			String currentValue = record.getValue(index);
			if (value.equals(currentValue)) {
				return record;
			}
		}
		return null;
	}
	
	public SjonRecord unique(String fieldName, String value) throws SjonParsingException, SjonScanningException {
		
		List<SjonRecord> records = this.getData();
		
		if (value == null) {
			return null;
		}
		
		for (SjonRecord record:records) {
			String currentValue = record.getValue(fieldName);
			if (value.equals(currentValue)) {
				return record;
			}
		}
		return null;
	}
}
