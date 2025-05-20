package domain;

public class StateAttack extends Attack{

    private String consecuence;
     
    public StateAttack(int idInside, String[] infoAttack, String[]infoState) throws POOBkemonException{
        super(idInside,infoAttack);
        this.consecuence = infoState[0];
    }
    public String getState(){
        return this.consecuence;
    }

}
