package domain;

import java.util.ArrayList;
import java.util.HashMap;
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
	private boolean random;
	private boolean ok;


	/**
	 * Constructor del Juego
	 */
	public POOBkemon(ArrayList<String> trainers,
					 HashMap<String, ArrayList<Integer>> pokemons,
					 HashMap<String, String[][]> items,
					 HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks,
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
				String[][] trainerItems = items.get(trainer);
				HashMap<Integer, ArrayList<Integer>> trainerAttacks = attacks.get(trainer);

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
		Trainer trainer = new Defensive(team,bagPack);
		return trainer;

	}

	private Trainer createOffensive(Team team,  BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Offensive(team,bagPack);
		return trainer;

	}

	private Trainer createRandom(Team team,  BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Random(team,bagPack);
		return trainer;

	}

	private Trainer createExpert(Team team,  BagPack bagPack, POOBkemon juego) throws POOBkemonException {
		Trainer trainer = new Expert(team,bagPack, juego);
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
				info[i][j] = String.format("%d. %s (Nivel: %d, HP: %d/%d)", //esto es para los test, pero se edita para conectar con la GUI
						p.getId(), p.getName(), p.level, p. currentHealth, p.maxHealth);
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

	private ArrayList<Item> createItems(String[][] item) {
		int id = 0;
		ArrayList<Item> items = new ArrayList<Item>();
		for(String[] i: item){
			Item ite = new Item(Integer.parseInt(i[0]), Integer.parseInt(i[1]));
			items.add(ite);
			id++;
		}
		return items;
	}

	/**
	 * crear los Pokemons
	 */
	private ArrayList<Pokemon> createPokemons(ArrayList<Integer> pokemons, HashMap<Integer,ArrayList<Integer>> atacksIds) {
		ArrayList<Pokemon> poks = new ArrayList<Pokemon>();
		for(Integer id:pokemons){
			Pokemon pokemon = this.createPokemon(id, atacksIds.get(id));
			poks.add(pokemon);
		}
		return poks;

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
		Trainer trainer = new Trainer(team, bagPack);
		return trainer;
	}

	/**
	 * metodo que decide quien inicia
	 */
	private ArrayList<Trainer> coin() {
		return null;
	}

	/**
	 * metodo de toma de decisiones
	 */
	public void takeDescicion(String[] trainer1, String[] trainer2) {

	}

	/**
	 * metodo para cambiar de pokemon
	 */
	private void changePokemon(Trainer trainer, int id) {

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
