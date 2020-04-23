package metmon.model.meta;

import metmon.model.metric.ProcIdentifier;

import java.util.List;

public class KeyRegisterRequest {

    ProcIdentifier pId;

    List<String> keys;

    public List<String> getKeys() {
        return keys;
    }

    public ProcIdentifier getpId() {
        return pId;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public void setpId(ProcIdentifier pId) {
        this.pId = pId;
    }

    public KeyRegisterRequest() {
    }

    public KeyRegisterRequest(ProcIdentifier pId, List<String> keys) {
        this.pId = pId;
        this.keys = keys;
    }
}
