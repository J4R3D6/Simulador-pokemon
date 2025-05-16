package domain;

public class StateHeal extends Attack {
	private int health;

	public StateHeal(int idInside, String[] info)throws POOBkemonException{
		super(idInside,info);
		this.health = Integer.parseInt(info[9]);

	}
	public int healer(){
		return health;
	}

}
