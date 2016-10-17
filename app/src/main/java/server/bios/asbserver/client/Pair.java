package server.bios.asbserver.client;

public class Pair<A,B> {

	private A first = null;
	private B second = null;
	
	public Pair(){}
	
	public Pair(A first, B second) {
		this.setFirst(first);
		this.setSecond(second);
	}
	
	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}
	
}
