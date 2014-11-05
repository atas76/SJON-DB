package org.sjon.db;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sjon.parser.SjonRecordParser;

public class SjonRecordTest {
	
	private SjonRecord sjonRecord;
	private String rawRecord = "{estonian:saame,english:we get,greek:παίρνουμε}";
	
	@Before
	public void setUp() throws Exception {
		this.sjonRecord = new SjonRecordParser(this.rawRecord).parse();
	}
	
	@Test
	public void testGetValue() {
		assertEquals("we get", this.sjonRecord.getValue(1)); // Simple test for a valid value
		assertNull(this.sjonRecord.getValue(3)); // Null value in case index is out of bounds.
	}
}
