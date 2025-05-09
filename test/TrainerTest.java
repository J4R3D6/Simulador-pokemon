import domain.BagPack;
import domain.Item;
import domain.POOBkemonException;
import domain.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {
    private Trainer trainer;
    private BagPack bagPack;

    @BeforeEach
    void setUp() throws POOBkemonException {
        // Crear una mochila con algunos ítems para las pruebas
        bagPack = new BagPack((java.util.ArrayList<Item>) List.of(
                new Item(1, 3), // Item ID 1, cantidad 3
                new Item(2, 1)  // Item ID 2, cantidad 1
        ));
        trainer = new Trainer(1, bagPack);
    }

    @Test
    @DisplayName("Constructor con mochila válida")
    void constructorWithValidBagPack() throws POOBkemonException {
        assertNotNull(trainer);
        assertEquals(1, trainer.getId());
        assertSame(bagPack, trainer.getBagPack());
        assertEquals(-1, trainer.getCurrentPokemonId()); // Valor por defecto
    }

    @Test
    @DisplayName("Constructor con mochila nula lanza excepción")
    void constructorWithNullBagPack() {
        assertThrows(POOBkemonException.class, () -> {
            new Trainer(1, null);
        });
    }

    @Test
    @DisplayName("Getter y Setter de currentPokemonId")
    void testCurrentPokemonId() {
        assertEquals(-1, trainer.getCurrentPokemonId());

        trainer.setCurrentPokemonId(5);
        assertEquals(5, trainer.getCurrentPokemonId());

        trainer.setCurrentPokemonId(-2);
        assertEquals(-2, trainer.getCurrentPokemonId()); // Acepta cualquier valor
    }

    @Test
    @DisplayName("Getter de ID")
    void testGetId() {
        assertEquals(1, trainer.getId());
    }

    @Test
    @DisplayName("Getter de BagPack")
    void testGetBagPack() {
        assertNotNull(trainer.getBagPack());
        assertSame(bagPack, trainer.getBagPack());
    }

    @Test
    @DisplayName("Método getItem retorna null (implementación actual)")
    void testGetItem() {
        assertNull(trainer.getItem(1)); // Implementación actual siempre retorna null
    }

    @Test
    @DisplayName("Cambio de Pokémon activo")
    void testPokemonChange() {
        trainer.setCurrentPokemonId(3);
        assertEquals(3, trainer.getCurrentPokemonId());

        trainer.setCurrentPokemonId(0);
        assertEquals(0, trainer.getCurrentPokemonId());
    }

    @Test
    @DisplayName("Estado inicial correcto")
    void testInitialState() {
        assertEquals(1, trainer.getId());
        assertNotNull(trainer.getBagPack());
        assertEquals(-1, trainer.getCurrentPokemonId());
    }

    @Test
    @DisplayName("Múltiples instancias con diferentes IDs")
    void testMultipleInstances() throws POOBkemonException {
        Trainer trainer2 = new Trainer(2, bagPack);
        Trainer trainer3 = new Trainer(3, bagPack);

        assertNotEquals(trainer.getId(), trainer2.getId());
        assertNotEquals(trainer2.getId(), trainer3.getId());
    }
}