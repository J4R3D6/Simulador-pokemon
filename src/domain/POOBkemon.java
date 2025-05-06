package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

public class POOBkemon {

	protected ArrayList<String> moves;
	protected ArrayList<Trainer> order;
	protected boolean finishBattle;
	private ArrayList<Team> teams;
	private ArrayList<Trainer> trainers;
	private ArrayList<BagPack> bagPacks;
	private int nid = 0;
	private int trainerId = 0;
	private boolean random;
	private boolean ok;


	/**
	 * Constructor del Juego
	 */
	public POOBkemon(ArrayList<String> trainers,
					 HashMap<String, ArrayList<Integer>> pokemons,
					 HashMap<String, int[][]> items,
					 HashMap<String,ArrayList<Integer>> attacks,
					 boolean random) throws POOBkemonException {

		// Validar datos básicos
		if (trainers == null || trainers.isEmpty()) {
			throw new POOBkemonException(POOBkemonException.MISSING_TRAINER_DATA);
		}
		if (pokemons == null || pokemons.isEmpty()) {
			throw new POOBkemonException(POOBkemonException.MISSING_POKEMON_DATA);
		}
		if (items == null || items.isEmpty()) {
			throw new POOBkemonException(POOBkemonException.MISSING_ITEMS_DATA);
		}

		this.trainers = new ArrayList<>();
		this.bagPacks = new ArrayList<>();
		this.teams = new ArrayList<>();
		this.finishBattle = false;
		this.random = random;

		try {
			for (String trainer : trainers) {
				// Validar que el entrenador tenga datos asociados (sino despues dará error)
				if (!pokemons.containsKey(trainer)){
					throw new POOBkemonException(POOBkemonException.INCOMPLETE_DATA + " Entrenador: " + trainer);
				}

				ArrayList<Integer> pokemonIds = pokemons.get(trainer);
				int[][] trainerItems = items.get(trainer);
				ArrayList<Integer> trainerAttacks = attacks.get(trainer);

				// Validar formatos
				if (pokemonIds == null || trainerItems == null || trainerAttacks == null) {
					throw new POOBkemonException(POOBkemonException.INCOMPLETE_DATA);
				}

				BagPack bagPack = this.createBagPack(this.createItems(trainerItems));
				Team team = this.createTeam(this.createPokemons(pokemonIds, trainerAttacks));
				Trainer train;


				if (!trainer.contains("Player")) {
					train = this.createTrainer(team, bagPack);
				} else {
					String trainerName = trainer.replace("Player", "");  // Ej: "PlayerOffensive1" -> "Offensive1"
					train = createTrainerByType(trainerName, team, bagPack);
				}

				this.trainers.add(train);
				this.bagPacks.add(bagPack);
				this.teams.add(team);
			}

			this.order = this.coin();
			this.moves = new ArrayList<>();
			this.moves.add("Start Game");
			this.ok = true;

		} catch (NullPointerException | NumberFormatException e) {
			throw new POOBkemonException(POOBkemonException.INVALID_FORMAT + ": " + e.getMessage());
		}
	}

	private Trainer createTrainerByType(String trainerName, Team team, BagPack bagPack)
			throws POOBkemonException {

		String trainerType = trainerName.replaceAll("\\d+$", "");

		// 1. Pre-crear las funciones sin lógica de excepciones
		Map<String, CheckedFunction<Team, Trainer>> trainerFactories = new HashMap<>();
		trainerFactories.put("Offensive", t -> createOffensive(t, bagPack));
		trainerFactories.put("Defensive", t -> createDefensive(t, bagPack));
		trainerFactories.put("Expert", t -> createExpert(t, bagPack, this));

		// 2. Obtener la fábrica o usar random por defecto
		CheckedFunction<Team, Trainer> factory = trainerFactories.getOrDefault(
				trainerType,
				t -> createRandom(t, bagPack)
		);

		try {
			return factory.apply(team);
		} catch (POOBkemonException e) {
			throw e; // Re-lanzar la misma excepción
		}
	}

	// Interfaz funcional para métodos que lanzan excepciones
	//Segun lo leido esto es lo mejor
	@FunctionalInterface
	interface CheckedFunction<T, R> {
		R apply(T t) throws POOBkemonException;
	}

	private Trainer createDefensive(Team team,  BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Defensive(trainerId,team,bagPack);
		trainerId++;
		return trainer;

	}

