import domain.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;

public class POOBkemonTest {
    private POOBkemon game;
    private ArrayList<String> trainers;
    private HashMap<String, ArrayList<Integer>> pokemons;
    private HashMap<String, int[][]> items;
    private HashMap<String, ArrayList<Integer>> attacks;

    @Before
    public void setUp() {
        trainers = new ArrayList<>();
        trainers.add("Player1");
        trainers.add("Defensive1");

        pokemons = new HashMap<>();
        ArrayList<Integer> playerPokemons = new ArrayList<>();
        playerPokemons.add(1); // Bulbasaur
        ArrayList<Integer> enemyPokemons = new ArrayList<>();
        enemyPokemons.add(4); // Charmander
        enemyPokemons.add(2); // Ivysaur
        pokemons.put("Player1", playerPokemons);
        pokemons.put("Defensive1", enemyPokemons);

        items = new HashMap<>();
        int[][] playerItems = {{1, 10}, {2, 5}}; // ID, cantidad
        int[][] enemyItems = {{3, 3}};
        items.put("Player1", playerItems);
        items.put("Defensive1", enemyItems);

        attacks = new HashMap<>();
        // Cada Pokémon necesita 4 ataques, así que preparamos listas con suficientes ataques
        ArrayList<Integer> playerAttacks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) playerAttacks.add(i); // Ataques 1-4 para Bulbasaur

        ArrayList<Integer> enemyAttacks = new ArrayList<>();
        for (int i = 5; i <= 12; i++) enemyAttacks.add(i); // Ataques 5-12 para Charmander e Ivysaur

        attacks.put("Player1", playerAttacks);
        attacks.put("Defensive1", enemyAttacks);

        try {
            game = POOBkemon.getInstance();
            game.initGame(trainers, pokemons, items, attacks, false);
        } catch(POOBkemonException e) {
            System.out.println(e);
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void isOkGame() {
        boolean isOK = game.isOk();
        Assert.assertEquals(true, isOK);
    }

    @Test
    public void ShouldShowMove() {
        ArrayList<String> moves = game.getMoves();
        String move1 = moves.get(0);
        Assert.assertEquals("Start Game", move1);
    }

    @Test
    public void ShouldCreateBattle() {
        Assert.assertFalse(game.finishBattle());
    }

    @Test
    public void shouldShowPokemonPerTrainerStructure() {
        String[][] result = game.pokemonPerTrainer();

        assertNotNull(result);
        assertEquals(2, result.length); // Debería haber 2 entrenadores

        // Player1 tiene 1 pokémon
        assertEquals(1, result[0].length);

        // Defensive1 tiene 2 pokémones
        assertEquals(2, result[1].length);
    }

    @Test
    public void shouldShowPokemonInfoCorrectFormat() {
        String[][] result = game.pokemonPerTrainer();

        // Verificar formato del string para cada pokémon
        for (String[] trainerPokemons : result) {
            for (String pokemonInfo : trainerPokemons) {
                assertTrue(pokemonInfo.matches("\\d+\\. .+ \\(Nivel: \\d+, HP: \\d+\\.\\d+/\\d+\\)"));
            }
        }
    }

    @Test(expected = POOBkemonException.class)
    public void testMissingTrainerData() throws POOBkemonException {
        ArrayList<String> emptyTrainers = new ArrayList<>();
        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        HashMap<String, int[][]> items = new HashMap<>();
        HashMap<String, ArrayList<Integer>> attacks = new HashMap<>();

        POOBkemon game1 = POOBkemon.getInstance();
        game1.initGame(emptyTrainers, pokemons, items, attacks, false);
    }

    @Test(expected = POOBkemonException.class)
    public void testMissingPokemonData() throws POOBkemonException {
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");
        HashMap<String, ArrayList<Integer>> emptyPokemons = new HashMap<>();
        HashMap<String, int[][]> items = new HashMap<>();
        HashMap<String, ArrayList<Integer>> attacks = new HashMap<>();

        POOBkemon game1 = POOBkemon.getInstance();
        game1.initGame(trainers, emptyPokemons, items, attacks, false);
    }

    @Test(expected = POOBkemonException.class)
    public void testMissingItemsData() throws POOBkemonException {
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");
        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        pokemons.put("Player1", new ArrayList<>());
        HashMap<String, int[][]> emptyItems = new HashMap<>();
        HashMap<String, ArrayList<Integer>> attacks = new HashMap<>();

        POOBkemon game1 = POOBkemon.getInstance();
        game1.initGame(trainers, pokemons, emptyItems, attacks, false);
    }

    @Test(expected = POOBkemonException.class)
    public void testIncompleteTrainerData() throws POOBkemonException {
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");
        trainers.add("Offensive1"); // No tiene datos asociados

        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        pokemons.put("Player1", new ArrayList<>());

        HashMap<String, int[][]> items = new HashMap<>();
        HashMap<String, ArrayList<Integer>> attacks = new HashMap<>();

        POOBkemon game1 = POOBkemon.getInstance();
        game1.initGame(trainers, pokemons, items, attacks, false);
    }

    @Test
    public void testTeamsCreation() {
        ArrayList<Team> teams = game.teams();
        assertNotNull(teams);
        assertEquals(2, teams.size());

        // Player1 tiene 1 pokémon
        assertEquals(1, teams.get(0).getPokemons().size());

        // Defensive1 tiene 2 pokémones
        assertEquals(2, teams.get(1).getPokemons().size());
    }
}