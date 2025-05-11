package domain;

import java.util.ArrayList;

public class Team {

	private ArrayList<Pokemon> pokemons;
	private Trainer trainer;

	public Team(ArrayList<Pokemon> pokemons, Trainer trainer) {
		this.pokemons = pokemons;
		this.trainer = trainer;
		this.pokemons.get(0).setActive(true);
		this.trainer.setCurrentPokemonId(this.pokemons.get(0).getId());
	}
	public ArrayList<Pokemon> getPokemons() {
		return pokemons;
	}
	/**
	 * Cambia el pokémon activo por el que coincida con el ID especificado
	 * @param id ID del pokémon a activar
	 * @return El pokémon que fue activado
	 * @throws POOBkemonException Si no se encuentra el pokémon o está debilitado
	 */
	public Pokemon changePokemon(int id) throws POOBkemonException {
		Pokemon pokemonToActivate = null;
		Pokemon currentActive = null;

		// Primer recorrido para identificar ambos pokémones
		for (Pokemon pokemon : pokemons) {
			if (pokemon.getActive()) {
				currentActive = pokemon;
			}
			if (pokemon.getId() == id) {
				pokemonToActivate = pokemon;
			}
		}
		// Validaciones
		if (pokemonToActivate == null) {
			throw new POOBkemonException( POOBkemonException.POKEMON_ID_NOT_FOUND + id);
		}
		if (pokemonToActivate.currentHealth <= 0) {
			throw new POOBkemonException(POOBkemonException.POKEMON_WEAK_CHANGE);
		}
		// Cambiar estados
		if (currentActive != null) {
			currentActive.setActive(false);
		}
		pokemonToActivate.setActive(true);
		this.trainer.setCurrentPokemonId(pokemonToActivate.getId());
		return pokemonToActivate;
	}
	public boolean allFainted(){
		for (Pokemon pokemon : pokemons) {
			if(!pokemon.getWeak()) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Obtiene los IDs de los Pokémon inactivos en el equipo, excluyendo al Pokémon actual.
	 * @param currentPokemon ID del Pokémon actualmente activo (que se excluirá de los resultados)
	 * @return Arreglo de enteros con los IDs de los Pokémon inactivos
	 */
	public int[] getPokemonsInactive(int currentPokemon) {
		// Lista para almacenar los IDs de los Pokémon inactivos
		ArrayList<Integer> inactiveList = new ArrayList<>();

		// Filtrar Pokémon inactivos que no sean el actual
		for (Pokemon pokemon : pokemons) {
			if(!pokemon.getActive()) {
				inactiveList.add(Integer.parseInt(pokemon.idPokedex));
			}
		}
		// Convertir ArrayList a array primitivo
		int[] inactivePokemons = new int[inactiveList.size()];
		for (int i = 0; i < inactiveList.size(); i++) {
			inactivePokemons[i] = inactiveList.get(i);
		}

		return inactivePokemons;
	}
	public Pokemon getPokemonById(int id) throws POOBkemonException {
		for (Pokemon p : pokemons) {
			if (p.getId() == id) {
				return p;
			}
		}
		throw new POOBkemonException("Pokémon con ID " + id + " no encontrado");
	}
	public Pokemon getPokemonByPOkedex(int id) throws POOBkemonException {
		for (Pokemon p : pokemons) {
			if (Integer.parseInt(p.idPokedex) == id) {
				return p;
			}
		}
		throw new POOBkemonException("Pokémon con ID " + id + " no encontrado");
	}
	public Trainer getTrainer() {
		return trainer;
	}

	public void useItem(int idPokemon,String datoItem) throws POOBkemonException{
		Pokemon pokemonTarget = null;
		for(Pokemon p : pokemons){
			if(p.getId() == idPokemon){
				pokemonTarget = p;
				break;
			}
		}
		//lanza error si no lo encuentra
		if(pokemonTarget == null){
			throw new POOBkemonException("Pokémon con ID " + idPokemon + " no encontrado en el Team");
		}
		this.getTrainer().useItem(pokemonTarget, datoItem);
	}

}
