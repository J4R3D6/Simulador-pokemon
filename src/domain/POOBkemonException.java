package domain;

public class POOBkemonException extends Exception {
    public static final String POKEMON_NO_ENCONTRADO  = "Pokemon fuera del rango";
    public static final String ITEM_FUERA_DE_RANGO  = "Item furea del rango";
    public POOBkemonException(String mensaje) {
        super(mensaje);
    }

}
