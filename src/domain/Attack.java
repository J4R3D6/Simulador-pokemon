package domain;



public class Attack {

	private String name;

	private String type;

	private int power;

	private int presition;

	private int ppMax;
	private int ppActual;

	private int idCSV;

	private int idInside;

	private String description;

	public Attack( int idInside,String[] info) {
		//id,nombre,descripcion,tipo,clase,potencia,precision,pp
		this.idInside = idInside;
		this.idCSV = Integer.parseInt(info[0]);
		this.name = info[1];
		this.type = info[3];
		this.power = Integer.parseInt(info[5]);
		this.presition = Integer.parseInt(info[6]);
		this.ppMax = Integer.parseInt(info[7]);
		this.ppActual = this.ppMax;
		this.description = info[2];
	}

	public void usePP(){
		this.ppActual--;
	}
	public String[] getInfo() {
		String[] info = new String[9];
		info[0] = this.name;               // Nombre del ataque
		info[1] = this.type;               // Tipo del ataque
		info[2] = String.valueOf(this.power);      // Poder del ataque
		info[3] = String.valueOf(this.presition);  // Precisión del ataque
		info[4] = String.valueOf(this.ppActual);         // Puntos de poder (PP)
		info[5] = String.valueOf(this.ppMax);         // Puntos de poder máximos (PP)
		info[6] = String.valueOf(this.idCSV);         // ID del ataque en el CSV
		info[7] = this.description;        // Descripción del ataque
		info[8] = String.valueOf(this.idInside); //Id interno del juego
		return info;
	}
	@Override
	public String toString() {
		return String.format("%s (Type: %s, Power: %d, Accuracy: %d%%, PP: %d/%d)",
				name, type, power, presition, ppActual, ppMax);
	}
	public int getIdInside() {
		return this.idInside;
	}
	public int getIdCSV(){
		return this.idCSV;
	}

	public int getPPActual(){
		return this.ppActual;
	}
	public int getPPMax(){
		return this.ppMax;
	}


}
