package metmon.hadoop.sink;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;

public class RestClient {

	String baseUrl;

	public RestClient(String baseURL) {
		baseUrl = baseURL;
	}

	public void postMetric(MetricRecord<String, Double> mr) throws Exception {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource(baseUrl + "/metmon/metrics/create");
		webResource.type(MediaType.APPLICATION_JSON).post(new Gson().toJson(mr));
	}
	
	public static void main(String[] args) throws Exception {
		MetricRecord<String, Double> mr = new MetricRecord<String, Double>(5, "hello", new ProcIdentifier("pg", "p"));
		mr.addRecord(new Metric<String, Double>("k1", 9.0));
		new RestClient("http://localhost:8080").postMetric(mr);
	}

}
