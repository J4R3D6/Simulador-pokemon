package domain;

public class Revive extends Item{
    public Revive(int number){
        super(number);
        this.name = "Revive";
    }

    @Override
    public void itemEffect(Pokemon pokemon) {
        String[] info = new String[4];
        info[0] = "Potion"; // nombre del item
        info[1] = "Revive"; // indicacion de efecto
        //aplico el efecto de la poti
        pokemon.effect(info);
    }

}
