package metmon.rest.client;

import com.google.gson.Gson;
import metmon.model.meta.KeyRegisterRequest;
import metmon.model.meta.MetaRecord;
import metmon.model.meta.Views;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricRequest;
import metmon.model.metric.MetricSerializer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class RestClient {

    static public class NullObject {
        @Override
        public String toString() {
            return "null";
        }
    }

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


    String baseUrl;

    MetricSerializer S;

    public static final String KEYS_PATH = "/meta/keys";
    public static final String METRICS_PUT_PATH = "/metrics/create";

    public static final String METRICS_GET_PATH = "/metrics";

    public static final String META_PATH = "/meta/metrics";

    public static final String VIEW_PATH = "/meta/views";
    public static final String PROCESSES_PATH = "/meta/processes";

    public static final String PROC_GROUPS_PATH = PROCESSES_PATH + "/procgroups";


    public RestClient(String baseURL) {
        baseUrl = baseURL;
        S = new MetricSerializer();
    }

    public void postMetric(MetricRecord mr) throws Exception {
        postJson(METRICS_PUT_PATH, mr, new ParameterizedTypeReference<RESTResponse<NullObject>>() {
        }).getItem();
    }

    public void postMetricBinary(MetricRecord mr) throws Exception {
        postBinary(METRICS_PUT_PATH, S.fromMetricRecord(mr), new ParameterizedTypeReference<RESTResponse<NullObject>>() {
        }).getItem();
    }

    public Map<String, Short> getKeys(KeyRegisterRequest req) throws Exception {
        return postJson(KEYS_PATH, req, new ParameterizedTypeReference<RESTResponse<Map<String, Short>>>() {
        }).getItem();
    }

    public <K, V> List<MetricRecord> getMetrics(MetricRequest<K> mr) throws Exception {
        return postJson(METRICS_GET_PATH, mr,
                new ParameterizedTypeReference<RESTResponse<List<MetricRecord>>>() {
                }).getItem();
    }

    public <K, V> MetaRecord getMeta(String procGrp, String proc) throws Exception {
        return getJson(META_PATH + "/" + procGrp + "/" + proc,
                new ParameterizedTypeReference<RESTResponse<MetaRecord>>() {
                }).getItem();
    }

    public <K, V> NullObject createView(Views v) throws Exception {
        return postJson(VIEW_PATH, v,
                new ParameterizedTypeReference<RESTResponse<NullObject>>() {
                }).getItem();
    }

    public <K, V> Views getViews(String procGrp, String proc) throws Exception {
        List<Views> vs = getJson(VIEW_PATH + "/" + procGrp + "/" + proc,
                new ParameterizedTypeReference<RESTResponse<List<Views>>>() {
                }).getItem();
        if (vs.isEmpty())
            return new Views();
        return vs.get(0);
    }

    public List<String> getProcessGroups() throws Exception {
        return getJson(PROC_GROUPS_PATH,
                new ParameterizedTypeReference<RESTResponse<List<String>>>() {
                }).getItem();
    }

    public List<String> getProcesses(String procGroup) throws Exception {
        return getJson(PROCESSES_PATH + "/" + procGroup + "/processes",
                new ParameterizedTypeReference<RESTResponse<List<String>>>() {
                }).getItem();
    }

    public Map<String, Short> registerKeys(KeyRegisterRequest req) throws Exception {
        return postJson(KEYS_PATH, req,
                new ParameterizedTypeReference<RESTResponse<Map<String, Short>>>() {
                }).getItem();
    }


    public static enum Method {
        GET,
        POST,
        PATCH,
        DELETE;
    }

    interface RespDeSerializer<T> {
        T deserialize(InputStream is);
    }

    class JsonDeserializer<T> implements RespDeSerializer<T> {

        Type typ;

        JsonDeserializer(Type type) {
            typ = type;
        }

        @Override
        public T deserialize(InputStream is) {
            return new Gson().fromJson(new InputStreamReader(is), typ);
        }
    }

    public <T> T execute(Method method, String url, byte[] data, Map<String, String> headers, RespDeSerializer<T> deser) throws Exception {
        URL U = new URL(baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) U.openConnection();
        con.setRequestMethod(method.name());

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        if (data != null) {
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(data);
            os.flush();
        }

        int resp = con.getResponseCode();
        if (resp != 200) {
            throw new Exception("HTTP error: " + resp);
        }
        return deser.deserialize(con.getInputStream());
    }

    public <T> T postBinary(String url, byte[] data, ParameterizedTypeReference<T> ptr) throws Exception {
        return execute(
                Method.POST,
                url,
                data,
                new HashMap<String, String>() {{
                    put("Content-Type", "application/octet-stream");
                }},
                new JsonDeserializer<>(ptr.getType())
        );
    }

    public <T> T postJson(String url, Object o, ParameterizedTypeReference<T> ptr) throws Exception {
        return execute(
                Method.POST,
                url,
                new Gson().toJson(o).getBytes(),
                new HashMap<String, String>() {{
                    put("Content-Type", "application/json");
                }},
                new JsonDeserializer<>(ptr.getType())
        );
    }

    public <T> T getJson(String url, ParameterizedTypeReference<T> ptr) throws Exception {
        return execute(
                Method.GET,
                url,
                null,
                null,
                new JsonDeserializer<>(ptr.getType())
        );
    }

}
