package domain;

/**
 * Representa un estado alterado aplicado a un Pokémon (como envenenado, paralizado, dormido, etc.).
 */
public class State {
    public enum stateType {
        PARALISIS, QUEMADURA, ENVENENAMIENTO, ENVENENAMIENTO_GRAVE, SUEÑO, CONGELACION,
        CONFUSION, ATRAPADO
    }

    private stateType type;
    private int duration;  // En turnos, -1 si es indefinido
    private boolean isPermanent; // Si persiste fuera de combate
    private int probability;
    private boolean isVolatile;    // Si puede coexistir con otros estados
    private int damage;




    /**
     * Crea un nuevo estado.
     * @param tipo Tipo del estado.
     * @param info Contiene la informacion del estado
     */
    public State (String[] info, stateType tipo) {
        this.type = tipo;
        this.duration = Integer.parseInt(info[0]);
        this.isPermanent = info[1].equals("1") ? true : false; //1 Es permanente
        this.isVolatile = info[2].equals("1") ? true : false;  //2 Es volati
        this.probability = Integer.parseInt(info[3]);          //3 Probabilidad de curarse
        this.damage = Integer.parseInt(info[4]);               //4 Daño del estado
    }

    public stateType getTipo() {
        return type;
    }

    public int getDamageState(){
        return this.damage;
    }

    public int getDuracion() {
        return duration;
    }

    public boolean esPermanente() {
        return isPermanent;
    }

    /**
     * Reduce la duración del estado en un turno.
     */
    public void avanzarTurno() {
        if (duration > 0) {
            duration--;
        }
    }

    /**
     * Verifica si el estado sigue activo.
     * @return true si sigue activo, false si expiró.
     */
    public boolean estaActivo() {
        return duration > 0;
    }

    public boolean isPrincipal(){
        return !isVolatile;
    }

    @Override
    public String toString() {
        return type.name() + (duration > 0 ? " (" + duration + " turnos restantes)" : "");
    }
}
