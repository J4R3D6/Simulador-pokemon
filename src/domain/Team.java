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
	public Pokemon getPokemon(int id) {
		return null;
	}

}
