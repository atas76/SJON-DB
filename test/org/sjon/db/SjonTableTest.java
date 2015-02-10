package org.sjon.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.sjon.parser.SjonParsingException;
import org.sjon.parser.SjonScanningException;

public class SjonTableTest {
	
	private SjonTable vocabularyTable;
	
	private static final String RESOURCES = "./resources";
	
	@Test
	public void testProjection() throws IOException, SjonParsingException, SjonScanningException {
		
		SjonTable ranking = new SjonTable(RESOURCES + "/" + "ranking_europe.sjon");
		
		Set<String> teams = ranking.projectColumn(1);
		
		assertEquals(74, teams.size());
		
		assertTrue(teams.contains("Viktoria Plzen"));
		assertTrue(teams.contains("Stuttgart"));
		assertFalse(teams.contains("Boca Juniors"));
	}
	
	@Test
	public void testOrderedProjection() throws IOException, SjonParsingException, SjonScanningException {
		
		SjonTable ranking = new SjonTable(RESOURCES + "/" + "ranking_europe.sjon");
		
		List<String> teams = ranking.projectOrderedColumn(1);
		
		assertEquals(74, teams.size());
		
		assertEquals("Viktoria Plzen", teams.get(44));
		assertEquals("Stuttgart", teams.get(58));
		
	}
	
	@Test
	public void testJoinFilterAnd() throws IOException, SjonParsingException, SjonScanningException {
		
		SjonTable scores = new SjonTable(RESOURCES + "/" + "scores.sjon");
		
		
		
	}
	
	@Test
	public void testJoinFilterOr() throws IOException, SjonParsingException, SjonScanningException {
		
		SjonTable scores = new SjonTable(RESOURCES + "/" + "scores.sjon");
		
		Set<String> values = new HashSet<String>();
		List<Integer> fieldIndices = new ArrayList<Integer>();
		
		values.add("Villarreal");
		values.add("Malaga");
		values.add("Levante");
		values.add("Atletico Madrid");
		values.add("Real Madrid");
		
		fieldIndices.add(0);
		fieldIndices.add(1);
		
		SjonTable filteredScores = scores.joinFilterOr(values, fieldIndices);
		
		assertEquals(5, filteredScores.getData().size());
		
	}
	
	@Test
	public void testMultipleFieldsUniqueSelection() throws IOException, SjonParsingException, SjonScanningException {
		
		SjonTable results = new SjonTable(RESOURCES + "/" + "results.sjon");
		
		SjonRecord olyMalm = results.unique(new String [] {"homeTeam", "awayTeam"}, new String [] {"Olympiakos", "Malmo FF"});
		SjonRecord dorAnder = results.unique(new String [] {"homeTeam", "awayTeam"}, new String [] {"Dortmund", "Anderlecht"});
		SjonRecord benAtl = results.unique(new String [] {"homeTeam", "awayTeam"}, new String [] {"Benfica", "Atletico Madrid"});
		
		assertEquals("4", olyMalm.getValue(2));
		assertEquals("2", olyMalm.getValue(3));
		
		assertEquals("1", dorAnder.getValue(2));
		assertEquals("1", dorAnder.getValue(3));
		
		assertNull(benAtl);
	}
	
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
