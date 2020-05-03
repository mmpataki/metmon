package metmon.rest;

import metmon.model.meta.KeyRegisterRequest;
import metmon.model.meta.MetaRecord;
import metmon.model.meta.View;
import metmon.model.meta.View.ViewData;
import metmon.model.meta.Views;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricRequest;
import metmon.model.metric.ProcIdentifier;
import metmon.rest.client.RestClient;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;
import java.util.stream.Collectors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTest extends TestBase {

    RestClient C;
    ProcIdentifier mrid;
    List<MetricRecord> recs;
    String ctxt = "testCtxt";
    String[] keys = {"numRequests", "maxRequestTime", "activeThreads", "memoryMB", "numExceptions"};

    public ControllerTest() throws Exception {
        super();
    }

    @Before
    public void setup() {
        C = new RestClient("http://localhost:8080");
        mrid = new ProcIdentifier("sim", "simt1");
        MetricProducer mp = new MetricProducer(mrid, ctxt, 5, keys);

        recs = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            recs.add(mp.pop());
        }
    }

    @Test
    public void T2_testMetricsIngestion() throws Exception {
        for (MetricRecord mr : recs) {
            C.postMetric(mr);
        }
        List<MetricRecord> metrics = C
                .getMetrics(new MetricRequest<Short>(recs.get(0).getTs(), recs.get(recs.size() - 1).getTs() + 1, mrid,
                        new HashSet<>(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4, (short) 5))));
        for (MetricRecord mr1 : recs) {
            boolean exists = false;
            for (MetricRecord mr2 : metrics) {
                if (mr2.equals(mr1)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                throw new RuntimeException(mr1 + " not present in the response");
            }
        }
    }

    @Test
    public void T1_testMetaIngest() throws Exception {
        C.registerKeys(new KeyRegisterRequest(mrid, Arrays.stream(keys).map(k -> ctxt + ":" + k).collect(Collectors.toList())));
        MetaRecord mm = C.getMeta(mrid.getProcessGrp(), mrid.getProcess());
        for (String key : keys) {
            if (mm.getKeys().keySet().stream().noneMatch(r -> r.equals(ctxt + ":" + key))) {
                throw new RuntimeException("meta ingest test failed. key not found : " + key);
            }
        }
    }

    @Test
    public void T3_testViewsApi() throws Exception {
        Set<String> s = Arrays.asList(keys).stream().map(k -> ctxt + ":" + k).collect(Collectors.toSet());
        View vc = new View(mrid, "myView", new ViewData(s));
        Views vs = new Views();
        vs.addRecord(vc);
        C.createView(vs);
        Views views = C.getViews(mrid.getProcessGrp(), mrid.getProcess());
        for (View v : views.getRecords()) {
            if (v.equals(vc))
                return;
        }
        throw new RuntimeException("view is not created");
    }

    @Test
    public void T4_testProcGroupApi() throws Exception {
        for (String pg : C.getProcessGroups()) {
            if (pg.equals(mrid.getProcessGrp()))
                return;
        }
        throw new RuntimeException("processGroup " + mrid.getProcessGrp() + " not found");
    }

    @Test
    public void T5_testProcsApi() throws Exception {
        for (String p : C.getProcesses(mrid.getProcessGrp())) {
            if (p.equals(mrid.getProcess()))
                return;
        }
        throw new RuntimeException("process " + mrid.getProcess() + " not found in " + mrid.getProcessGrp());
    }

    @Test
    public void T6_testBinaryApi() throws Exception {
        for (MetricRecord mr : recs) {
            C.postMetricBinary(mr);
        }
        List<MetricRecord> metrics = C
                .getMetrics(new MetricRequest<Short>(recs.get(0).getTs(), recs.get(recs.size() - 1).getTs() + 1, mrid,
                        new HashSet<>(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4, (short) 5))));
        for (MetricRecord mr1 : recs) {
            boolean exists = false;
            for (MetricRecord mr2 : metrics) {
                if (mr2.equals(mr1)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                throw new RuntimeException(mr1 + " not present in the response");
            }
        }
    }
}
