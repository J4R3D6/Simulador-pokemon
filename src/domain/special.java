package domain;

public class special extends Attack {
    public special(int idInside,String[] info) throws POOBkemonException{
        super(idInside,info);
    }
    @Override
    public void timeOver(){
        this.ppActual--;
    }
}
