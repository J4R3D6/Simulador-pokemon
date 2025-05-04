package domain;

public class Expert extends Machine {

	private POOBkemon poobkemon;

	public Expert(Team team, BagPack bagPack, POOBkemon poobkemon) throws POOBkemonException {
        super(team,bagPack);
		this.poobkemon = poobkemon;
	}
	@Override
	public String takeDescicion() {
		return null;
	}

}
