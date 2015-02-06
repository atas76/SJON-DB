package org.sjon.util.domains.sports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ScoreConverter {
	
	public static String toSjon(String score) {
		
		StringBuilder sjonScore = new StringBuilder();
		
		sjonScore.append("{");
		
		// Strip whitespace from score
		score = score.replaceAll("\\s+"," ");
		// score = score.replaceAll("\\-", ",");
		
		String homeTeamName = score.substring(0, score.indexOf("-")).trim();
		String awayTeamName = score.substring(score.indexOf("-") + 1, score.length() - 3).trim();
		String scoreSubstring = score.substring(score.length() - 3);
		
		sjonScore.append(homeTeamName);
		sjonScore.append(",");
		sjonScore.append(awayTeamName);
		sjonScore.append(",");
		sjonScore.append(scoreSubstring.replace("-", ","));
		
		// sjonScore.append(score);
		
		sjonScore.append("}");
		
		return sjonScore.toString();
	}
	
	public static String toSjon(File file) {
		
		StringBuilder scores = new StringBuilder();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String currentScore;
			
			while ((currentScore = br.readLine()) != null) {
				scores.append(toSjon(currentScore));
				scores.append("\n");
			}
			
			br.close();
			
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		
		return scores.toString();
	}
}
