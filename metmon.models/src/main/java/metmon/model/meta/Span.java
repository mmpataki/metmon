package metmon.model.meta;

public class Span {

	long from, to;

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}
	
	public Span() {
		
	}

	public Span(long from, long to) {
		super();
		this.from = from;
		this.to = to;
	}
	
	@Override
	public boolean equals(Object obj) {
		Span s = (Span)obj;
		return getFrom() == s.getFrom() && getTo() == s.getTo();
	}
	
}