	private Trainer createOffensive(Team team,  BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Offensive(trainerId,team,bagPack);
		trainerId++;
		return trainer;

	}

	private Trainer createRandom(Team team,  BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Random(trainerId,team,bagPack);
		return trainer;

	}

	private Trainer createExpert(Team team,  BagPack bagPack, POOBkemon juego) throws POOBkemonException {
		Trainer trainer = new Expert(trainerId,team,bagPack, juego);
		trainerId++;
		return trainer;

	}
	/**
	 * Da los pokemones por entrenador
	 */
	/**
	 * Información de todos los pokémones por entrenador
	 * @return Matriz con información de pokémones por entrenador
	 */
	public String[][] pokemonPerTrainer() {
		String[][] info = new String[teams.size()][];

		for (int i = 0; i < teams.size(); i++) {
			Team team = teams.get(i);
			ArrayList<Pokemon> pokemons = team.getPokemons();
			info[i] = new String[pokemons.size()];

			for (int j = 0; j < pokemons.size(); j++) {
				Pokemon p = pokemons.get(j);
				info[i][j] = String.format("%d. %s (Nivel: %d, HP: %.1f/%d)",
						p.getId(), p.getName(), p.level, p.currentHealth, p.maxHealth);

			}
		}

		return info;
	}

	/**
	 * informacion de todo los pokemons
	 */
	public String[][] pokemones() {
		return null;
	}

	/**
	 * crear la maleta
	 */
	private BagPack createBagPack(ArrayList<Item> items) {
		BagPack bagPack = new BagPack(items);
		return bagPack;
	}

	private ArrayList<Item> createItems(int[][] item) {
		int id = 0;
		ArrayList<Item> items = new ArrayList<Item>();
		for(int[] i: item){
			Item ite = new Item(i[0], i[1]);
			items.add(ite);
			id++;
		}
		return items;
	}

	/**
	 * crear los Pokemons
	 */
	private ArrayList<Pokemon> createPokemons(ArrayList<Integer> pokemonIds, ArrayList<Integer> attackIds) {
		ArrayList<Pokemon> pokemons = new ArrayList<>();
		final int ATTACKS_PER_POKEMON = 4;
		int totalAttacksNeeded = pokemonIds.size() * ATTACKS_PER_POKEMON;

		// Verificar si hay suficientes ataques
		if (attackIds.size() < totalAttacksNeeded) {
			throw new IllegalArgumentException(
					"No hay suficientes ataques. Se necesitan " + totalAttacksNeeded +
							", pero solo hay " + attackIds.size()
			);
		} // hacer la exception en la clase correspondiente.
		int attackIndex = 0;
		for (Integer pokemonId : pokemonIds) {
			// Obtener 4 ataques consecutivos para este Pokémon
			List<Integer> attacksForPokemon = attackIds.subList(
					attackIndex,
					attackIndex + ATTACKS_PER_POKEMON
			);

			// Crear el Pokémon con sus ataques (asumo que createPokemon acepta una lista)
			Pokemon pokemon = this.createPokemon(pokemonId, new ArrayList<>(attacksForPokemon));
			pokemons.add(pokemon);
			// Avanzar al siguiente bloque de 4 ataques
			attackIndex += ATTACKS_PER_POKEMON;
		}
		return pokemons;
	}

	/**
	 * crear el pokemon
	 */
	private Pokemon createPokemon(int id, ArrayList<Integer> attackIds) {
		PokemonRepository info = new PokemonRepository();
		String[] infoPokemon = info.getPokemonId(id);
		Pokemon pokemon = new Pokemon(nid,infoPokemon,attackIds, this.random);
		nid++;
		return pokemon;
	}

	/**
	 * crear el team
	 */
	private Team createTeam(ArrayList<Pokemon> pokemones) {
		Team team = new Team(pokemones);
		return team;
	}

	/**
	 * crear al entrenador
	 */
	private Trainer createTrainer(Team team, BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Trainer(trainerId,team, bagPack);
		this.trainerId++;
		return trainer;
	}

	/**
	 * metodo que decide quien inicia
	 */
	private ArrayList<Trainer> coin() {
		return null;
	}

