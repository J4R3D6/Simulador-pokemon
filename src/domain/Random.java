package domain;

public class Random extends Machine {
    private BagPack bagPack;
    private Team team;
    public Random(Team team, BagPack bagPack) {
        this.bagPack = bagPack;
        this.team = team;
    }
    @Override
    public String takeDescicion() {
        return null;
    }

}
