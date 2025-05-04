package domain;

public abstract class Machine extends Trainer {
	public Machine(Team team, BagPack bagPack) throws POOBkemonException {
		super(team, bagPack);
	}

	public abstract String takeDescicion();
}
