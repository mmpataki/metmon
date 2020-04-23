package metmon.rest.client;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import metmon.model.meta.KeyRegisterRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import metmon.model.meta.MetaRecord;
import metmon.model.meta.Views;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricRequest;
import metmon.rest.controllers.MetaController;
import metmon.rest.controllers.MetricsController;
import metmon.rest.controllers.util.NullObject;
import metmon.rest.controllers.util.RESTResponse;

public class RestClient {

	RestTemplate template;

	public RestClient() {
		template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
	}

	private String getBaseUri() {
		return "http://localhost:8080";
	}

	public <InputType, OutputType> OutputType exec(HttpMethod method, String path, InputType o,
			ParameterizedTypeReference<RESTResponse<OutputType>> ptype) throws RESTException {
		String notEncoded = "testuser:passwd";
		String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", encodedAuth);
		RESTResponse<OutputType> resp = template
				.exchange(getBaseUri() + path, method, new HttpEntity<InputType>(o, headers), ptype).getBody();
		if (resp.isSuccess()) {
			return resp.getItem();
		} else {
			throw new RESTException(resp.getError());
		}
	}

	public <T> T get(String path, ParameterizedTypeReference<RESTResponse<T>> gtype) throws RESTException {
		return exec(HttpMethod.GET, path, null, gtype);
	}
	
	public <T> T post(String path, Object o, ParameterizedTypeReference<RESTResponse<T>> gtype) throws RESTException {
		return exec(HttpMethod.POST, path, o, gtype);
	}

	public <T> void delete(String path, ParameterizedTypeReference<RESTResponse<T>> gtype) throws RESTException {
		exec(HttpMethod.DELETE, path, null, gtype);
	}

	public <T> void patch(String path, Object o, ParameterizedTypeReference<RESTResponse<T>> ptype)
			throws RESTException {
		exec(HttpMethod.PATCH, path, o, ptype);
	}

	public <K, V> void postMetric(MetricRecord mr) throws RESTException {
		post(MetricsController.METRICS_PATH_CREATE, mr, 
				new ParameterizedTypeReference<RESTResponse<NullObject>>() {});
	}

	public <K, V> List<MetricRecord> getMetrics(MetricRequest<K> mr) throws RESTException {
		return post(MetricsController.METRICS_PATH, mr,
				new ParameterizedTypeReference<RESTResponse<List<MetricRecord>>>() {});
	}
	
	public <K, V> MetaRecord getMeta(String procGrp, String proc) throws RESTException {
		return get(MetaController.META_PATH + "/" + procGrp + "/" + proc,
				new ParameterizedTypeReference<RESTResponse<MetaRecord>>() {});
	}

	public <K, V> NullObject createView(Views v) throws RESTException {
		return post(MetaController.VIEW_PATH, v,
				new ParameterizedTypeReference<RESTResponse<NullObject>>() {});
	}

	public <K, V> Views getViews(String procGrp, String proc) throws RESTException {
		List<Views> vs = get(MetaController.VIEW_PATH + "/" + procGrp + "/" + proc,
				new ParameterizedTypeReference<RESTResponse<List<Views>>>() {});
		if(vs.isEmpty())
			return new Views();
		return vs.get(0);
	}

	public List<String> getProcessGroups() throws RESTException {
		return get(MetaController.PROC_GROUPS_PATH,
				new ParameterizedTypeReference<RESTResponse<List<String>>>() {});
	}
	
	public List<String> getProcesses(String procGroup) throws RESTException {
		return get(MetaController.PROCESSES_PATH + "/" + procGroup + "/processes",
				new ParameterizedTypeReference<RESTResponse<List<String>>>() {});
	}

	public Map<String, Short> registerKeys(KeyRegisterRequest req) throws RESTException {
		return post(MetaController.KEYS_PATH, req,
				new ParameterizedTypeReference<RESTResponse<Map<String, Short>>>() {});
	}
}