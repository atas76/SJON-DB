package org.sjon.db;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.sjon.parser.SjonParsingException;
import org.sjon.parser.SjonScanningException;

public class SjonNestedRecordsTest {
	
	private SjonTable vocabularyTable;
	
	private static final String RESOURCES = "./resources";
	
	@Test
	public void testNestedRecordsOrderedType() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "orderedSynonyms.sjon");
		
		SjonRecord record1 = this.vocabularyTable.unique(0, "εγώ");
		assertEquals("I", record1.getValue(1));
		assertEquals("[ma,mina]", record1.getValue(2));
		
		SjonRecord record2 = this.vocabularyTable.unique(0, "εσύ");
		assertEquals("you", record2.getValue(1));
		assertEquals("[sa,sina]", record2.getValue(2));
	}
	
	@Test
	public void testNestedRecordsOrdered() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "synonyms.sjon");
		
		SjonRecord record = this.vocabularyTable.unique(0, "εγώ");
		assertEquals("I", record.getValue(1));
		assertEquals("[ma,mina]", record.getValue(2));
	}
	
	@Test
	public void testNestedRecordsNamed() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "synonyms.sjon");
		
		SjonRecord record = this.vocabularyTable.unique(0, "εσύ");
		assertEquals("you", record.getValue(1));
		assertEquals("[sa,sina]", record.getValue("estonian"));
	}
}
