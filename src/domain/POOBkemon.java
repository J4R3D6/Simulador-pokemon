package domain;

import java.util.ArrayList;
import java.util.HashMap;

public class POOBkemon {

	protected ArrayList<String> moves;
	protected ArrayList<Trainer> order;
	protected boolean finshBattle;
	private ArrayList<Team> teams;
	private ArrayList<Trainer> trainers;
	private ArrayList<BagPack> bagPacks;
	private int nid = 0;
	private boolean random;
	private boolean ok;


	/**
	 * Constructor del Juego
	 */
	public POOBkemon(ArrayList<String> trainers, HashMap<String,ArrayList<Integer>> pokemons, HashMap<String,String[][]> items, HashMap<String,HashMap<Integer,ArrayList<Integer>>> atacks, boolean random) {

		this.trainers = new ArrayList<Trainer>();
		this.bagPacks = new ArrayList<BagPack>();
		this.teams = new ArrayList<Team>();
		this.finshBattle = false;
		this.random = random;

		for(String trainer : trainers) {
			BagPack bagPack = this.createBagPack(this.creatItems(items.get(trainer)));
			Team team = this.createTeam(this.createPokemons(pokemons.get(trainer), atacks.get(trainer)));
			Trainer train;
			if(!trainer.contains("Player")){
				train = this.createTrainer(team, bagPack);
			}else{
				if(trainer.contains("Ofensive")){
					train = this.createOfensive(team, bagPack);
				}
				else if (trainer.contains("Defensive")) {
					train = this.createDefensive(team, bagPack);
				} else if (trainer.contains("Expert")) {
					train = this.createExpert(team, bagPack,this);
				} else {
					train = this.createRandom(team, bagPack);
				}
			}
			this.trainers.add(train);
			this.bagPacks.add(bagPack);
			this.teams.add(team);
		}

		this.order = this.coin();
		this.moves = new ArrayList<String>();
		this.moves.add("Start Game");
		this.ok = true;
	}

	private Trainer createDefensive(Team team,  BagPack bagPack) {
		Trainer trainer = new Defensive(team,bagPack);
		return trainer;

	}

	private Trainer createOfensive(Team team,  BagPack bagPack) {
		Trainer trainer = new Ofensive(team,bagPack);
		return trainer;

	}

	private Trainer createRandom(Team team,  BagPack bagPack) {
		Trainer trainer = new Random(team,bagPack);
		return trainer;

	}

	private Trainer createExpert(Team team,  BagPack bagPack, POOBkemon juego) {
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
				info[i][j] = String.format("%d. %s (Nivel: %d, HP: %d/%d)",
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

	private ArrayList<Item> creatItems(String[][] item) {
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
	private Trainer createTrainer(Team team, BagPack bagPack) {
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
		return this.finshBattle;
	}
	public ArrayList<Team> teams(){
		return this.teams;
	}

}
