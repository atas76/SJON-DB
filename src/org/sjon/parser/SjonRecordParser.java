package org.sjon.parser;

import org.sjon.db.SjonRecord;

public class SjonRecordParser {
	
	private final static char NAMED_COLUMN_RECORD_BLOCK_BEGIN = '{';
	private final static char NAMED_COLUMN_RECORD_BLOCK_END = '}';
	
	private final static char ORDERED_COLUMN_RECORD_BLOCK_BEGIN = '[';
	private final static char ORDERED_COLUMN_RECORD_BLOCK_END = ']';
	
	private final static char COLUMN_DELIMITER = ',';
	
	private final static char KV_DELIMITER = ':';
	
	private String rawLine;
	
	public SjonRecordParser(String rawLine) {
		this.rawLine = rawLine;
	}
	
	private SjonRecord record = new SjonRecord();
	
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
			
			if (currentChar != COLUMN_DELIMITER && currentChar != NAMED_COLUMN_RECORD_BLOCK_END) {
				namedColumn.append(currentChar);
			} else {
				// call readNamedColumns again for the remainder of columns)
				if (currentChar == COLUMN_DELIMITER) {
					processNamedColumn(namedColumn.toString());
					return false;
				} else {
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
