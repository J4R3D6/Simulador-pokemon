package domain;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;

public class Pokemon {

	public String name;

	public String idPokedex;

	private int id;

	public String type;

	public int maxHealth;

	public int currentHealth;

	public int attack;

	public int defense;

	public int specialAttack;

	public int specialDefense;

	public int speed;

	private int ivs;

	public int level;

	private boolean active;

	private boolean weak;

	public int levelRequirement;

	public int xp;

	private static boolean random;

	private ArrayList<Attack> attacks;
	private boolean shiny;
	private ArrayList<State> states;

	public Pokemon() {
		initDefault();
	}

	public Pokemon(int id, String[] info, ArrayList<Integer> attacksIds, boolean random, int pokemonLvl) {
		try {
			if (info.length < 11) throw new POOBkemonException(POOBkemonException.LESS_INFORMACION_POKEMON);
			initFromParameters(id, info, attacksIds, random, pokemonLvl);
		} catch (POOBkemonException | NumberFormatException e) {
			initDefault();
			System.out.println(id+" "+info.toString()+" "+attacksIds.toString()+" "+random);
			for(String s: info){
				System.out.println(s);
			}
			System.err.println("Error al crear Pokémon: " + e.getMessage());
		}
		this.probShiny();
	}

	private void initDefault() {
		this.id = 0;
		this.name = "MissingNo";
		this.idPokedex = "0";
		this.type = "Normal";
		this.maxHealth = 100;
		this.currentHealth = this.maxHealth;
		this.attack = 10;
		this.defense = 10;
		this.specialAttack = 10;
		this.specialDefense = 10;
		this.speed = 10;
		this.xp = 0;
		this.level = 1;
		this.levelRequirement = 100;
		this.states = new ArrayList<>();
		this.active = false;
		this.weak = false;
		this.random = false;
		this.attacks = new ArrayList<>();
	}

	private void initFromParameters(int id, String[] info, ArrayList<Integer> attacksIds, boolean random, int pokemonLvl) {
		this.id = id;
		this.name = info[1];
		this.idPokedex = info[0];
		this.type = info[2];
		this.maxHealth = Integer.parseInt(info[5]);
		this.currentHealth = this.maxHealth;
		this.attack = Integer.parseInt(info[6]);
		this.defense = Integer.parseInt(info[7]);
		this.specialAttack = Integer.parseInt(info[8]);
		this.specialDefense = Integer.parseInt(info[9]);
		this.speed = Integer.parseInt(info[10]);
		this.xp = 0;
		this.level = pokemonLvl;
		this.levelRequirement = 100;
		this.states = new ArrayList<>();
		this.active = false;
		this.weak = false;
		this.random = random;
		this.attacks = new ArrayList<>(this.createAttacks(attacksIds));
	}

	private int randomStatics(int base) {
		return 0;
	}

	private void setAtacks(String[] Attacks) {

	}

	public String[] pokemonInfo() {
		return null;
	}

	public String[][] attackInfo() {
		return null;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;

	}

	public void getDamage() {

	}
	public int getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}

	public Attack getAttack(int id) {
		return null;
	}

	public void levelUp() {

	}

	private ArrayList<Attack> createAttacks(ArrayList<Integer> attacksIds) {
		ArrayList<Attack> ataques =  new ArrayList<>();
		MovesRepository movesRepository = new MovesRepository();
		for(Integer id : attacksIds) {
			String[] infoAttack = movesRepository.getAttacksId(id);
			Attack atack = new Attack(infoAttack);
			ataques.add(atack);
		}
		return ataques;
	}
	public boolean getWeak() {
		return this.weak;
	}
	public ArrayList<State> getStates() {
		return this.states;
	}
	public ArrayList<Attack> getAttacks() {
		return this.attacks;
	}
	private void probShiny(){
		this.shiny = Math.random() < 0.2;
	}
	public String[] getInfo() {

		return new String[] {
				String.valueOf(id),           // 0 - ID
				name,                         // 1 - Nombre
				idPokedex,                    // 2 - ID Pokédex
				type,                         // 3 - Tipo
				String.valueOf(level),        // 4 - Nivel
				String.valueOf(maxHealth),    // 5 - Vida máxima
				String.valueOf(currentHealth), // 6 - Vida actual
				String.valueOf(attack),        // 7 - Ataque
				String.valueOf(defense),      // 8 - Defensa
				String.valueOf(specialAttack), // 9 - Ataque Especial
				String.valueOf(specialDefense),// 10 - Defensa Especial
				String.valueOf(speed),         // 11 - Velocidad
				String.valueOf(xp),            // 12 - XP actual
				String.valueOf(levelRequirement), // 13 - XP requerido
				String.valueOf(active),        // 14 - Estado (activo)
				String.valueOf(weak),        // 15 - Pokemon debilitad
				String.valueOf(shiny),          // 16 - Pokemon shiny
		};
	}

	public void getDamage(int damage,int idAttack) {
		MovesRepository movesRepository = new MovesRepository();
		StatsRepository statsRepository = new StatsRepository();
		String[]info = movesRepository.getAttackDamageAndType(idAttack);
		double multiplicator = statsRepository.getMultiplier(info[0],this.type);
		this.currentHealth = (int)(this.currentHealth - damage*multiplicator);
		if(this.currentHealth < 0){
			this.currentHealth = 0;
			this.weak = true;
		}
	}
}
