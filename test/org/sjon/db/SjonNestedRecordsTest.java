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
	public void testNestedRecordsLabeledNamedInNamed() throws IOException, SjonParsingException, SjonScanningException {
		
		// {:{}}
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "inflections.sjon");
		
		SjonRecord record1 = this.vocabularyTable.unique(0, "human");
		assertEquals("{singular:άνθρωπος,plural:άνθρωποι}", record1.getValue("greek"));
		assertEquals("{singular:ihminen,plural:ihmiset}", record1.getValue("finnish"));
		
		SjonRecord record2 = this.vocabularyTable.unique(0, "step");
		assertEquals("{singular:βήμα,plural:βήματα}", record2.getValue("greek"));
		assertEquals("{singular:askel,plural:askelet}", record2.getValue("finnish"));
		
	}
	
	@Test
	public void testNestedRecordsNamedInNamed() throws IOException, SjonParsingException, SjonScanningException {
		
		// {{}}

		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "full_inflections_named.sjon");
		
		SjonRecord record = this.vocabularyTable.unique(0, "askel");
		assertEquals("{singular:askel,plural:askelet}", record.getValue(1));
		
	}
	
	@Test
	public void testNestedRecordsNamedInOrdered() throws IOException, SjonParsingException, SjonScanningException {
		
		// [{}]
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "full_inflections.sjon");
		
		SjonRecord record = this.vocabularyTable.unique(0, "askel");
		
		// 1, 3, 5
		assertEquals("{singular:askel,plural:askelet}", record.getValue(1));
		assertEquals("{singular:askelta,plural:askelia}", record.getValue(3));
		assertEquals("{singular:askelelle,plural:askelille}", record.getValue(5));
		
	}
	
	@Test
	public void testNestedRecordsOrderedInOrdered() throws IOException, SjonParsingException, SjonScanningException {
		
		// [[]]
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "orderedSynonyms.sjon");
		
		SjonRecord record1 = this.vocabularyTable.unique(0, "εγώ");
		assertEquals("I", record1.getValue(1));
		assertEquals("[ma,mina]", record1.getValue(2));
		
		SjonRecord record2 = this.vocabularyTable.unique(0, "εσύ");
		assertEquals("you", record2.getValue(1));
		assertEquals("[sa,sina]", record2.getValue(2));
	}
	
	@Test
	public void testNestedRecordsOrderedInNamed() throws IOException, SjonParsingException, SjonScanningException {
		
		// {[]}
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "synonyms.sjon");
		
		SjonRecord record = this.vocabularyTable.unique(0, "εγώ");
		assertEquals("I", record.getValue(1));
		assertEquals("[ma,mina]", record.getValue(2));
	}
	
	@Test
	public void testNestedRecordsLabeledOrderedInNamed() throws IOException, SjonParsingException, SjonScanningException {
		
		// {:[]}
		
		this.vocabularyTable = new SjonTable(RESOURCES + "/" + "synonyms.sjon");
		
		SjonRecord record = this.vocabularyTable.unique(0, "εσύ");
		assertEquals("you", record.getValue(1));
		assertEquals("[sa,sina]", record.getValue("estonian"));
	}
}
