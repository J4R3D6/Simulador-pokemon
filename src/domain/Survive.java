package domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Survive extends POOBkemon {
    private static Survive instance;
    private int pokemonLvl = 100;

    private Survive() {
        super();
    }

    public static Survive getInstance() {
        if (instance == null) {
            instance = new Survive();
        }
        return instance;
    }
    @Override
    protected Pokemon createPokemon(int id, ArrayList<Integer> attackIds) {
        PokemonRepository info = new PokemonRepository();
        String[] infoPokemon = info.getPokemonId(id);
        return new Pokemon(nid, infoPokemon, attackIds, this.random, this.pokemonLvl);
    }

    @Override
    public void initGame(ArrayList<String> trainers,
                         HashMap<String, ArrayList<Integer>> pokemons,
                         HashMap<String, int[][]> items,
                         HashMap<String, ArrayList<Integer>> attacks,
                         boolean random) throws POOBkemonException {
        this.pokemonLvl = 100; // Asegura que siempre sea nivel 100
        super.initGame(trainers, pokemons, items, attacks, random);
    }
}
