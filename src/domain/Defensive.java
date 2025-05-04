package domain;

public class Defensive extends Machine {

    public Defensive(int id,Team team, BagPack bagPack) throws POOBkemonException {
        super(id,team,bagPack);
    }
    @Override
    public String takeDescicion() {
        return null;
    }
}
