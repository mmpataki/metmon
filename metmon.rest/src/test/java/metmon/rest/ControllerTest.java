package metmon.rest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.DependsOn;

import metmon.model.meta.MetaRecord;
import metmon.model.meta.View;
import metmon.model.meta.Views;
import metmon.model.meta.View.ViewData;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricRequest;
import metmon.model.metric.ProcIdentifier;
import metmon.rest.client.RESTException;
import metmon.rest.client.RestClient;

public class ControllerTest extends TestBase {

	RestClient C;
	ProcIdentifier mrid;
	List<MetricRecord> recs;
	String ctxt = "testCtxt";
	String[] keys = { "numRequests", "maxRequestTime", "activeThreads", "memoryMB", "numExceptions" };

	@Before
	public void setup() {
		C = new RestClient();
		mrid = new ProcIdentifier("sim", "simmon_test_thread1");
		MetricProducer mp = new MetricProducer(mrid, ctxt, 5, keys);

		recs = new LinkedList<>();
		for (int i = 0; i < 1; i++) {
			recs.add(mp.pop());
		}
	}

	@Test
	public void basicMetricsIngestion() throws RESTException {
		for (MetricRecord mr : recs) {
			C.postMetric(mr);
		}
	}

	@Test
	@DependsOn("basicMetricsIngestion")
	public void ingestValidation() throws RESTException {
		List<MetricRecord> metrics = C
				.getMetrics(new MetricRequest<String>(recs.get(0).getTs(), recs.get(recs.size() - 1).getTs() + 1, mrid,
						Arrays.asList(keys).stream().map(k -> ctxt + ":" + k).collect(Collectors.toSet())));

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
	@DependsOn(value = "basicMetricsIngestion")
	public void testMetaIngestValidation() throws RESTException {
		MetaRecord mm = C.getMeta(mrid.getProcessGrp(), mrid.getProcess());
		for (String key : keys) {
			if (!mm.getContexts().get(ctxt).stream().anyMatch(r -> r.equals(key))) {
				throw new RuntimeException("meta ingest test failed. key not found : " + key);
			}
		}
	}

	@Test
	@DependsOn("basicMetricsIngestion")
	public void testViewsApi() throws RESTException {
		Set<String> s = Arrays.asList(keys).stream().map(k -> ctxt + ":" + k).collect(Collectors.toSet());
		View vc = new View(mrid, "myView", new ViewData(s));
		Views vs = new Views();
		vs.addRecord(vc);
		C.createView(vs);
		Views views = C.getViews(mrid.getProcessGrp(), mrid.getProcess());
		for (View v : views.getRecords()) {
			if(v.equals(vc))
				return;
		}
		throw new RuntimeException("view is not created");
	}

	@Test
	@DependsOn("basicMetricsIngestion")
	public void testProcGroupApi() throws RESTException {
		for (String pg : C.getProcessGroups()) {
			if (pg.equals(mrid.getProcessGrp()))
				return;
		}
		throw new RuntimeException("processGroup " + mrid.getProcessGrp() + " not found");
	}

	@Test
	@DependsOn("basicMetricsIngestion")
	public void testProcsApi() throws RESTException {
		for (String p : C.getProcesses(mrid.getProcessGrp())) {
			if (p.equals(mrid.getProcess()))
				return;
		}
		throw new RuntimeException("process " + mrid.getProcess() + " not found in " + mrid.getProcessGrp());
	}
}
