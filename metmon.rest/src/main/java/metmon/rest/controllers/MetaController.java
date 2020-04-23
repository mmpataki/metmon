package metmon.rest.controllers;

import static metmon.rest.controllers.util.RestTryExecutor.build;

import java.util.List;
import java.util.Map;

import metmon.model.meta.KeyRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import metmon.model.meta.MetaRecord;
import metmon.model.meta.Views;
import metmon.rest.controllers.util.NullObject;
import metmon.rest.controllers.util.RESTResponse;
import metmon.rest.services.MetricMetaService;
import metmon.rest.services.ProcessService;
import metmon.rest.services.ViewsService;

@RestController
@CrossOrigin("*")
public class MetaController {

    public static final String META_PATH = "/metmon/meta/metrics";

    public static final String KEYS_PATH = "/metmon/meta/keys";

    public static final String VIEW_PATH = "/metmon/meta/views";

    public static final String PROCESSES_PATH = "/metmon/meta/processes";

    public static final String PROC_GROUPS_PATH = PROCESSES_PATH + "/procgroups";


    @Autowired
    MetricMetaService MS;

    @Autowired
    ViewsService VS;

    @Autowired
    ProcessService PS;

    @RequestMapping(method = RequestMethod.GET, path = META_PATH + "/{procGroup}/{proc}")
    public RESTResponse<MetaRecord> getAvailbleMetrics(@PathVariable String procGroup, @PathVariable String proc) {
        return build(() -> MS.getAvailableMetrics(procGroup, proc));
    }

    @RequestMapping(method = RequestMethod.GET, path = VIEW_PATH + "/{procGroup}/{proc}")
    public RESTResponse<List<Views>> getAvailableViews(@PathVariable String procGroup, @PathVariable String proc) {
        return build(() -> VS.getAvailableViews(procGroup, proc));
    }

    @RequestMapping(method = RequestMethod.POST, path = VIEW_PATH)
    public RESTResponse<NullObject> createView(@RequestBody Views view) {
        return build(() -> VS.createView(view));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = VIEW_PATH + "/{procGroup}/{proc}/{viewName}")
    public RESTResponse<NullObject> deleteView(@PathVariable String procGroup, @PathVariable String proc, @PathVariable String viewName) {
        return build(() -> VS.deleteView(procGroup, proc, viewName));
    }

    @RequestMapping(method = RequestMethod.GET, path = PROC_GROUPS_PATH)
    public RESTResponse<List<String>> getProcGroups() {
        return build(() -> PS.getProcGroups());
    }

    @RequestMapping(method = RequestMethod.GET, path = PROCESSES_PATH + "/{procGroup}/processes")
    public RESTResponse<List<String>> getProcesses(@PathVariable String procGroup) {
        return build(() -> PS.getProcesses(procGroup));
    }

    @RequestMapping(method = RequestMethod.POST, path = KEYS_PATH)
    public RESTResponse<Map<String, Short>> registerKeys(@RequestBody KeyRegisterRequest req) {
        return build(() -> {
            PS.addProcess(req.getpId());
            return MS.registerMetrics(req);
        });
    }
}
