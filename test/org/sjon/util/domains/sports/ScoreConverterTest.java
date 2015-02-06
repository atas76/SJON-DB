package org.sjon.util.domains.sports;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class ScoreConverterTest {
	
	private String score1;
	private String score2;
	
	private File scoresFile;
	
	@Before
	public void setUp() {
		score1 = "Young Boys - Basel 0-1";
		score2 = "Young Boys - Basel					0-1";
	}
	
	@Test
	public void testSingleScoreConversion() {
		assertEquals("{Young Boys,Basel,0,1}", ScoreConverter.toSjon(score1));
		assertEquals("{Young Boys,Basel,0,1}", ScoreConverter.toSjon(score2));
	}
	
	@Test
	public void testScoreConversionFromFile() {
		
		this.scoresFile = new File("resources/scores.txt");
		
		// Just a rough check
		System.out.println(ScoreConverter.toSjon(this.scoresFile));
	}
}
