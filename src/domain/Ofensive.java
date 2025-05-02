package domain;

public class Ofensive extends Machine {
    private BagPack bagPack;
    private Team team;

    public Ofensive(Team team, BagPack bagPack) {
        this.bagPack = bagPack;
        this.team = team;
    }
    @Override
    public String takeDescicion() {
        return null;
    }

}
