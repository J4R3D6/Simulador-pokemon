package domain;

public class Defensive extends Machine {

    public Defensive(Team team, BagPack bagPack) throws POOBkemonException {
        super(team,bagPack);
    }
    @Override
    public String takeDescicion() {
        return null;
    }
}
