package domain;

import java.util.Random;
import java.util.ArrayList;

/**
 * Represents a Pokemon creature with all its attributes and battle capabilities.
 * This class handles Pokemon creation, stats calculation, and battle mechanics.
 */
public class Pokemon {

	// Basic Pokemon attributes
	public String name;
	public String idPokedex;
	private int id;
	public String type;

	// Combat stats
	public int maxHealth;
	public int currentHealth;
	public int attack;
	public int defense;
	public int specialAttack;
	public int specialDefense;
	public int speed;
	public int level;

	// Status flags
	private boolean active;
	private boolean weak;
	private boolean shiny;

	// Progression
	public int levelRequirement;
	public int xp;
	public int ivs;

	// Battle modifiers
	private static boolean random;
	private static int attackId = 0;
	private ArrayList<Attack> attacks;
	private ArrayList<State> states;

	private State principalState;
	private int accuracyStage = (int)(Math.random() * 13) - 6;
	private int evasionStage = (int)(Math.random() * 13) - 6;

	// Constants for battle calculations
	private static final double CRITICAL_HIT_CHANCE = 0.0417; // 4.17% standar = 0.0417
	private static final double STAGE_MODIFIER = 1.3;  // 1.5 Pokemon standar (Modify Accuracy)

	/**
	 * Default constructor for Pokemon.
	 * @throws POOBkemonException if there's an error during creation
	 */
	public Pokemon() throws POOBkemonException {
		initDefault();
	}

	/**
	 * Parameterized constructor for Pokemon.
	 * @param id Pokemon ID
	 * @param info Array containing Pokemon information
	 * @param attacksIds List of attack IDs
	 * @param random Whether to generate random stats
	 * @param pokemonLvl Initial level
	 * @throws POOBkemonException if there's an error during creation
	 */
	public Pokemon(int id, String[] info, ArrayList<Integer> attacksIds, boolean random, int pokemonLvl) throws POOBkemonException {
		try {
			if (info.length < 11) throw new POOBkemonException(POOBkemonException.LESS_INFORMACION_POKEMON);
			initFromParameters(id, info, attacksIds, random, pokemonLvl);
		} catch (POOBkemonException | NumberFormatException e) {
			initDefault();
			System.err.println("Error creating Pokémon: " + e.getMessage());
		}
		this.probShiny();
	}

	/**
	 * Initializes default values for a Pokemon.
	 * @throws POOBkemonException if default attack cannot be created
	 */
	private void initDefault() throws POOBkemonException {
		this.id = 0;
		this.name = "MissingNo";
		this.idPokedex = "0";
		this.type = "Normal";
		this.maxHealth = 180;
		this.currentHealth = this.maxHealth;
		this.attack = 10;
		this.defense = 10;
		this.specialAttack = 10;
		this.specialDefense = 10;
		this.speed = 10;
		this.xp = 0;
		this.level = 1;
		this.levelRequirement = 100;
		this.states = new ArrayList<State>();
		this.active = false;
		this.weak = false;
		this.random = false;
		this.ivs = 10;
		this.attacks = new ArrayList<>();
		this.attackDefault();
	}

	/**
	 * Initializes Pokemon from parameters.
	 * @param id Pokemon ID
	 * @param info Pokemon information array
	 * @param attacksIds List of attack IDs
	 * @param random Whether to generate random stats
	 * @param pokemonLvl Initial level
	 * @throws POOBkemonException if there's an error during creation
	 */
	private void initFromParameters(int id, String[] info, ArrayList<Integer> attacksIds, boolean random, int pokemonLvl) throws POOBkemonException {
		this.id = id;
		this.name = info[1];
		this.idPokedex = info[0];
		this.type = info[2];

		// Level handling
		this.level = random ? (int)(Math.random() * 31) + 25 : pokemonLvl;

		this.levelRequirement = 100;
		this.xp = 0;
		this.active = false;
		this.weak = false;
		this.random = random;
		this.attacks = new ArrayList<>(this.createAttacks(attacksIds));
		this.states = new ArrayList<State>();
		this.ivs = createRandom(32);

		// Base stats
		int baseHP = Integer.parseInt(info[5]);
		int baseAttack = Integer.parseInt(info[6]);
		int baseDefense = Integer.parseInt(info[7]);
		int baseSpAttack = Integer.parseInt(info[8]);
		int baseSpDefense = Integer.parseInt(info[9]);
		int baseSpeed = Integer.parseInt(info[10]);

		// Calculate scaled stats
		if (random) {
			this.maxHealth = calculateHPStat(baseHP, this.level, true);
			this.attack = calculateOtherStat(baseAttack, this.level, true);
			this.defense = calculateOtherStat(baseDefense, this.level, true);
			this.specialAttack = calculateOtherStat(baseSpAttack, this.level, true);
			this.specialDefense = calculateOtherStat(baseSpDefense, this.level, true);
			this.speed = calculateOtherStat(baseSpeed, this.level, true);
		} else {
			this.maxHealth = calculateHPStat(baseHP, this.level);
			this.attack = calculateOtherStat(baseAttack, this.level);
			this.defense = calculateOtherStat(baseDefense, this.level);
			this.specialAttack = calculateOtherStat(baseSpAttack, this.level);
			this.specialDefense = calculateOtherStat(baseSpDefense, this.level);
			this.speed = calculateOtherStat(baseSpeed, this.level);
		}

		this.currentHealth = this.maxHealth;
		if(this.attacks.size() == 0) {
			this.attackDefault();
		}
	}

