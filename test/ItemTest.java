import domain.Item;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int expectedNumber = 123;
        int expectedId = 456;

        Item item = new Item(expectedNumber, expectedId);

        assertEquals(expectedNumber, item.number());
        assertEquals(expectedId, item.getId());
    }

    @Test
    public void testNumberMethodReturnsCorrectValue() {
        // Arrange
        int expectedNumber = 789;
        Item item = new Item(expectedNumber, 0);

        int actualNumber = item.number();

        assertEquals(expectedNumber, actualNumber);
    }

    @Test
    public void testIdFieldIsAccessible() {

        int expectedId = 999;
        Item item = new Item(0, expectedId);

        int actualId = item.getId();

        assertEquals(expectedId, actualId);
    }
}
