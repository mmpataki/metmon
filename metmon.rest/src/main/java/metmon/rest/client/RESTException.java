package metmon.rest.client;

public class RESTException extends Exception {
	private static final long serialVersionUID = 1370098502239399656L;

	public RESTException() {
		super();
	}

	public RESTException(String error) {
		super(error);
	}
}
