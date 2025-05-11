package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POOBkemon {

	protected ArrayList<String> moves;
	protected ArrayList<Integer> order;
	protected boolean finishBattle;
	protected ArrayList<Team> teams;
	protected int nid = 0;
	protected int trainerId = 0;
	protected boolean random;
	protected boolean ok;
	protected static POOBkemon game;
	private int pokemonLvl = 1;



	/**
	 * Constructor del Juego
	 */
	protected POOBkemon() {}

	/**
	 * Obtiene la instancia singleton del juego POOBkemon.
	 * @return Instancia única del juego
	 * @throws IllegalStateException Si el juego no ha sido inicializado
	 */
	public static POOBkemon getInstance() {
		if (game == null) {
			game = new POOBkemon();
		}
		return game;
	}
	/**
	 * Inicializa el juego con los datos proporcionados.
	 * @param trainers Lista de nombres de entrenadores
	 * @param pokemons Mapa de Pokémon por entrenador (clave: nombre entrenador)
	 * @param items Mapa de objetos por entrenador (clave: nombre entrenador)
	 * @param attacks Mapa de ataques por entrenador (clave: nombre entrenador)
	 * @param random Indica si se usan elementos aleatorios
	 * @throws POOBkemonException Si hay errores en los datos de entrada
	 * @throws NullPointerException Si algún parámetro requerido es nulo
	 */
	public void initGame(ArrayList<String> trainers,
						 HashMap<String, ArrayList<Integer>> pokemons,
						 HashMap<String, int[][]> items,
						 HashMap<String, ArrayList<Integer>> attacks,
						 boolean random) throws POOBkemonException{
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
				Trainer train;
				train = createTrainerByType(trainer, bagPack);
				Team team = this.createTeam(this.createPokemons(pokemonIds, trainerAttacks),train );



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
	public void deleteGame(){
		this.game = null;
		this.trainerId = 0;
		this.finishBattle = false;
		this.order = null;
		this.random = false;
		this.teams = null;
		this.moves = null;
		this.ok = true;
	}
	protected void nextIdPokemon(){
		this.nid = this.nid + 1;
	}

	/**
	 * Crea un entrenador según su tipo.
	 * @param trainerName Nombre del entrenador (incluye tipo)
	 * @param bagPack Mochila del entrenador
	 * @return Instancia del entrenador creado
	 * @throws POOBkemonException Si no se puede crear el entrenador
	 */
	private Trainer createTrainerByType(String trainerName, BagPack bagPack) throws POOBkemonException {
		String trainerType = trainerName.replaceAll("\\d+$", "");

		Map<String, CheckedFunction<BagPack, Trainer>> trainerFactories = new HashMap<>();
		trainerFactories.put("Player", bp -> new Trainer(trainerId++, bp));
		trainerFactories.put("Offensive", bp -> new Offensive(trainerId++, bp));
		trainerFactories.put("Defensive", bp -> new Defensive(trainerId++, bp));
		trainerFactories.put("Expert", bp -> new Expert(trainerId++, bp));

		CheckedFunction<BagPack, Trainer> factory = trainerFactories.getOrDefault(
				trainerType,
				bp -> new Random(trainerId++, bp)
		);

		return factory.apply(bagPack);
	}
	/**
	 * Interfaz funcional para métodos que pueden lanzar excepciones.
	 * @param <T> Tipo de entrada
	 * @param <R> Tipo de retorno
	 */
	@FunctionalInterface
	interface CheckedFunction<T, R> {
		R apply(T t) throws POOBkemonException;
	}

	private Trainer createDefensive( BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Defensive(trainerId,bagPack);
		trainerId++;
		return trainer;

	}

	private Trainer createOffensive(  BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Offensive(trainerId,bagPack);
		trainerId++;
		return trainer;

	}

	private Trainer createRandom(BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Random(trainerId,bagPack);
		trainerId++;
		return trainer;

	}

	private Trainer createExpert(BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Expert(trainerId,bagPack);
		trainerId++;
		return trainer;

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
	public ArrayList<Pokemon> createPokemons(ArrayList<Integer> pokemonIds, ArrayList<Integer> attackIds) {
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
	 * Crea un Pokémon con sus características.
	 * @param id ID del Pokémon en el repositorio
	 * @param attackIds Lista de IDs de ataques
	 * @return Pokémon creado
	 */
	public Pokemon createPokemon(int id, ArrayList<Integer> attackIds){
		PokemonRepository info = new PokemonRepository();
		String[] infoPokemon = info.getPokemonId(id);
		Pokemon pokemon = new Pokemon(nid,infoPokemon,attackIds, this.random, this.pokemonLvl);
		this.nextIdPokemon();
		return pokemon;
	}

	/**
	 * Crea un equipo de Pokémon.
	 * @param pokemones Lista de Pokémon del equipo
	 * @return Equipo creado
	 */
	private Team createTeam(ArrayList<Pokemon> pokemones, Trainer trainer){
		Team team = new Team(pokemones, trainer);
		return team;
	}

	/**
	 * crear al entrenador
	 */
	private Trainer createTrainer(Team team, BagPack bagPack) throws POOBkemonException {
		Trainer trainer = new Trainer(trainerId, bagPack);
		this.trainerId++;
		return trainer;
	}

	/**
	 * Determina aleatoriamente el orden de turnos inicial.
	 * @return Lista con IDs de entrenadores en orden de turno
	 */
	public ArrayList<Integer> coin(){
		ArrayList<Integer> turnOrder = new ArrayList<>();
		if (this.teams.size() < 2) return turnOrder;

		int t1 = teams.get(0).getTrainer().getId();
		int t2 = teams.get(1).getTrainer().getId();
		if (Math.random()<0.5) {
			turnOrder.add(t1);
			turnOrder.add(t2);
		} else {
			turnOrder.add(t2);
			turnOrder.add(t1);
		}
		return turnOrder;
	}
	//llama a los entrenadores maquina para que tomen una desicion
	public String[] machineDecision(int idTrainer) throws POOBkemonException {
		for (Team t : teams) {
			if (idTrainer == t.getTrainer().getId()) {
				Trainer trainer = t.getTrainer();

				if (trainer instanceof Machine) {
					String[] desci = ((Machine) trainer).machineMovement(this);
					return desci;
				}
			}
		}
		return null;
	}


	/**
	 * Método para procesar las decisiones de los entrenadores en un turno
	 * @param decisionTrainer Lista de decisiones del entrenador (String)
	 * @throws POOBkemonException Si hay errores en las decisiones
	 */
	public void takeDecision(String[] decisionTrainer) throws POOBkemonException {
		// Validar que haya dos entrenadores
		if (this.teams.size() < 2) {
			throw new POOBkemonException("Se necesitan al menos 2 entrenadores para una batalla");
		}
		// Procesar decisiones en el orden establecido (order)

		String[] decision = decisionTrainer;

		if (decision == null || decision.length == 0) {
			throw new POOBkemonException("Se necesita introducir un movimiento valido");
		}

		String action = decision[0];
		try {
			switch (action) {
				case "Attack":
					if (decision.length < 3) throw new POOBkemonException("Faltan parámetros para Attack");
					System.out.println();
					int attackId = Integer.parseInt(decision[1]);
					int pokemonId1 = Integer.parseInt(decision[2]);
					int trainerId = Integer.parseInt(decision[3]);
					this.attack(attackId,trainerId, pokemonId1);
					//falta agregar la accion a las acciones del movimiento
					break;

				case "UseItem":
					if (decision.length < 3) throw new POOBkemonException("Faltan parámetros para UseItem");
					int itemId = Integer.parseInt(decision[1]);
					int pokemonId = Integer.parseInt(decision[2]);
					this.useItem(Integer.valueOf(decision[1]), itemId, pokemonId);
					moves.add("Player"+decision[1] + " usó ítem " + itemId + " en Pokémon " + pokemonId);
					break;

				case "ChangePokemon":
					if (decision.length < 3) throw new POOBkemonException("Faltan parámetros para ChangePokemon");
					int newPokemonId = Integer.parseInt(decision[2]);
					this.changePokemon(Integer.parseInt(decision[1]), newPokemonId);
					String pokemonName = "";
					for(Team t : this.teams) {
						for(Pokemon p :t.getPokemons()){
							if(p.getId() == newPokemonId){
								pokemonName = p.getName();
							}
						}
					}
					moves.add("Player"+decision[1] + " cambió a Pokémon " + pokemonName);
					break;

				case "Run":
					this.run(Integer.valueOf(decision[1]));
					moves.add("Player"+decision[1] + " huyó de la batalla");
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
			System.out.println("Game Over");
		}
		// Verificar estado de la batalla después de ambos turnos
		checkBattleStatus();
	}
	public boolean isMachine(int TrainerId){
		for (Team t : teams) {
			if(t.getTrainer().getId() == TrainerId){
				if(t.getTrainer() instanceof Machine){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Verifica el estado de la batalla y determina si ha terminado.
	 */
    public void checkBattleStatus() {
		for (Team team : teams) {
			if (team.allFainted()) {
				this.finishBattle = true;
				moves.add("¡Batalla terminada! Equipo " + team + " ha sido derrotado");
				break;
			}
		}
	}

	/**
	 * Cambia el Pokémon activo de un entrenador.
	 * @param trainerId ID del entrenador
	 * @param pokemonId ID del nuevo Pokémon a activar
	 * @throws POOBkemonException Si el entrenador no existe o el Pokémon no es válido
	 */
	public void changePokemon(int trainerId, int pokemonId) throws POOBkemonException {

		// Buscar el entrenador
		Trainer selectedTrainer = null;
		Team team = null;
		for (Team t : this.teams) {

			if (t.getTrainer().getId() == trainerId) {
				selectedTrainer = t.getTrainer();
				team = t;
				break;
			}
		}

		// Verificar si se encontró el entrenador
		if (selectedTrainer == null) {
			throw new POOBkemonException("Entrenador con ID " + trainerId + " no encontrado");
		}

		try {
			// Intentar cambiar el Pokémon
			team.changePokemon(pokemonId);
		} catch (POOBkemonException e) {
			// Relanzar la excepción con contexto adicional
			throw new POOBkemonException("Error al cambiar Pokémon del entrenador " + trainerId + ": " + e.getMessage());
		}
	}

	/**
	 * metodo parasalir de la pelea
	 */
	private void run(int trainer) {
		for (Team team : teams) {
			if (team.getTrainer().getId() == trainer) {
				this.moves.add("GameOver");
				break;
			}
		}
		checkBattleStatus();
		if (finishBattle) {
			System.out.println("Game Over");
		}

	}

	/**
	 * metodo para usar un item
	 */
	private void useItem(Integer trainer, int iditem, int idPokemon) {

	}
	/**
	 * Realiza un ataque entre Pokémon.
	 * @param idAttack ID del ataque a utilizar
	 * @param idThrower ID del Pokémon que realiza el ataque
	 */
	public void attack(int idAttack, int idTrainer, int idThrower) throws POOBkemonException{
		Attack damage = null;
		Pokemon attacker = null;
		Pokemon target = null;
		// Primero: Encontrar el Pokémon atacante (idThrower) en el equipo del entrenador (idTrainer)
		for (Team team : teams) {
			if (team.getTrainer().getId() == idTrainer) { // Buscamos en el equipo del entrenador
				for (Pokemon pokemon : team.getPokemons()) {
					if (pokemon.getId() == idThrower) {
						attacker = pokemon;
						damage = pokemon.getAttack(idAttack);
						break;
					}
				}
				break; // Salimos después de encontrar el equipo correcto
			}
		}

		// Segundo: Encontrar el Pokémon objetivo (el activo del otro equipo)
		for (Team team : teams) {
			if (team.getTrainer().getId() != idTrainer) { // Buscamos en el equipo contrario
				for (Pokemon pokemon : team.getPokemons()) {
					if (pokemon.getActive()) {
						target = pokemon;
						break;
					}
				}
				break; // Solo necesitamos un objetivo activo
			}
		}
		// Verificar que tenemos ambos Pokémon
		if (attacker == null) {
			throw new POOBkemonException("Error: No se encontró el Pokemon atacante");
		}
		if( damage == null){
			throw new POOBkemonException("Error: No se encontró el ataque");
		}
		if (target == null) {
			throw new POOBkemonException("Error: No se encontró un Pokémon objetivo activo");
		}
		// Aplicar el daño
		damage.usePP();
		target.getDamage(damage, attacker);
		// Cambio automático si es necesario
		this.autoChangePokemon();
	}
	//Para cuando est debilitado el pokemon activo haga un cambio automatico
	private void autoChangePokemon(){
		try {
			for (Team team : teams) {
				for (Pokemon pokemon : team.getPokemons()) {
					if (pokemon.getWeak() && pokemon.getActive()) {
						int savePokemon = this.getAlivePokemon(team.getTrainer().getId());
						this.changePokemon(team.getTrainer().getId(), savePokemon);
					}
				}
			}
		}catch (POOBkemonException e){
			System.out.println("Error al cambiar autocambio del Pokemon: " + e.getMessage());
		}
	}
	//para el cambio automatico que encuentre un pokemon Valido
	private int getAlivePokemon(int trainerId){
		int id = 0;
		for (Team team : teams) {
			if(team.getTrainer().getId() == trainerId){
				for(Pokemon p: team.getPokemons()){
					if(!p.getWeak()){
						id = p.getId();
					}
				}
			}
		}
		return id;
	}

	public boolean isOk (){
		return this.ok;
	}
	public ArrayList<String> getMoves(){
		return this.moves;
	}
	public String getLastMoves(){
		return this.moves.getLast();
	}
	public boolean finishBattle(){
		return this.finishBattle;
	}
	public ArrayList<Team> teams(){
		return this.teams;
	}
	public ArrayList<Integer> getOrder(){
		return order;
	}

	/**
	 * Obtiene los Pokémon actualmente activos de cada entrenador.
	 * @return Mapa con ID de entrenador como clave y array de información del Pokémon activo como valor
	 */
	public HashMap<Integer, String[]> getCurrentPokemons() {
		HashMap<Integer, String[]> pokemons = new HashMap<>();
		for (Team t : this.teams) {
			try {
				int trainerid = t.getTrainer().getId();
				Trainer currentTrainer = t.getTrainer();
				Pokemon currentPokemon = t.getPokemonById(currentTrainer.getCurrentPokemonId());// Obtiene el objeto Pokemon
				String[] pokemonInfo = currentPokemon.getInfo();
				pokemons.put(trainerid, pokemonInfo);
			} catch (POOBkemonException e) {
				pokemons.put(t.getTrainer().getId(), new String[]{"No active Pokémon"});
			}
		}
		return pokemons;
	}

	public int[] getPokemonsInactive(int idTrainer) {
		for (Team t : teams) {
			if (t.getTrainer().getId() == idTrainer) {
				ArrayList<Pokemon> pokemons = t.getPokemons();
				int activePokemonId = t.getTrainer().getCurrentPokemonId();
				ArrayList<Integer> inactiveIds = new ArrayList<>();
				for (Pokemon p : pokemons) {
					if (!p.getActive()) {
						inactiveIds.add(Integer.parseInt(p.idPokedex));
					}
				}
				// Convertir ArrayList a arreglo
				int[] result = new int[inactiveIds.size()];
				for (int i = 0; i < inactiveIds.size(); i++) {
					result[i] = inactiveIds.get(i);
				}
				return result;
			}
		}
		return new int[0]; // Si no se encuentra el entrenador
	}

	/**
	 * Obtiene la información de un Pokémon específico de un entrenador.
	 *
	 * @param idTrainer ID del entrenador dueño del Pokémon
	 * @param idPokemon ID del Pokémon a buscar
	 * @return Array de Strings con la información del Pokémon
	 * @throws POOBkemonException Si no se encuentra el entrenador o el Pokémon,
	 *                           o si ocurre un error al acceder a la información
	 */
	public String[] getPokemonInfo(int idTrainer, int idPokemon) throws POOBkemonException {
		// Buscar el entrenador
		for (Team t : teams) {
			if (t.getTrainer().getId() == idTrainer) {
				try {
					String[] pokemon = t.getPokemonById(idPokemon).getInfo();
					return pokemon;
				} catch (POOBkemonException e) {
					try {
						String[] pokemon = t.getPokemonByPOkedex(idPokemon).getInfo();
						return pokemon;
					}catch (POOBkemonException e2){
						throw new POOBkemonException("Error al obtener información del Pokémon: " + e.getMessage());
					}
				}
			}
		}
		throw new POOBkemonException("Entrenador con ID " + idTrainer + " no encontrado");
	}
	public HashMap<Integer, String[][]> getActiveAttacks(){
		HashMap<Integer, String[][]> pokemons = new HashMap<>();
		for (Team t : this.teams) {
			try {
				int trainerid = t.getTrainer().getId();
				Trainer currentTrainer = t.getTrainer();
				Pokemon currentPokemon = t.getPokemonById(currentTrainer.getCurrentPokemonId());// Obtiene el objeto Pokemon
				String[][] pokemonInfo = currentPokemon.getAttacksInfo();
				pokemons.put(trainerid, pokemonInfo);
			} catch (POOBkemonException e) {
				pokemons.put(t.getTrainer().getId(), new String[][]{{"Sin ataques activos"}});
			}
		}
		return pokemons;
	}
	//==================Informacion=======================
	/**
	 * Obtiene información de todos los Pokémon disponibles.
	 * @return Lista de arrays con información de Pokémon
	 */
	public ArrayList<String[]> getPokInfo(){
		ArrayList<String[]> info = new PokemonRepository().getPokemons();
		return info;
	}
	/**
	 * Obtiene información de todos los objetos disponibles.
	 * @return Lista de listas con información de objetos
	 */
	public ArrayList<ArrayList<String>> getItemInfo(){
		ArrayList<ArrayList<String>> info = new ItemRepository().getItems();
		return info;
	}
	/**
	 * Obtiene información de un ataque específico.
	 * @param id ID del ataque
	 * @return String con información del ataque
	 */
	public String getMoveInfo(int id){
		String info = new MovesRepository().getAttackId(id);
		return info;
	}
	public ArrayList<Team> getTeams(){
		return teams;
	}
}
