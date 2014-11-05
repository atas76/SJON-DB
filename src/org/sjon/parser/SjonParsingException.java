package org.sjon.parser;

/**
 * Self-explanatory
 * 
 * @author Andreas Tasoulas
 *
 */
public class SjonParsingException extends Exception {
	
	public enum Cause {
		
		SYNTAX_ERROR("Syntax error");
		
		private String msg;
		
		private Cause(String msg) {
			this.msg = msg;
		}
		
		public String getMsg() {
			return this.msg;
		}
	}
	
	private Cause cause;
	
	public SjonParsingException(Cause cause) {
		this.cause = cause;
	}
}
