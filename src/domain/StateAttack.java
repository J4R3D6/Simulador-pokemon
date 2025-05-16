package domain;

public class StateAttack extends Attack{
    private String consecuence;
    public StateAttack(int idInside, String[] info) throws POOBkemonException{
        super(idInside,info);
        this.consecuence = info[8];
    }
    public String getState(){
        return this.consecuence;
    }


}
