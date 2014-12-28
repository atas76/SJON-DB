package org.sjon.parser;

import org.sjon.db.SjonRecord;

/**
 * 
 * Parser of an SJON 'record', equivalent to a set of column-value pairs. In essence, it marshals a string representation of a record to an equivalent object 
 * 
 * @author Andreas Tasoulas
 *
 */

public class SjonRecordParser {
	
	private final static char NAMED_COLUMN_RECORD_BLOCK_BEGIN = '{';
	private final static char NAMED_COLUMN_RECORD_BLOCK_END = '}';
	
	private final static char ORDERED_COLUMN_RECORD_BLOCK_BEGIN = '[';
	private final static char ORDERED_COLUMN_RECORD_BLOCK_END = ']';
	
	private final static char COLUMN_DELIMITER = ',';
	
	private final static char KV_DELIMITER = ':';
	
	private String rawLine;
	
	/**
	 * 
	 * Loads an SJON file line (an SJON record by convention), for processing and conversion to an SjonRecord object
	 * 
	 * @param rawLine the string representation of an SJON record
	 */
	public SjonRecordParser(String rawLine) {
		this.rawLine = rawLine;
	}
	
	private SjonRecord record = new SjonRecord();
	
	/**
	 * 
	 * Marshals the SJON record string representation loaded in the constructor to an SjonRecord object 
	 * 
	 * @return an SjonRecord object
	 * @throws SjonParsingException a parsing error has occurred
	 * @throws SjonScanningException a lexical analysis error has occurred
	 */
	public SjonRecord parse() throws SjonParsingException, SjonScanningException {
		
		char [] lineChars = new char[rawLine.length()]; 

		rawLine.getChars(0, rawLine.length(), lineChars, 0);
		
		SjonScanner currentRowScanner = new SjonScanner(lineChars);
		
		boolean namedColumnRecord = currentRowScanner.check(SjonRecordParser.NAMED_COLUMN_RECORD_BLOCK_BEGIN);
		boolean orderedColumnRecord = currentRowScanner.check(SjonRecordParser.ORDERED_COLUMN_RECORD_BLOCK_BEGIN);
		
		if (namedColumnRecord) {
			currentRowScanner.read(SjonRecordParser.NAMED_COLUMN_RECORD_BLOCK_BEGIN);
			boolean lastColumn = false;
			while (!lastColumn) {
				lastColumn = readNextColumn(currentRowScanner);
			}
		} else if (orderedColumnRecord) {
			currentRowScanner.read(SjonRecordParser.ORDERED_COLUMN_RECORD_BLOCK_BEGIN);
			boolean lastOrderedColumn = false;
			while (!lastOrderedColumn) {
				lastOrderedColumn = readNextOrderedColumn(currentRowScanner);
			}
		} else {
			throw new SjonParsingException(SjonParsingException.Cause.SYNTAX_ERROR);
		}
		
		return this.record;
	}
	
