package domain;

import java.io.Serializable;

/**
 * Representa un ataque que aplica efectos de estado o modificaciones
 * a las estadísticas de un Pokémon. Puede tener efectos positivos (mejoras)
 * o negativos (estados alterados) y puede afectar al usuario o al objetivo.
 */
public class StateAttack extends Attack implements Serializable {

    private String stateName;
    private int effectValue;
    private boolean affectsSelf;
    private boolean isPersistent;

    /**
     * Constructor para crear un ataque de estado
     * @param idInside ID interno del ataque
     * @param infoAttack Información básica del ataque [nombre, tipo, poder, precision, pp, etc.]
     * @param infoState Información del estado [nombre, efecto, duración, etc.]
     * @throws POOBkemonException Si hay error en la creación
     */
    public StateAttack(int idInside, String[] infoAttack, String[] infoState) throws POOBkemonException {
        super(idInside, infoAttack);

        if (infoState == null || infoState.length < 3) {
            if (infoState == null){
                throw new POOBkemonException("Informacion de estado nula");
            }
            throw new POOBkemonException("Información de estado inválida");

        }

        this.stateName = infoState[0];
        this.effectValue = Integer.parseInt(infoState[1]);
        this.affectsSelf = infoAttack.length > 8 && infoAttack[8].equalsIgnoreCase("ally");
        this.isPersistent = Boolean.parseBoolean(infoState[2]);
    }

    /**
     * Aplica el efecto completo del ataque
     * @param target Pokémon objetivo
     * @param user Pokémon que usa el ataque
     * @return Mensaje descriptivo del resultado
     */
    public String applyEffect(Pokemon target, Pokemon user) {
        if (target == null || user == null) {
            return "";
        }

        Pokemon affectedPokemon = this.affectsSelf ? user : target;
        if(!affectsSelf){
            if(affectedPokemon.isProtected()) return affectedPokemon.getName() + " está protegido!";
        }

        StringBuilder result = new StringBuilder();

        // 1. Verificar si el ataque impacta
        if (!doesEffectHit()) {
            return user.getName() + " falló el ataque!";
        }

        // 2. Aplicar efecto inmediato (curación/daño)
        if (this.effectValue != 0) {
            result.append(applyImmediateEffect(affectedPokemon));
        }

        // 3. Aplicar efecto de estado persistente
        if (this.isPersistent && !this.stateName.isEmpty()) {
            if (result.length() > 0) result.append(" ");
            result.append(applyPersistentEffect(affectedPokemon, user));
        }

        return result.toString();
    }

    /**
     * Determina si el ataque impacta basado en su precisión
     * @return true si el ataque tiene éxito
     */
    private boolean doesEffectHit() {
        // Ataques con precisión especial (ej. "—" en los juegos) siempre golpean
        if (this.getAccuracy() <= 0) return true;

        double hitProbability = this.getAccuracy() / 100.0;
        hitProbability *= 0.85 + (Math.random() * 0.15); // Variación aleatoria

        return Math.random() < Math.max(0.01, Math.min(1.0, hitProbability));
    }

    /**
     * Aplica efectos inmediatos de curación o daño
     * @param pokemon Pokémon afectado
     * @return Mensaje descriptivo
     */
    private String applyImmediateEffect(Pokemon pokemon) {
        if (this.effectValue > 0) {
            int oldHealth = pokemon.currentHealth;
            pokemon.currentHealth = Math.min(pokemon.maxHealth, pokemon.currentHealth + this.effectValue);
            int healed = pokemon.currentHealth - oldHealth;
            return pokemon.getName() + " recuperó " + healed + " PS!";
        }
        else if (this.effectValue < 0) {
            int damage = Math.min(-this.effectValue, pokemon.currentHealth);
            pokemon.currentHealth -= damage;
            pokemon.isWeak();
            return pokemon.getName() + " perdió " + damage + " PS!";
        }
        return "";
    }

    /**
     * Aplica efectos de estado persistentes
     * @param target Pokémon objetivo real
     * @param user Pokémon que usa el ataque
     * @return Mensaje descriptivo
     */
    private String applyPersistentEffect(Pokemon target, Pokemon user) {
        try {
            StatusRepository repo = new StatusRepository();
            String[] stateInfo = repo.getStatusByName(this.stateName);

            if (stateInfo == null || stateInfo.length == 0) {
                return "";
            }

            State state = new State(stateInfo);

            if (target.isImmune(state)) {
                return target.getName() + " es inmune a " + state.getType();
            }

            target.persistentDamage(state);

            return this.affectsSelf ?
                    user.getName() + " se aplicó " + state.getType() :
                    user.getName() + " aplicó " + state.getType() + " a " + target.getName();

        } catch (Exception e) {
            System.err.println("Error aplicando estado persistente: " + e.getMessage());
            return "";
        }
    }

    // Getters
    public String getState() { return this.stateName; }
    /**
     * Obtiene información completa del ataque
     * @return Array con toda la información
     */
    @Override
    public String[] getInfo() {
        String[] baseInfo = super.getInfo();
        String[] fullInfo = new String[baseInfo.length + 4];

        System.arraycopy(baseInfo, 0, fullInfo, 0, baseInfo.length);

        fullInfo[baseInfo.length] = this.stateName;
        fullInfo[baseInfo.length + 1] = String.valueOf(this.effectValue);
        fullInfo[baseInfo.length + 2] = String.valueOf(this.affectsSelf);
        fullInfo[baseInfo.length + 3] = String.valueOf(this.isPersistent);

        return fullInfo;
    }
}