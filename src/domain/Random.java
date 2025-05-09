package domain;

public class Random extends Machine {
    private BagPack bagPack;
    private Team team;
    public Random(int id, BagPack bagPack) throws POOBkemonException {
        super(id,bagPack);
    }
    @Override
    public String[] machineMovement( POOBkemon game)  throws POOBkemonException {
        return null;
    }

}
