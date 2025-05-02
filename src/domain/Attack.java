package domain;



public class Attack {

	private String name;

	private String type;

	private int power;

	private int presition;

	private int pp;

	private int id;

	private String description;

	public Attack(String[] info) {
		//id,nombre,descripcion,tipo,clase,potencia,precision,pp
		this.id = Integer.parseInt(info[0]);
		this.name = info[1];
		this.type = info[3];
		this.power = Integer.parseInt(info[5]);
		this.presition = Integer.parseInt(info[6]);
		this.pp = Integer.parseInt(info[7]);
		this.description = info[2];
	}

	public int getId() {
		return id;
	}

	public String[] getInfo() {
		String[] info = new String[7];
		info[0] = this.name;               // Nombre del ataque
		info[1] = this.type;               // Tipo del ataque
		info[2] = String.valueOf(this.power);      // Poder del ataque
		info[3] = String.valueOf(this.presition);  // Precisión del ataque
		info[4] = String.valueOf(this.pp);         // Puntos de poder (PP)
		info[5] = String.valueOf(this.id);         // ID del ataque
		info[6] = this.description;        // Descripción del ataque
		return info;
	}
	@Override
	public String toString() {
		return String.format("%s (Type: %s, Power: %d, Accuracy: %d%%, PP: %d/%d)",
				name, type, power, presition, pp, pp);
	}

}
