package metmon.rest;

import org.springframework.boot.SpringApplication;

import metmon.MetmonApp;

/*
 * Handles the boiler plate code for testing. Currently this
 * 	- starts the application
 */
public class TestBase {

	static boolean initDone = false;
	
	public TestBase() {
		if(!initDone) {
			synchronized (TestBase.class) {
				if(!initDone) {
					SpringApplication.run(MetmonApp.class);
					initDone = true;
				}
			}
		}
	}

}
