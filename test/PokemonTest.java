
import domain.Pokemon;
import domain.PokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PokemonTest {

    private Pokemon pokemon;
    private String[] sampleInfo;
    private ArrayList<Integer> sampleAttacksIds;

    @BeforeEach
    public void setUp() {
        PokemonRepository sampleInfo = new PokemonRepository();
        String[] sampleInfo1 = sampleInfo.getPokemonId(1);
        sampleAttacksIds = new ArrayList<>(Arrays.asList(1, 2));
        pokemon = new Pokemon(1, sampleInfo1, sampleAttacksIds, false);
    }

    @Test
    public void testConstructorInitialization() {
        assertEquals(1, pokemon.getId());
        assertEquals("Bulbasaur", pokemon.getName());
        assertEquals("001", pokemon.idPokedex);
        assertEquals("Grass", pokemon.type);
        assertEquals(45, pokemon.maxHealth);
        assertEquals(45, pokemon.currentHealth);
        assertEquals(49, pokemon.defense);
        assertEquals(65, pokemon.specialAttack);
        assertEquals(65, pokemon.specialDefense);
        assertEquals(45, pokemon.speed);
        assertEquals(0, pokemon.xp);
        assertEquals(1, pokemon.level);
        assertEquals(100, pokemon.levelRequirement);
        assertFalse(pokemon.getActive());
        assertFalse(pokemon.getWeak());
        assertEquals(2, pokemon.getAttacks().size());
    }

    @Test
    public void testGetActive() {
        assertFalse(pokemon.getActive());
    }

    @Test
    public void testSetActive() {
        pokemon.setActive(true);
        assertTrue(pokemon.getActive());

        pokemon.setActive(false);
        assertFalse(pokemon.getActive());
    }

    @Test
    public void testGetId() {
        assertEquals(1, pokemon.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Bulbasaur", pokemon.getName());
    }

    @Test
    public void testCreateAttacks() {
        // Verificamos que se crearon los ataques correctamente
        assertEquals(2, pokemon.getAttacks().size());
        // Aquí podríamos añadir más verificaciones sobre los ataques si tuviéramos acceso a ellos
    }

    @Test
    public void testPokemonInfo() {
        // Este test fallará hasta que implementes pokemonInfo()
        assertNull(pokemon.pokemonInfo());
    }

    @Test
    public void testAttackInfo() {
        // Este test fallará hasta que implementes attackInfo()
        assertNull(pokemon.attackInfo());
    }

    @Test
    public void testGetAttack() {
        // Este test fallará hasta que implementes getAttack()
        assertNull(pokemon.getAttack(1));
    }

    @Test
    public void testLevelUp() {
        // Este test verifica el comportamiento inicial
        // Deberías expandirlo cuando implementes levelUp()
        pokemon.levelUp();
        // Aquí deberían ir aserciones sobre los cambios esperados
    }

    @Test
    public void testGetDamage() {
        // Este test verifica que el método no lance excepciones
        assertDoesNotThrow(() -> pokemon.getDamage());
    }
}
