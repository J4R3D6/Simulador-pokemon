package domain;

import java.util.List;

public class Trainer {

	private int currentPokemonId = -1;  // Ahora es solo el ID (int)
	private BagPack bagPack;
	private int id;

	public Trainer(int id, BagPack bagPack) throws POOBkemonException {
		if (bagPack == null) {
			throw new POOBkemonException(POOBkemonException.NULL_BAGPACK);
		}
		this.id = id;
		this.bagPack = bagPack;
	}

	public int getId() {
		return id;
	}
	public BagPack getBagPack() {
		return bagPack;
	}
	public int getCurrentPokemonId() {
		return currentPokemonId;
	}
	public void setCurrentPokemonId(int currentPokemonId) {
		this.currentPokemonId = currentPokemonId;
	}

	public Item getItem(int id) {
		return null;
	}
}