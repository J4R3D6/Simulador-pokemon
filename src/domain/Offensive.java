package domain;

public class Offensive extends Machine {

    public Offensive(int id,Team team, BagPack bagPack) throws POOBkemonException {
        super(id,team,bagPack);

    }
    @Override
    public String takeDescicion() {
        return null;
    }

}
