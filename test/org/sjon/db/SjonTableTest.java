package org.sjon.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.sjon.parser.SjonParsingException;
import org.sjon.parser.SjonScanningException;

public class SjonTableTest {
	
	private SjonTable vocabularyTable;
	
	private static final String RESOURCES = "./resources";
	
	@Test
	public void testHybridFieldsRowsTable() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "hybridRows.sjon");
		
		SjonRecord workNamed = this.vocabularyTable.unique("estonian", "töö");
		assertNull(workNamed);
		
		SjonRecord work = this.vocabularyTable.unique(0, "töö");
		assertEquals("work", work.getValue(1));
		assertEquals("δουλειά", work.getValue(2));
		
		SjonRecord when = this.vocabularyTable.unique("english", "when");
		assertEquals("kuna", when.getValue("estonian"));
		assertEquals("πότε", when.getValue("greek"));
		
		SjonRecord again = this.vocabularyTable.unique("greek", "πάλι");
		assertEquals("taas", again.getValue(0));
		assertEquals("again", again.getValue(1));
	}
	
	@Test
	public void testOrderedFieldsRowsTable() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "orderedRows.sjon");
		
		SjonRecord workNamed = this.vocabularyTable.unique("estonian", "töö");
		assertNull(workNamed);
		
		SjonRecord work = this.vocabularyTable.unique(0, "töö");
		assertEquals("work", work.getValue(1));
		assertEquals("δουλειά", work.getValue(2));
		
		SjonRecord when = this.vocabularyTable.unique(1, "when");
		assertEquals("kuna", when.getValue(0));
		assertEquals("πότε", when.getValue(2));
		
		SjonRecord again = this.vocabularyTable.unique(2, "πάλι");
		assertEquals("taas", again.getValue(0));
		assertEquals("again", again.getValue(1));
	}
	
	@Test
	public void testNamedFieldsRowsTable() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "typedRows.sjon");
		
		// Search by Estonian term
		SjonRecord work = this.vocabularyTable.unique("estonian", "töö");
		assertEquals("work", work.getValue("english"));
		assertEquals("δουλειά", work.getValue("greek"));
		
		// Search by English term
		SjonRecord when = this.vocabularyTable.unique("english", "when");
		assertEquals("kuna", when.getValue("estonian"));
		assertEquals("πότε", when.getValue("greek"));
		
		// Search by Greek term
		SjonRecord again = this.vocabularyTable.unique("greek", "πάλι");
		assertEquals("taas", again.getValue("estonian"));
		assertEquals("again", again.getValue("english"));
	}
	
	@Test
	public void testHybridRecordsTable() throws IOException, SjonParsingException, SjonScanningException {
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "hybridRecords.sjon");
		
		SjonRecord work = this.vocabularyTable.unique(0, "töö");
		assertEquals("work", work.getValue(1));
		assertEquals("δουλειά", work.getValue(2));
		
		// Search by English term
		SjonRecord when = this.vocabularyTable.unique("english", "when");
		assertEquals("kuna", when.getValue("estonian"));
		assertEquals("πότε", when.getValue("greek"));
				
		// Search by Greek term
		SjonRecord again = this.vocabularyTable.unique("greek", "πάλι");
		assertEquals("taas", again.getValue(0));
		assertEquals("again", again.getValue(1));
		
		// Search again by index
		SjonRecord againByIndex = this.vocabularyTable.unique(0, "taas");
		assertEquals("again", againByIndex.getValue(1));
		assertEquals("πάλι", againByIndex.getValue("greek"));
		
		
	}
}
