package domain;

import java.util.List;

public class Trainer {

	private Pokemon currentPokemon;

	private BagPack bagPack;

	private Team team;

	private int id;

	public Trainer(int id,Team team, BagPack bagPack) throws POOBkemonException {

		// Validación de parámetros nulos
		if (team == null) {
			throw new POOBkemonException(POOBkemonException.NULL_TEAM);
		}
		if (bagPack == null) {
			throw new POOBkemonException(POOBkemonException.NULL_BAGPACK);
		}

		// Validación de equipo vacío (debe tener equipo, sino como juega?)
		List<Pokemon> pokemons = team.getPokemons();
		if (pokemons == null || pokemons.isEmpty()) {
			throw new POOBkemonException(POOBkemonException.EMPTY_TEAM);
		}

		// Validación del primer pokémon (no debe ser nulo)
		Pokemon firstPokemon = pokemons.get(0);
		if (firstPokemon == null) {
			throw new POOBkemonException(POOBkemonException.NULL_POKEMON);
		}
		//el pokemon al asignarse, debe estar no debilitado
		if (firstPokemon.currentHealth <= 0) {
			throw new POOBkemonException(POOBkemonException.INVALID_FIRST_POKEMON);
		}

		firstPokemon.setActive(true);

		this.id = id;
		this.team = team;
		this.bagPack = bagPack;
		this.currentPokemon = firstPokemon;
	}
	public int getId() {
		return id;
	}
	/**
	 * Cambia el pokémon activo al pokémon con el ID especificado.
	 * Si ocurre un error, mantiene el pokémon actual activo.
	 * @param id ID del pokémon al que se desea cambiar
	 */
	public void changePokemon(int id) {
		// Validación para evitar cambio innecesario
		if (currentPokemon != null && currentPokemon.getId() == id) {
			currentPokemon.setActive(true);  // Asegurar que sigue activo
			return;
		}

		try {
			Pokemon newPokemon = this.team.changePokemon(id);

			if (currentPokemon != null) {
				currentPokemon.setActive(false);
			}
			newPokemon.setActive(true);
			this.currentPokemon = newPokemon;

		} catch (POOBkemonException e) {
			// Mantener el pokémon actual activo
			if (currentPokemon != null) {
				currentPokemon.setActive(true); // Reforzar estado activo
			}
			// Opcional: Registrar el error
			System.err.println("Error al cambiar pokémon: " + e.getMessage());
		}
	}

	public Pokemon getCurrentPokemon() {
		return currentPokemon;
	}

	public BagPack getBagPack() {
		return bagPack;
	}

	public Team getTeam() {
		return team;
	}

	public String[] activePokemon(){
		return this.currentPokemon.getInfo();
	}

	public Item getItem(int id) {
		return null;
	}

}
