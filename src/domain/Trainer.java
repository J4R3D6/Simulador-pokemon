package domain;

import java.util.List;

public class Trainer {

	private int currentPokemonId = -1;  // Ahora es solo el ID (int)
	private BagPack bagPack;
	private Team team;
	private int id;

	public Trainer(int id, Team team, BagPack bagPack) throws POOBkemonException {
		// Validación de parámetros nulos
		if (team == null) {
			throw new POOBkemonException(POOBkemonException.NULL_TEAM);
		}
		if (bagPack == null) {
			throw new POOBkemonException(POOBkemonException.NULL_BAGPACK);
		}

		// Validación de equipo vacío
		List<Pokemon> pokemons = team.getPokemons();
		if (pokemons == null || pokemons.isEmpty()) {
			throw new POOBkemonException(POOBkemonException.EMPTY_TEAM);
		}

		// Validación del primer pokémon
		Pokemon firstPokemon = pokemons.get(0);
		if (firstPokemon == null) {
			throw new POOBkemonException(POOBkemonException.NULL_POKEMON);
		}
		if (firstPokemon.currentHealth <= 0) {
			throw new POOBkemonException(POOBkemonException.INVALID_FIRST_POKEMON);
		}

		// Establecer el primer Pokémon como activo (guardando solo su ID)
		firstPokemon.setActive(true);
		this.currentPokemonId = firstPokemon.getId();  // Solo guardamos el ID
		this.id = id;
		this.team = team;
		this.bagPack = bagPack;
	}

	public int getId() {
		return id;
	}

	/**
	 * Cambia el pokémon activo al pokémon con el ID especificado.
	 */
	public void changePokemon(int newPokemonId) throws POOBkemonException {
		// Validación básica
		if (currentPokemonId == newPokemonId) {
			return;  // No hacer nada si es el mismo Pokémon
		}

		// Verificar que el nuevo Pokémon existe en el equipo
		Pokemon newPokemon = team.getPokemonById(newPokemonId);
		if (newPokemon == null) {
			throw new POOBkemonException("El Pokémon con ID " + newPokemonId + " no existe en el equipo");
		}

		// Verificar que el nuevo Pokémon no está debilitado
		if (newPokemon.currentHealth <= 0) {
			throw new POOBkemonException("No se puede cambiar a un Pokémon debilitado");
		}

		try {
			// Desactivar el Pokémon actual (si existe)
			if (currentPokemonId != -1) {
				Pokemon currentPokemon = team.getPokemonById(currentPokemonId);
				if (currentPokemon != null) {
					currentPokemon.setActive(false);
				}
			}

			// Activar el nuevo Pokémon
			newPokemon.setActive(true);
			this.currentPokemonId = newPokemon.getId();

		} catch (POOBkemonException e) {
			// En caso de error, revertir cambios
			if (currentPokemonId != -1) {
				Pokemon currentPokemon = team.getPokemonById(currentPokemonId);
				if (currentPokemon != null) {
					currentPokemon.setActive(true);
				}
			}
			throw new POOBkemonException("Error al cambiar de Pokémon: " + e.getMessage());
		}
	}

	public int getCurrentPokemonId() {
		return currentPokemonId;
	}

	public Pokemon getCurrentPokemon() throws POOBkemonException {
		return team.getPokemonById(currentPokemonId);
	}

	public BagPack getBagPack() {
		return bagPack;
	}

	public Team getTeam() {
		return team;
	}

	public String[] activePokemon() {
		try {
			Pokemon current = team.getPokemonById(currentPokemonId);
			return current != null ? current.getInfo() : new String[]{"No active Pokémon"};
		} catch (POOBkemonException e) {
			return new String[]{"Error: " + e.getMessage()};
		}
	}

	public int[] getPokemonsInactive(){
		return team.getPokemonsInactive(this.currentPokemonId);
	}

	public Item getItem(int id) {
		return null;  // Implementar lógica según sea necesario
	}
}