package domain;

public class Item {
	private int number;
	private int id;
	public Item(int number, int id){
		this.number = number;
		this.id = id;
	}
	public int number(){
		return number;
	}

	public void action(Pokemon pokemon) {

	}
	public  int getId(){
		return id;
	}
}
