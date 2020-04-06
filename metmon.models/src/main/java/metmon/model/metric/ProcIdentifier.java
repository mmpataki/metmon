package metmon.model.metric;

public class ProcIdentifier {

	String processGrp;

	String process;

	public String getProcessGrp() {
		return processGrp;
	}

	public void setProcessGrp(String processGrp) {
		this.processGrp = processGrp;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public ProcIdentifier(String processGrp, String process) {
		super();
		this.processGrp = processGrp;
		this.process = process;
	}

	public ProcIdentifier() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object obj) {
		ProcIdentifier id = (ProcIdentifier)obj;
		return (process.equals(id.getProcess()) &&
				processGrp.equals(id.getProcessGrp()));
	}
	
	@Override
	public String toString() {
		return processGrp + "/" + process;
	}
}
