package domain;

import java.io.Serializable;

/**
 * Representa un estado alterado aplicado a un Pokémon (como envenenado, paralizado, dormido, etc.).
 */
public class State implements Serializable {
    public enum stateType {
        PARALYSIS, QUEMADURA, POISON, BAD_POISON, SUEÑO, CONGELACION
    }

    private stateType type;
    private int duration;  // En turnos, -1 si es indefinido
    private boolean isPermanent; // Si persiste fuera de combate
    private int probability;
    private boolean isVolatile;    // Si puede coexistir con otros estados
    private int damage;
    private int turnsActive = 1;
    private String description;




    /**
     * Crea un nuevo estado.
     * @param tipo Tipo del estado.
     * @param info Contiene la informacion del estado
     */
    public State (String[] info) {
        this.type = State.stateType.valueOf(info[0].trim().toUpperCase());
        this.duration = Integer.parseInt(info[1]);
        this.isPermanent = info[2].equals("1") ? true : false; //1 Es permanente
        this.isVolatile = info[3].equals("1") ? true : false;  //2 Es volatil
        /**this.probability = Integer.parseInt(info[3]); */ //Revisar si existirá probabilidad         //3 Probabilidad de curarse
        this.description = info[4];               //4 Descripcion estado.
    }

    public String getType(){
        System.out.println(this.type.name());
        return this.type.name();
    }

    public int getDamageState(){
        return this.damage;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isPermanent() {
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
    public boolean isActive() {
        return duration > 0;
    }

    public boolean isPrincipal(){
        return !isVolatile;
    }

    public String applyEffect(Pokemon pokemon) {
        switch (this.type.name()) {
            case "QUEMADURA":
                damage = pokemon.maxHealth / 8;
                pokemon.currentHealth = Math.max(0, pokemon.currentHealth - damage);
                if(pokemon.currentHealth == 0){
                    pokemon.setActive(false);
                }
                return pokemon.getName() + " sufre por quemaduras y pierde " + damage + " de salud.\n";
            case "POISON":
                damage = pokemon.maxHealth / 8;
                pokemon.currentHealth = Math.max(0, pokemon.currentHealth - damage);
                if(pokemon.currentHealth == 0){
                    pokemon.setActive(false);
                }
                return pokemon.getName() + " se debilita por el veneno y pierde " + damage + " de salud.\n";
            case "PARALYSIS":
                pokemon.speed = pokemon.speed - (int)(pokemon.speed*(0.5));
                if(pokemon.currentHealth == 0){
                    pokemon.setActive(false);
                }
                break;
            case "BAD_POISON":
                damage = (pokemon.maxHealth / 16) * turnsActive;
                pokemon.currentHealth = Math.max(0, pokemon.currentHealth - damage);
                System.out.println(pokemon.getName() + " sufre " + damage + " por envenenamiento grave.");
                turnsActive++;
                if(pokemon.currentHealth == 0){
                    //pokemon.setActive(false);
                }
                break;
        }

        if (duration > 0) {
            duration--;
            if (duration == 0) {
                // Aquí podrías marcar el estado como finalizado si implementas eso
                return pokemon.getName() + " se ha curado de " + (this.type.name().toLowerCase());
            }
    }
        return "";
    }

    @Override
    public String toString() {
        return type.name() + (duration > 0 ? " (" + duration + " turnos restantes)" : "");
    }
}

