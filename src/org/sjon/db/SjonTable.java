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

/**
 * 
 * The equivalent of a set of column groups or a relational table.
 * 
 * It is loaded from a Unicode text file.
 * 
 * @author Andreas Tasoulas
 *
 */

public class SjonTable {
	
	private List<String> rawContent = new ArrayList<String>();
	private List<SjonRecord> records = null;
	
	/**
	 * 
	 * Loads the SJON data from a text file. 
	 * 
	 * Its functionality has more of a utility nature; loading the data without performing any validations. 
	 * 
	 * @param path A Unicode text file, containing the data in SJON format. The first line should start with the '#' character (commented-out, reserved for metadata)
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
	
	/**
	 * 
	 * Creates the SjonRecord objects (column groups) and loads them. 
	 * 
	 * Validation is also performed, by parsing each column group representation.
	 * 
	 * @return the list of SjonRecord objects, constructed from the contents of the data source specified in the constructor
	 * @throws SjonParsingException thrown when a parsing error occurs in reading the representation of a column group
	 * @throws SjonScanningException thrown when a scanning error occurs in reading the representation of a column group
	 */
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
	
	/**
	 * 
	 * Getting a unique SjonRecord object filtered by a value and its order within the column group
	 * 
	 * @param index the (zero-based) order of the filtering value within the column group
	 * @param value the filtering value 
	 * @return the first SjonRecord object having a value equal to the filtering one, in the order specified
	 * @throws SjonParsingException thrown in case of parsing error in initialization of the SjonRecord objects
	 * @throws SjonScanningException thrown in case of lexical analysis error in initialization of the SjonRecord objects
	 */
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
	
	/**
	 * 
	 * Getting a unique SjonRecord object filtered by a value and its column name within the column group
	 * 
	 * @param fieldName the column name of the filtering value within the column group
	 * @param value the filtering value 
	 * @return the first SjonRecord object having a value equal to the filtering one, for the column specified by its name
	 * @throws SjonParsingException thrown in case of parsing error in initialization of the SjonRecord objects
	 * @throws SjonScanningException thrown in case of lexical analysis error in initialization of the SjonRecord objects
	 */
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
