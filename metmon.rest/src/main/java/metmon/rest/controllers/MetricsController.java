package metmon.rest.controllers;

import metmon.model.metric.MetricSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricRequest;
import metmon.rest.client.NullObject;
import metmon.rest.client.RESTResponse;
import metmon.rest.services.MetricsService;

import static metmon.rest.controllers.util.RestTryExecutor.build;

import java.util.List;

@RestController
@CrossOrigin("*")
public class MetricsController {

    @Autowired
    MetricsService MC;

    MetricSerializer S = new MetricSerializer();

    public static final String METRICS_PATH_CREATE = "/metmon/metrics/create";
    public static final String METRICS_PATH = "/metmon/metrics";

    @RequestMapping(method = RequestMethod.POST, value = METRICS_PATH_CREATE)
    RESTResponse<NullObject> publish(@RequestBody MetricRecord update) throws Exception {
        return build(() -> MC.consume(update));
    }

    @RequestMapping(method = RequestMethod.POST, value = METRICS_PATH_CREATE, consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    RESTResponse<NullObject> publishBinary(@RequestBody byte[] data) throws Exception {
        return build(() -> MC.consume(S.toMetricRecord(data)));
    }

    @RequestMapping(method = RequestMethod.POST, value = METRICS_PATH)
    RESTResponse<List<MetricRecord>> get(@RequestBody MetricRequest<Short> req) {
        return build(() -> MC.fetch(req));
    }

}
