package domain;

public class Random extends Machine {
    private BagPack bagPack;
    private Team team;
    public Random(Team team, BagPack bagPack) throws POOBkemonException {
        super(team,bagPack);
    }
    @Override
    public String takeDescicion() {
        return null;
    }

}