	private boolean readNextOrderedColumn(SjonScanner currentRowScanner) throws SjonParsingException {
		
		char currentChar = '\0';
		StringBuilder orderedColumn = new StringBuilder();
		
		while(true) {
			
			try {
				currentChar = currentRowScanner.readNext();
			} catch (ArrayIndexOutOfBoundsException aiobex) {
				throw new SjonParsingException(SjonParsingException.Cause.SYNTAX_ERROR);
			}
			
			if (currentChar == ORDERED_COLUMN_RECORD_BLOCK_BEGIN) {
				
				orderedColumn.append(currentChar);
				
				while (currentChar != ORDERED_COLUMN_RECORD_BLOCK_END) { // Normally it should include the field name parsed previously
					currentChar = currentRowScanner.readNext();
					orderedColumn.append(currentChar);
				}
				// processOrderedColumn(orderedColumn.toString());
				// return false;
				
				try { // Resume one level up
					currentChar = currentRowScanner.readNext();
				} catch (ArrayIndexOutOfBoundsException aiobex) {
					throw new SjonParsingException(SjonParsingException.Cause.SYNTAX_ERROR);
				}
			}
			
			if (currentChar == NAMED_COLUMN_RECORD_BLOCK_BEGIN) {
				
				orderedColumn.append(currentChar);
				
				while (currentChar != NAMED_COLUMN_RECORD_BLOCK_END) {
					currentChar = currentRowScanner.readNext();
					orderedColumn.append(currentChar);
				}
				
				// System.out.println("Named column in ordered record: " + orderedColumn.toString());
				
				// processOrderedColumn(orderedColumn.toString());
				// return false;
				
				try { // Resume one level up
					currentChar = currentRowScanner.readNext();
				} catch (ArrayIndexOutOfBoundsException aiobex) {
					throw new SjonParsingException(SjonParsingException.Cause.SYNTAX_ERROR);
				}
			}
			
			if (currentChar != COLUMN_DELIMITER && currentChar != ORDERED_COLUMN_RECORD_BLOCK_END) {
				orderedColumn.append(currentChar);
			} else {
				// call readNamedColumns again for the remainder of columns)
				if (currentChar == COLUMN_DELIMITER) {
					processOrderedColumn(orderedColumn.toString());
					// currentRowScanner.readNext();
					return false;
				} else {
					processOrderedColumn(orderedColumn.toString());
					return true;
				}
			}
		}
	}
	
	private boolean readNextColumn(SjonScanner currentRowScanner) throws SjonParsingException {
		
		char currentChar = '\0';
		
		StringBuilder namedColumn = new StringBuilder();
		
		while(true) {
			
			try {
				currentChar = currentRowScanner.readNext();
			} catch (ArrayIndexOutOfBoundsException aiobex) {
				throw new SjonParsingException(SjonParsingException.Cause.SYNTAX_ERROR);
			}
			
			if (currentChar == ORDERED_COLUMN_RECORD_BLOCK_BEGIN) {
				
				namedColumn.append(currentChar);
				
				while(currentChar != ORDERED_COLUMN_RECORD_BLOCK_END) { // Normally it should include the field name parsed previously
					currentChar = currentRowScanner.readNext();
					namedColumn.append(currentChar);
				}
		
				processNamedColumn(namedColumn.toString());
				return false;
			}
			
			if (currentChar == NAMED_COLUMN_RECORD_BLOCK_BEGIN) {
				
				namedColumn.append(currentChar);
				
				while(currentChar != NAMED_COLUMN_RECORD_BLOCK_END) { // Normally it should include the field name parsed previously
					currentChar = currentRowScanner.readNext();
					namedColumn.append(currentChar);
				}
		
				processNamedColumn(namedColumn.toString());
				return false;
			}
			
			if (currentChar != COLUMN_DELIMITER && currentChar != NAMED_COLUMN_RECORD_BLOCK_END) {
				namedColumn.append(currentChar);
			} else {
				// call readNamedColumns again for the remainder of columns
				if (currentChar == COLUMN_DELIMITER) {
					processNamedColumn(namedColumn.toString());
					return false;
				} else if (currentChar == NAMED_COLUMN_RECORD_BLOCK_END){
					processNamedColumn(namedColumn.toString());
					return true;
				}
			}
		}
	}
	
	private void processOrderedColumn(String orderedColumn) {
		this.record.addValue(null, orderedColumn);
	}
	
	private void processNamedColumn(String namedColumn) {
		
		int separatorIndex = namedColumn.indexOf(KV_DELIMITER);
		String key = null;
		String value = "";
		
		if (separatorIndex == -1) { // The column is unnamed
			 value = namedColumn;
		} else {
			key = namedColumn.substring(0, separatorIndex);
			value = namedColumn.substring(separatorIndex + 1);
		}
		this.record.addValue(key, value);
	}
}
