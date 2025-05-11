package domain;

public abstract class Item {
	private int number;
	protected String name;
	public Item(int number){
		this.number = number;
	}
	public int number(){
		return number;
	}

	public void effect(Pokemon pokemon){
		if(!this.isUsed()) {
			this.number = this.usedItem();
			this.itemEffect(pokemon);
		}
	}
	private int usedItem(){
		this.number = this.number - 1;
		return this.number;
	}
	public boolean isUsed(){
		return this.number == 0;
	}

	public String getName() {
		if(this.name == null) return "Unknown";
		return this.name;
	}
	public abstract void itemEffect( Pokemon pokemon);
}
