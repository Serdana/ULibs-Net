package main.java.ulibs.net.exceptions;

public class NetworkException extends Exception {
	private static final long serialVersionUID = 8701790550918829753L;
	
	public NetworkException(Reason reason) {
		super(reason.print);
	}
	
	public enum Reason {
		//@formatter:off
		invalid_message         ("Invalid Message"),
		unknown_message         ("Unknown Message"),
		unknown_message_data    ("Unknown Message Data"),
		empty_message           ("Empty Message"),
		too_big                 ("Too Big Message"),
		other                   ("Other Error"),
		already_connected       ("Connection Already Established"),
		non_existent_connection ("Non Existent Connection");
		//@formatter:on
		
		private final String print;
		
		private Reason(String print) {
			this.print = print;
		}
	}
}
