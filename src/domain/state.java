package domain;

public class state extends Attack {

	private int time;

	public state(int time,int idInside, String[] info){
		super(idInside,info);
		this.time = time;
	}

	public void countdown() {

	}

}
