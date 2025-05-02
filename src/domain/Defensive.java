package domain;

public class Defensive extends Machine {
    private BagPack bagPack;
    private Team team;

    public Defensive(Team team, BagPack bagPack) {
        this.bagPack = bagPack;
        this.team = team;
    }
    @Override
    public String takeDescicion() {
        return null;
    }
}