	/**
	 * Calculates HP stat using Pokemon formula.
	 * @param baseStat Base HP stat
	 * @param level Pokemon level
	 * @param random Whether to use random IVs/EVs
	 * @return Calculated HP stat
	 */
	private int calculateHPStat(int baseStat, int level, boolean random) {
		int iv = random ? (int)(Math.random() * 32) : this.ivs;
		int ev = random ? (int)(Math.random() * 256) : 0;
		return (int)(((2 * baseStat + iv + (ev / 4)) * level) / 100) + level + 10;
	}

	/**
	 * Calculates other stats using Pokemon formula.
	 * @param baseStat Base stat value
	 * @param level Pokemon level
	 * @param random Whether to use random IVs/EVs
	 * @return Calculated stat value
	 */
	private int calculateOtherStat(int baseStat, int level, boolean random) {
		int iv = random ? (int)(Math.random() * 32) : this.ivs;
		int ev = random ? (int)(Math.random() * 256) : 0;
		return (int)(((2 * baseStat + iv + (ev / 4)) * level / 100) + 5);
	}

	// Overloaded stat calculation methods without random flag
	private int calculateHPStat(int baseStat, int level) {
		return calculateHPStat(baseStat, level, false);
	}

	private int calculateOtherStat(int baseStat, int level) {
		return calculateOtherStat(baseStat, level, false);
	}

	/**
	 * Generates a random number up to specified limit.
	 * @param limit Upper bound (exclusive)
	 * @return Random number
	 */
	public int createRandom(int limit) {
		return new Random().nextInt(limit);
	}

	// Getters and setters
	public boolean getActive() { return this.active; }
	public void setActive(boolean active) { this.active = active; }
	public int getId() { return this.id; }
	public String getName() { return this.name; }
	public boolean getWeak() { return this.weak; }
	//public ArrayList<State> getStates() { return this.states; }
	public ArrayList<Attack> getAttacks() { return this.attacks; }

	/**
	 * Gets a specific attack by ID.
	 * @param id Attack ID
	 * @return Attack object or null if not found
	 */
	public Attack getAttack(int id) {
		for(Attack ataque : attacks) {
			if(ataque.getIdInside() == id) {
				return ataque;
			}
		}
		return null;
	}

	/**
	 * Creates attacks from IDs.
	 * @param attacksIds List of attack IDs
	 * @return List of Attack objects
	 * @throws POOBkemonException if attack creation fails
	 */
	private ArrayList<Attack> createAttacks(ArrayList<Integer> attacksIds) throws POOBkemonException {
		ArrayList<Attack> ataques = new ArrayList<>();
		MovesRepository movesRepository = new MovesRepository();

		for(Integer id : attacksIds) {
			String[] infoAttack = movesRepository.getAttacksId(id);
			if(infoAttack[4].equalsIgnoreCase("physical")){
				Attack attack = new Attack(this.nextAttackId(), infoAttack);
				ataques.add(attack);
			}

			if(infoAttack[4].equalsIgnoreCase("special")){
				Attack attack = new special(this.nextAttackId(), infoAttack);
				ataques.add(attack);
			}
			/**
			if(infoAttack[4].equalsIgnoreCase("status")){
				StatusRepository statusRepository = new StatusRepository();
				String [] infoStatus = statusRepository.getStatusByName(infoAttack[8].toUpperCase());
				if(infoAttack[8].equalsIgnoreCase("ally")){
					Attack attack =new StateHeal(this.nextAttackId(),infoStatus);
					ataques.add(attack);
				}
			}
			 */
		}
		return ataques;
	}

	private int nextAttackId() {
		return ++this.attackId;
	}

