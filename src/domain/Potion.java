package domain;

public class Potion extends Item {
	private int healthPoints;

	public Potion(int number, int health) {
		super(number);
		this.healthPoints = health;
		this.createName();
	}
	@Override
	public void itemEffect(Pokemon pokemon) {
		String[] info = new String[4];
		info[0] = "Potion"; // nombre del item
		info[1] = "Heals"; // indicacion de efecto
		info[2] = Integer.toString(this.healthPoints); //puntos que da
		info[3] = "1"; //turnos del efecto
		//aplico el efecto de la poti
		System.out.println("Heal de " + this.healthPoints + " puntos");
		pokemon.effect(info);
	}
	private void createName() {
		if(this.healthPoints >1 &&this.healthPoints < 25) this.name = "Normal";
		if(this.healthPoints >= 25 && this.healthPoints < 50) this.name = "Super";
		if(this.healthPoints >= 50 && this.healthPoints < 75) this.name = "Hyper";
		if(this.healthPoints >= 75) this.name = "Mega";
	}
	public String getName() {
		return this.name;
	}
}
