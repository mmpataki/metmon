package metmon.rest.controllers.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class RestTryExecutor<ReturnType> {

	public interface ReturningFunction<ReturnType> {
		public ReturnType run() throws Throwable;
	}

	public interface VoidFunction {
		public void run() throws Throwable;
	}
	
	public RESTResponse<ReturnType> run(ReturningFunction<ReturnType> function) {
		try {
			return new RESTResponse<ReturnType>(function.run());
		} catch(Throwable ex) {
			return new RESTResponse<ReturnType>(getExceptionString(ex));
		}
	}
	
	private String getExceptionString(Throwable ex) {
		ex.printStackTrace();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}

	public RESTResponse<NullObject> run(VoidFunction func) {
		try {
			func.run();
			return new RESTResponse<NullObject>((NullObject)null);
		} catch(Throwable ex) {
			return new RESTResponse<NullObject>(getExceptionString(ex));
		}
	}
	
	/* sugar */
	public static <RetType> RESTResponse<RetType> build(RestTryExecutor.ReturningFunction<RetType> func) {
		return new RestTryExecutor<RetType>().run(func);
	}
	/* honey */
	public static RESTResponse<NullObject> build(RestTryExecutor.VoidFunction func) {
		return new RestTryExecutor<NullObject>().run(func);
	}
}
