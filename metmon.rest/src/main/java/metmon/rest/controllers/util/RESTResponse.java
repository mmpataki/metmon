package metmon.rest.controllers.util;

/* wrapper around response */
public class RESTResponse<ReturnObject> {

	String error;
	boolean success;
	ReturnObject item;
	
	public RESTResponse(ReturnObject obj) {
		item = obj;
		success = true;
		error = null;
	}
	
	public RESTResponse(String err) {
		item = null;
		error = err;
		success = false;
	}

	public RESTResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ReturnObject getItem() {
		return item;
	}

	public void setItem(ReturnObject item) {
		this.item = item;
	}
}
