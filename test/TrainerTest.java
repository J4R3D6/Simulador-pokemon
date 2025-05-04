import domain.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TrainerTest {

    private Team validTeam;
    private BagPack validBagPack;
    private Pokemon healthyPokemon;
    private Pokemon faintedPokemon;
    private HashMap<String, String[][]> items;

    @Before
    public void setUp() throws POOBkemonException {
        // Configuración de ataques para los pokémones
        MovesRepository movesRepository = new MovesRepository();
        String[] attackInfo1 = movesRepository.getAttacksId(1); // Ataque básico 1
        String[] attackInfo2 = movesRepository.getAttacksId(2); // Ataque básico 2
        Attack ataque1 = new Attack(attackInfo1);
        Attack ataque2 = new Attack(attackInfo2);

        // Configuración común para los tests
        ArrayList<Integer> attacksIds = new ArrayList<>(Arrays.asList(1, 2));
        healthyPokemon = new Pokemon(1,
                new String[]{"1", "Bulbasaur", "Grass", "", "", "45", "49", "49", "65", "65", "45"},
                attacksIds, false);

        faintedPokemon = new Pokemon(2,
                new String[]{"2", "Pikachu", "Electric", "", "", "35", "55", "40", "50", "50", "90"},
                attacksIds, false);

        // Debilita el pokémon
        faintedPokemon.getDamage(999, ataque1);

        ArrayList<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(healthyPokemon);
        pokemons.add(new Pokemon(3,
                new String[]{"3", "Charizard", "Fire", "", "", "78", "84", "78", "109", "85", "100"},
                attacksIds, false));

        validTeam = new Team(pokemons);
        String[][] playerItems = {{"1", "10"}, {"2", "5"}};
        int id = 0;
        ArrayList<Item> items = new ArrayList<Item>();
        for(String[] i: playerItems){
            Item ite = new Item(Integer.parseInt(i[0]), Integer.parseInt(i[1]));
            items.add(ite);
            id++;
        }

        validBagPack = new BagPack(items);
    }

    // Tests de Constructor
    @Test(expected = POOBkemonException.class)
    public void testConstructorWithNullTeam() throws POOBkemonException {
        new Trainer(null, validBagPack);
    }

    @Test(expected = POOBkemonException.class)
    public void testConstructorWithNullBagPack() throws POOBkemonException {
        new Trainer(validTeam, null);
    }

    @Test(expected = POOBkemonException.class)
    public void testConstructorWithEmptyTeam() throws POOBkemonException {
        new Trainer(new Team(new ArrayList<>()), validBagPack);
    }

    @Test(expected = POOBkemonException.class)
    public void testConstructorWithNullPokemon() throws POOBkemonException {
        List<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(null);
        new Trainer(new Team((ArrayList<Pokemon>) pokemons), validBagPack);
    }

    @Test(expected = POOBkemonException.class)
    public void testConstructorWithFaintedFirstPokemon() throws POOBkemonException {
        List<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(faintedPokemon);
        new Trainer(new Team((ArrayList<Pokemon>) pokemons), validBagPack);
    }

    @Test
    public void testValidConstructor() throws POOBkemonException {
        Trainer trainer = new Trainer(validTeam, validBagPack);

        assertNotNull(trainer);
        assertEquals(healthyPokemon, trainer.getCurrentPokemon());
        assertEquals(validTeam, trainer.getTeam());
        assertEquals(validBagPack, trainer.getBagPack());
    }

    // Tests de changePokemon
    @Test
    public void testChangePokemonToValidId() throws POOBkemonException {
        Trainer trainer = new Trainer(validTeam, validBagPack);
        Pokemon originalPokemon = trainer.getCurrentPokemon();
        Pokemon newPokemon = validTeam.getPokemons().get(1); // Cambiar al segundo pokémon

        trainer.changePokemon(newPokemon.getId());

        // Verificaciones
        assertEquals(newPokemon.getId(), trainer.getCurrentPokemon().getId());
        assertFalse(originalPokemon.getActive());  // El anterior debe estar inactivo
        assertTrue(newPokemon.getActive());        // El nuevo debe estar activo
        assertTrue(validTeam.getPokemons().stream()
                .filter(p -> p.getId() == newPokemon.getId())
                .findFirst()
                .get()
                .getActive()); // Verificar en el equipo también
    }

    @Test
    public void testChangePokemonToSameId() throws POOBkemonException {
        Trainer trainer = new Trainer(validTeam, validBagPack);
        trainer.changePokemon(1); // Mismo ID

        assertEquals(healthyPokemon, trainer.getCurrentPokemon());
        assertTrue(healthyPokemon.getActive());
    }

    @Test
    public void testChangePokemonToInvalidId() throws POOBkemonException {
        // 1. Preparación
        Trainer trainer = new Trainer(validTeam, validBagPack);
        Pokemon originalPokemon = trainer.getCurrentPokemon();
        int originalActiveCount = countActivePokemons(validTeam);

        // 2. Ejecución (debe fallar silenciosamente)
        trainer.changePokemon(999); // ID inexistente

        // 3. Verificaciones
        // - El pokémon actual no cambió
        assertEquals(originalPokemon, trainer.getCurrentPokemon());
        // - Sigue activo
        assertTrue(originalPokemon.getActive());
        // - No se activaron pokémones adicionales
        assertEquals(1, countActivePokemons(validTeam));
    }

    // Método auxiliar para contar pokémones activos
    private int countActivePokemons(Team team) {
        return (int) team.getPokemons().stream()
                .filter(Pokemon::getActive)
                .count();
    }

    // Tests de activePokemon
    @Test
    public void testActivePokemonInfo() throws POOBkemonException {
        Trainer trainer = new Trainer(validTeam, validBagPack);
        String[] info = trainer.activePokemon();

        assertNotNull(info);
        assertEquals("1", info[0]); // ID
        assertEquals("Bulbasaur", info[1]); // Nombre
        assertEquals("45", info[5]); // HP máximo
        assertEquals("45", info[6]); // HP actual
    }

    // Test adicional para verificar estado inicial
    @Test
    public void testInitialActiveState() throws POOBkemonException {
        Trainer trainer = new Trainer(validTeam, validBagPack);
        assertTrue(trainer.getCurrentPokemon().getActive());
        assertEquals(1, trainer.getCurrentPokemon().getId());
    }
}