	/**
	 * Método para procesar las decisiones de los entrenadores en un turno
	 * @param decisionTrainer1 Acción del primer entrenador [acción, parámetro1, parámetro2]
	 * @param decisionTrainer2 Acción del segundo entrenador [acción, parámetro1, parámetro2]
	 * @throws POOBkemonException Si hay errores en las decisiones
	 */
	public void takeDecision(String[] decisionTrainer1, String[] decisionTrainer2) throws POOBkemonException {
		// Validar que haya dos entrenadores
		if (trainers.size() < 2) {
			throw new POOBkemonException("Se necesitan al menos 2 entrenadores para una batalla");
		}

		Trainer trainer1 = trainers.get(0);
		Trainer trainer2 = trainers.get(1);

		// Procesar decisiones en el orden establecido (order)
		for (Trainer currentTrainer : order) {
			String[] decision = currentTrainer.equals(trainer1) ? decisionTrainer1 : decisionTrainer2;

			if (decision == null || decision.length == 0) {
				continue; // Si no hay decisión, pasar al siguiente
			}

			String action = decision[0];
			try {
				switch (action) {
					case "Attack":
						if (decision.length < 3) throw new POOBkemonException("Faltan parámetros para Attack");
						int attackId = Integer.parseInt(decision[1]);
						int targetId = Integer.parseInt(decision[2]);
						this.attack(attackId, targetId);
						moves.add(currentTrainer + " usó ataque " + attackId + " contra " + targetId);
						break;

					case "UseItem":
						if (decision.length < 3) throw new POOBkemonException("Faltan parámetros para UseItem");
						int itemId = Integer.parseInt(decision[1]);
						int pokemonId = Integer.parseInt(decision[2]);
						this.useItem(currentTrainer, itemId, pokemonId);
						moves.add(currentTrainer + " usó ítem " + itemId + " en Pokémon " + pokemonId);
						break;

					case "ChangePokemon":
						if (decision.length < 2) throw new POOBkemonException("Faltan parámetros para ChangePokemon");
						int newPokemonId = Integer.parseInt(decision[1]);
						this.changePokemon(currentTrainer.getId(), newPokemonId);
						moves.add(currentTrainer + " cambió a Pokémon " + newPokemonId);
						break;

					case "Run":
						this.run(currentTrainer);
						moves.add(currentTrainer + " huyó de la batalla");
						this.finishBattle = true;
						break;

					default:
						throw new POOBkemonException("Acción no reconocida: " + action);
				}
			} catch (NumberFormatException e) {
				throw new POOBkemonException("Formato inválido en parámetros: " + e.getMessage());
			}

			// Verificar si la batalla ha terminado después de cada acción
			if (finishBattle) {
				break;
			}
		}

		// Verificar estado de la batalla después de ambos turnos
		checkBattleStatus();
	}

	private void checkBattleStatus() {
		for (Team team : teams) {
			if (team.allFainted()) {
				this.finishBattle = true;
				moves.add("¡Batalla terminada! Equipo " + team + " ha sido derrotado");
				break;
			}
		}
	}

	/**
	 * metodo para cambiar de pokemon
	 */
	private void changePokemon(int trainer, int id) {
		Trainer selectedTrainer = null;
		for(Trainer t: this.trainers){
			if(t.getId() == trainer){
				selectedTrainer = t;
			}
		}
		selectedTrainer.changePokemon(id);

	}

	/**
	 * metodo parasalir de la pelea
	 */
	private void run(Trainer trainer) {

	}

	/**
	 * metodo para usar un item
	 */
	private void useItem(Trainer trainer, int iditem, int idPokemon) {

	}

	private void attack(int idAttack, int idThrower) {
		int damage = 0;
		for (Team team : teams) {
			for(Pokemon pokemon: team.getPokemons()){
				if(pokemon.getId() == idThrower){
					damage = pokemon.attack;
				}
			}
			for(Pokemon pokemon : team.getPokemons()) {
				if(pokemon.getActive() && pokemon.getId() != idThrower){
					pokemon.getDamage(damage,idAttack);
				}
			}
		}
	}

	public String[][] attacks() {
		return null;
	}

	public String[][] items() {
		return null;
	}

	public String[][] states() {
		return null;
	}

	public boolean isOk (){
		return this.ok;
	}
	public ArrayList<String> getMoves(){
		return this.moves;
	}

	public boolean finishBattle(){
		return this.finishBattle;
	}
	public ArrayList<Team> teams(){
		return this.teams;
	}

}
