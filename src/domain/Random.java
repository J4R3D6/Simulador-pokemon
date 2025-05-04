package domain;

public class Random extends Machine {
    private BagPack bagPack;
    private Team team;
    public Random(int id,Team team, BagPack bagPack) throws POOBkemonException {
        super(id,team,bagPack);
    }
    @Override
    public String takeDescicion() {
        return null;
    }

}
