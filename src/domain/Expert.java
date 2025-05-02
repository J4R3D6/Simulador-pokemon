package domain;

public class Expert extends Machine {

	private POOBkemon poobkemon;
	private BagPack bagPack;
	private Team team;

	public Expert(Team team, BagPack bagPack, POOBkemon poobkemon) {
		this.bagPack = bagPack;
		this.team = team;
		this.poobkemon = poobkemon;
	}
	@Override
	public String takeDescicion() {
		return null;
	}

}
