package org.sjon.parser;

public class SjonScanner {
	
	private char [] lineChars;
	private int index;
	
	public SjonScanner(char [] lineChars) {
		this.lineChars = lineChars;
	}
	
	public boolean check(char c) {
		
		boolean checked = false;
		
		for (int i = index; i < lineChars.length; i++) {
			if (Character.isWhitespace(lineChars[i])) {
				index++; // We advance the index only for whitespace 
				continue;
			}
			if (lineChars[i] == c) {
				checked = true;
			} else {
				checked = false;
			}
			break;
		}
		return checked;
	}
	
	public int read(char c) throws SjonScanningException {
		for (int i = index; i < lineChars.length; i++, index++) {
			if (Character.isWhitespace(lineChars[i])) continue;
			if (lineChars[i] == c) {
				index++;
				return i + 1;
			} else {
				throw new SjonScanningException();
			}
		}
		throw new SjonScanningException();
	}
	
	public char readNext() {
		char nextChar = this.lineChars[this.index];
		this.index++;
		return nextChar;
	}
}
