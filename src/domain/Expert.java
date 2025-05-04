package domain;

public class Expert extends Machine {

	private POOBkemon poobkemon;

	public Expert(int id,Team team, BagPack bagPack, POOBkemon poobkemon) throws POOBkemonException {
        super(id,team,bagPack);
		this.poobkemon = poobkemon;
	}
	@Override
	public String takeDescicion() {
		return null;
	}

}