	/**
	 * Calculates damage dealt by an attack.
	 * @param damage Attack being used
	 * @param attacker Pokemon using the attack
	 * @return Result message string
	 * @throws POOBkemonException if damage calculation fails
	 */
	public String getDamage(Attack damage, Pokemon attacker) throws POOBkemonException {
		if (attacker.currentHealth <= 0) {
			return "";
		}
		//verificar que sea un ataque de estado aplicado al enemigo (this)
		/**
		if (damage instanceof StateAttack) {
			StateAttack stateAttack = (StateAttack) damage;

			// Verifica si se aplica el estado con base en su precisión
			double prob = Math.random() * 100;
			if (prob < stateAttack.getAccuracy()) {
				StatusRepository infoState = new StatusRepository();

				// Obtiene el estado por nombre (ej. "Paralisis")
				String[] info = infoState.getStatusByName(stateAttack.getState());

				if (info != null) {
					try {
						// Crea el objeto State usando la información del CSV
						State estado = new State(info, State.stateType.valueOf(info[0].toUpperCase()));
						if(estado.isPrincipal()){
							this.principalState = estado;
						}else{
							this.states.add(estado); // Aplica el estado al Pokémon actual
						}
					} catch (IllegalArgumentException e) {
						System.err.println("Tipo de estado inválido: " + info[0]);
					}
				}
			}
			String message = " ["+ attacker.getName()+"] dejó afectado a " + this.getName();
			return message;
		}*/


		MovesRepository movesRepository = new MovesRepository();
		StatsRepository statsRepository = new StatsRepository();
		String[] info = movesRepository.getAttackDamageAndType(damage.getIdCSV());

		double multiplicator = statsRepository.getMultiplier(info[0], this.type);
		if (multiplicator == 0.0) {
			attacker.spectorPP();
			return " No afecta a " + this.name + "...";
		}

		if (damage.getAccuracy() < 100 && !doesAttackHit(damage, attacker)) {
			attacker.spectorPP();
			return attacker.name + " falló el ataque!";
		}

		String damageString = "";
		if (multiplicator == 2.0) {
			damageString = " ¡Fue super efectivo! ";
		} else if (multiplicator == 0.5) {
			damageString = " No fue muy efectivo... ";
		}

		double calculatedDamage = calculateDamage(damage, attacker, multiplicator);
		this.currentHealth = Math.max(0, this.currentHealth - (int)calculatedDamage);

		if (this.currentHealth <= 0) {
			this.currentHealth = 0;
			this.weak = true;
		}

		attacker.spectorPP();
		return damageString + " [" + damage.getName() + "] causó " + (int)calculatedDamage + " puntos de daño!";
	}

	/**
	 * Determines if an attack hits.
	 * @param damage Attack being used
	 * @param attacker Pokemon using the attack
	 * @return true if attack hits, false otherwise
	 */
	private boolean doesAttackHit(Attack damage, Pokemon attacker) {
		if (damage.getAccuracy() >= 100) return true;

		double hitProbability = (damage.getAccuracy() / 100.0) *
				(Math.pow(STAGE_MODIFIER, attacker.accuracyStage) / Math.pow(STAGE_MODIFIER, -this.evasionStage));

		hitProbability *= (damage.getAccuracy() <= 30) ? 0.6 : 1.0;
		return Math.random() < Math.max(0.1, Math.min(1.0, hitProbability));
	}

	/**
	 * Calculates damage using Pokemon formula.
	 * @param damage Attack being used
	 * @param attacker Pokemon using the attack
	 * @param typeEffectiveness Type effectiveness multiplier
	 * @return Calculated damage
	 */
	private double calculateDamage(Attack damage, Pokemon attacker, double typeEffectiveness) {
		int power = damage.getPower();
		int level = attacker.level;
		double randomFactor = 0.85 + (Math.random() * 0.15);
		double critical = (Math.random() < CRITICAL_HIT_CHANCE) ? 2 : 1.0;

		double attackStat = damage instanceof special ? attacker.specialAttack : attacker.attack;
		double defenseStat = damage instanceof special ? this.specialDefense : this.defense;
		if (damage instanceof StateHeal) {
			StateHeal state = (StateHeal) damage;
			healsState(state, attacker);
		}


		double damageValue = (((2 * level / 5 + 2) * power * attackStat / defenseStat) / 50 + 2);
		damageValue *= critical * typeEffectiveness * randomFactor;

		return Math.max(1, Math.round(damageValue));
	}

