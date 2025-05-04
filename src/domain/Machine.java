package domain;

public abstract class Machine extends Trainer {
	public Machine(int id,Team team, BagPack bagPack) throws POOBkemonException {
		super(id,team, bagPack);
	}

	public abstract String takeDescicion();
}
