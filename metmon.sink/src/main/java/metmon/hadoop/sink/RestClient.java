package metmon.hadoop.sink;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import metmon.model.meta.KeyRegisterRequest;
import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;
import sun.net.www.http.HttpClient;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClient {

    String baseUrl;

    Client client;

    String KEYS_PATH = "/metmon/meta/keys";
    String METRICS_PATH = "/metmon/metrics/create";

    public RestClient(String baseURL) {
        baseUrl = baseURL;
    }

    public void postMetric(MetricRecord mr) throws Exception {
        doPost(baseUrl + METRICS_PATH, mr, Void.class);
    }

    public static class RestResponse {
        HashMap<String, Short> item;
        String error = null;
        boolean success = true;

        public RestResponse() {
        }

        public HashMap<String, Short> getItem() {
            return item;
        }

        public void setItem(HashMap<String, Short> item) {
            this.item = item;
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
    }

    public <T> T execute(String method, String url, Object data, Class<T> retType) throws Exception {
        URL U = new URL(url);
        HttpURLConnection con = (HttpURLConnection) U.openConnection();
        con.setRequestMethod(method);

        if(data != null) {
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            OutputStream os = con.getOutputStream();
            os.write(new Gson().toJson(data).getBytes());
            os.flush();
        }

        int resp = con.getResponseCode();
        if(resp != 200) {
            throw new Exception("HTTP error: " + resp);
        }
        return new Gson().fromJson(new InputStreamReader(con.getInputStream()), retType);
    }

    public <T> T doPost(String url, Object data, Class<T> retType) throws Exception {
        return execute("POST", url, data, retType);
    }

    public Map<String, Short> getKeys(KeyRegisterRequest req) throws Exception {
        return doPost(baseUrl + KEYS_PATH, req, RestResponse.class).item;
    }

    public static void main(String[] args) throws Exception {
        ProcIdentifier id = new ProcIdentifier("pg", "p");
        MetricRecord mr = new MetricRecord(5, id);
        mr.addRecord(new Metric((short) 1, 9.0));
        RestClient C = new RestClient("http://localhost:8080");
        C.getKeys(new KeyRegisterRequest(id, Arrays.asList("123", "234")));
        C.postMetric(mr);
    }

}