	private void healsState(StateHeal damage, Pokemon attacker){
		int sum = damage.healer();
		attacker.heal(sum);
	}
	private void heal(int sum){
		this.currentHealth += sum;
		if(this.currentHealth>= this.maxHealth){
			this.currentHealth = this.maxHealth;
		}
	}
	private void persistentDamage(State attackStateRival){
		if(principalState == null && attackStateRival.isPrincipal()){
			principalState = attackStateRival;
		}else if(!attackStateRival.isPrincipal()){
			this.states.add(attackStateRival);
		}
	}



	/**
	 * Obtiene la información del Pokémon en un arreglo de Strings.
	 * @return Arreglo con toda la información del Pokémon
	 */
	public String[] getInfo() {
		return new String[] {
				String.valueOf(this.id),           // 0 - ID
				this.name,                         // 1 - Nombre
				this.idPokedex,                    // 2 - ID Pokédex
				this.type,                         // 3 - Tipo
				String.valueOf(this.level),        // 4 - Nivel
				String.valueOf(this.maxHealth),    // 5 - Vida máxima
				String.valueOf(this.currentHealth), // 6 - Vida actual
				String.valueOf(this.attack),        // 7 - Ataque
				String.valueOf(this.defense),      // 8 - Defensa
				String.valueOf(this.specialAttack), // 9 - Ataque Especial
				String.valueOf(this.specialDefense),// 10 - Defensa Especial
				String.valueOf(this.speed),         // 11 - Velocidad
				String.valueOf(this.xp),            // 12 - XP actual
				String.valueOf(this.levelRequirement), // 13 - XP requerido
				String.valueOf(this.active),        // 14 - Estado (activo)
				String.valueOf(this.weak),          // 15 - Pokemon debilitado
				String.valueOf(this.shiny)          // 16 - Pokemon shiny
		};
	}

	/**
	 * Obtiene la información de todos los ataques del Pokémon.
	 * @return Matriz con la información de cada ataque
	 */
	public String[][] getAttacksInfo() {
		int attacksSize = attacks.size();
		String[][] attacksInfo = new String[attacksSize][9];

		for (int i = 0; i < attacksSize; i++) {
			Attack attack = attacks.get(i);
			if (attack != null) {
				attacksInfo[i] = attack.getInfo();
			} else {
				// Valores por defecto si el ataque es null
				attacksInfo[i] = new String[]{
						"Desconocido",  // nombre
						"Normal",       // tipo
						"0",            // poder
						"0",            // precisión
						"0",            // pp
						"0",            // id
						""              // descripción
				};
			}
		}
		return attacksInfo;
	}

	/**
	 * Cura al Pokémon una cantidad específica de HP.
	 * @param heal Cantidad de HP a curar
	 */
	private void heals(int heal) {
		this.currentHealth = this.currentHealth + heal;
		if(this.currentHealth > this.maxHealth) {
			this.currentHealth = this.maxHealth;
		}
	}

	/**
	 * Revive al Pokémon con la mitad de su vida máxima.
	 */
	private void revive() {
		if(this.currentHealth == 0) {
			this.currentHealth = this.maxHealth/2;
		}
	}

	/**
	 * Aplica un efecto al Pokémon según la información proporcionada.
	 * @param info Información del efecto a aplicar
	 */
	public void effect(String[] info) {
		if(info[0].equalsIgnoreCase("Potion") || info[0].equalsIgnoreCase("Revive")) {
			if(info[1].equalsIgnoreCase("Heals")) {
				this.heals(Integer.parseInt(info[2]));
			} else if (info[1].equalsIgnoreCase("Revive")) {
				this.revive();
			}
		}
	}

	/**
	 * Initializes default attack when no attacks are available.
	 * @throws POOBkemonException if default attack cannot be created
	 */
	private void attackDefault() throws POOBkemonException {
		ArrayList<Integer> unickAttack = new ArrayList<>();
		unickAttack.add(357);
		this.attacks = this.createAttacks(unickAttack);
		if(this.attacks.size() == 0) throw new POOBkemonException("Ataque no creado.");
	}

	/**
	 * Checks and resets PP if all attacks are exhausted.
	 * @throws POOBkemonException if default attack cannot be created
	 */
	private void spectorPP() throws POOBkemonException {
		boolean hasPP = false;
		for(Attack attack: this.attacks) {
			if(attack.getPPActual() > 0) {
				hasPP = true;
				break;
			}
		}
		if(!hasPP) {
			this.attacks.clear();
			this.attackDefault();
		}
	}

	/**
	 * Determines if Pokemon is shiny (10% chance).
	 */
	private void probShiny() {
		this.shiny = Math.random() < 0.1;
	}

	/**
	 * Decision auntomatica si se le acaba el tiempo al entrenador
	 */
	public void timeOver() {
		for (Attack at : this.attacks) {
			at.timeOver();
		}
	}
}