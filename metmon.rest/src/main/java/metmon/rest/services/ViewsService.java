package metmon.rest.services;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import metmon.conf.MetmonConfiguration;
import metmon.model.meta.MetaConsts;
import metmon.model.meta.MetaRecordRequest;
import metmon.model.meta.View;
import metmon.model.meta.Views;
import metmon.model.metric.ProcIdentifier;
import metmon.store.DeleteRequest;
import metmon.store.Store;

@Service
public class ViewsService {

	@Autowired
	MetmonConfiguration conf;

	Stores<String, String, Views, View> stores;

	@PostConstruct
	private void init() throws Exception {
		stores = new Stores<>("viewstore", conf, new SerDes.StringSerde(), new SerDes.StringSerde());
	}

	/* views */
	public void createView(Views view) throws Exception {
		Store<String, String, Views, View> store = stores.open(view.getpId(), true);
		view.setTs(MetaConsts.META_VIEWS_TS);
		store.put(view, r -> r, c -> {
			((View) c).setpId(view.getpId());
			return c;
		});
	}

	public void deleteView(String procGroup, String proc, String viewName) throws Exception {
		ProcIdentifier pId = new ProcIdentifier(procGroup, proc);
		Store<String, String, Views, View> store = stores.open(pId, false);
		store.delete(new DeleteRequest<String>(MetaConsts.META_VIEWS_TS,
				Arrays.asList(new View(pId, viewName, null).getKey())));
	}

	public List<Views> getAvailableViews(String procGroup, String proc) throws Exception {
		ProcIdentifier pId = new ProcIdentifier(procGroup, proc);
		Store<String, String, Views, View> store = stores.open(pId, true);
		return store.get(new MetaRecordRequest(MetaConsts.META_VIEWS_TS, MetaConsts.META_VIEWS_TS_END),
				(ts) -> new Views(), (k, v) -> new View(k, v));
	}

}
