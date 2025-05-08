package domain;

import java.util.ArrayList;

public class Team {

	private ArrayList<Pokemon> pokemons;

	public Team(ArrayList<Pokemon> pokemons) {
		this.pokemons = pokemons;
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
		ArrayList<Integer> inactivePokemons = new ArrayList<>();

		// Filtrar Pokémon inactivos que no sean el actual
		for (Pokemon pokemon : pokemons) {
			if(!pokemon.getActive() && pokemon.getId() != currentPokemon) {
				inactivePokemons.add(pokemon.getId());
			}
		}

		// Convertir ArrayList<Integer> a int[]
		int[] result = new int[inactivePokemons.size()];
		for (int i = 0; i < inactivePokemons.size(); i++) {
			result[i] = inactivePokemons.get(i);
		}

		return result;
	}
	public Pokemon getPokemonById(int id) throws POOBkemonException {
		for (Pokemon p : pokemons) {
			if (p.getId() == id) {
				return p;
			}
		}
		throw new POOBkemonException("Pokémon con ID " + id + " no encontrado");
	}

}
