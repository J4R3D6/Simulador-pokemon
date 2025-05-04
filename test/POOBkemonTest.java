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
    private HashMap<String, String[][]> items;
    private HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks;

    @Before
    public void setUp() {

        trainers = new ArrayList<>();
        trainers.add("Player1");
        trainers.add("Defensive1");

        pokemons = new HashMap<>();
        ArrayList<Integer> playerPokemons = new ArrayList<>();
        playerPokemons.add(1); //es Bulbasaur
        ArrayList<Integer> enemyPokemons = new ArrayList<>();
        enemyPokemons.add(4); //4 es Charmander
        enemyPokemons.add(2); // un random ahi
        pokemons.put("Player1", playerPokemons);
        pokemons.put("Defensive1", enemyPokemons);

        items = new HashMap<>();
        String[][] playerItems = {{"1", "10"}, {"2", "5"}}; // ID, cantidad (creo que toca editarlo para Jared)
        String[][] enemyItems = {{"3", "3"}};
        items.put("Player1", playerItems);
        items.put("Defensive1", enemyItems); //la maquina  Ofensiva, Defensiva ya fueron probadas, faltan las demas

        attacks = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> playerAttacks = new HashMap<>();
        playerAttacks.put(1, new ArrayList<Integer>() {{ add(1); add(2); }});
        HashMap<Integer, ArrayList<Integer>> enemyAttacks = new HashMap<>();
        enemyAttacks.put(4, new ArrayList<Integer>() {{ add(3); add(4); }});
        enemyAttacks.put(2,new ArrayList<Integer>(){{add(7);add(34);}});
        attacks.put("Player1", playerAttacks);
        attacks.put("Defensive1", enemyAttacks);
        try {
            game = new POOBkemon(trainers, pokemons, items, attacks, false);
        }
        catch(POOBkemonException e){
            System.out.println(e);
        }

    }

    @Test
    public void isOkGame(){
        boolean isOK = game.isOk();
        Assert.assertEquals(true,isOK);
    }
    @Test
    public void ShouldShowMove(){
        ArrayList<String> moves = game.getMoves();
        String move1 = moves.get(0);
        Assert.assertEquals(move1,"Start Game");
    }
    @Test
    public void ShouldCreateBattle(){
        Assert.assertFalse(game.finishBattle());
    }

    @Test
    public void shouldShowPokemonPerTrainerStructure() {
        String[][] result = game.pokemonPerTrainer();

        // Verificar que el String se haga para los dos entrenadores
        assertNotNull(result);
        assertEquals(2, result.length); // Debería haber 2 entrenadores (siempre xd)

        // Player1 tiene 1 pokémones
        assertEquals(1, result[0].length);

        // Enemy1 tiene 1 pokémon
        assertEquals(2, result[1].length);// Debería haber 2 mostros =)
    }

    @Test
    public void shouldShowPokemonPerTrainerContent() {
        String[][] result = game.pokemonPerTrainer();

        // Verificar formato del string para cada pokémon
        for (String[] trainerPokemons : result) {
            for (String pokemonInfo : trainerPokemons) {
                assertTrue(pokemonInfo.matches("\\d+\\. .+ \\(Nivel: \\d+, HP: \\d+/\\d+\\)"));
            }
        }

        // Verificar que los nombres de los pokémones aparecen en el resultado, y que sean igualkes
        String playerPokemons = String.join(" ", result[0]);

        assertTrue(playerPokemons.contains("Bulbasaur"));

        String enemyPokemons = String.join(" ", result[1]);
        System.out.println(enemyPokemons);
        assertTrue(enemyPokemons.contains("Charmander"));
    }
    @Test
    public void testPokemonPerTrainerStatsAccuracy() {
        String[][] result = game.pokemonPerTrainer();

        // Obtener los pokémones directamente para comparar
        ArrayList<Pokemon> playerTeam = game.teams().get(0).getPokemons();

        // Verificar que los datos mostrados coinciden con los datos reales
        for (int i = 0; i < playerTeam.size(); i++) {
            Pokemon p = playerTeam.get(i);
            String expected = String.format("%d. %s (Nivel: %d, HP: %d/%d)",
                    p.getId(), p.getName(), p.level, p. currentHealth, p.maxHealth);

            assertEquals(expected, result[0][i]);
        }
    }

    // --- Tests para datos faltantes ---

    @Test(expected = POOBkemonException.class)
    public void testMissingTrainerData() throws POOBkemonException {
        // Arrange
        ArrayList<String> emptyTrainers = new ArrayList<>(); // Lista vacía
        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        HashMap<String, String[][]> items = new HashMap<>();
        HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks = new HashMap<>();

        // Act & Assert (debe lanzar excepción)
        new POOBkemon(emptyTrainers, pokemons, items, attacks, false);
    }

    @Test(expected = POOBkemonException.class)
    public void testMissingPokemonData() throws POOBkemonException {
        // Arrange
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");
        HashMap<String, ArrayList<Integer>> emptyPokemons = new HashMap<>(); // Sin pokémones
        HashMap<String, String[][]> items = new HashMap<>();
        HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks = new HashMap<>();

        // Act & Assert
        new POOBkemon(trainers, emptyPokemons, items, attacks, false);
    }

    @Test(expected = POOBkemonException.class)
    public void testMissingItemsData() throws POOBkemonException {
        // Arrange
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");
        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        pokemons.put("Player1", new ArrayList<>());
        HashMap<String, String[][]> emptyItems = new HashMap<>(); // Sin ítems
        HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks = new HashMap<>();

        // Act & Assert
        new POOBkemon(trainers, pokemons, emptyItems, attacks, false);
    }

    // --- Tests para datos incompletos ---

    @Test(expected = POOBkemonException.class)
    public void testIncompleteTrainerData() throws POOBkemonException {
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");
        trainers.add("Offensive1"); // No tiene datos asociados

        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        pokemons.put("Player1", new ArrayList<>()); // Solo Player1 tiene datos

        HashMap<String, String[][]> items = new HashMap<>();
        HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks = new HashMap<>();

        // Act & Assert
        new POOBkemon(trainers, pokemons, items, attacks, false);
    }

    // --- Tests para formato inválido si ingresa una letra y no un numero ---

    @Test(expected = POOBkemonException.class)
    public void testInvalidNumberFormat() throws POOBkemonException {
        // Arrange
        ArrayList<String> trainers = new ArrayList<>();
        trainers.add("Player1");

        HashMap<String, ArrayList<Integer>> pokemons = new HashMap<>();
        pokemons.put("Player1", new ArrayList<>());

        HashMap<String, String[][]> items = new HashMap<>();
        items.put("Player1", new String[][]{{"not_a_number", "10"}}); // ID no es número

        HashMap<String, HashMap<Integer, ArrayList<Integer>>> attacks = new HashMap<>();

        // Act & Assert
        new POOBkemon(trainers, pokemons, items, attacks, false);
    }

    // --- Test para mensajes de excepción (que lo lance pues)---

    @Test
    public void testExceptionMessageContent() {
        // Arrange
        ArrayList<String> emptyTrainers = new ArrayList<>();

        // Act
        try {
            new POOBkemon(emptyTrainers, new HashMap<>(), new HashMap<>(), new HashMap<>(), false);
            fail("Debería haber lanzado una excepción");
        } catch (POOBkemonException e) {
            // Assert
            assertEquals(POOBkemonException.MISSING_TRAINER_DATA, e.getMessage());
        }
    }
}
