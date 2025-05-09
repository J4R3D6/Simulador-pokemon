package domain;

public class Defensive extends Machine {

    public Defensive(int id, BagPack bagPack) throws POOBkemonException {
        super(id,bagPack);
    }
    @Override
    public String takeDescicion() {
        return null;
    }
}
