import domain.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.Random;

class POOBkemonTest {
    private POOBkemon game;
    private ArrayList<String> trainers;
    private HashMap<String, ArrayList<Integer>> pokemons;
    private HashMap<String, int[][]> items;
    private HashMap<String, ArrayList<Integer>> attacks;

    @BeforeEach
    void setUp() {
        game = POOBkemon.getInstance();

        // Datos básicos de prueba
        trainers = new ArrayList<>();
        trainers.add("Player1");
        trainers.add("Player2");

        pokemons = new HashMap<>();
        pokemons.put("Player1", new ArrayList<>(List.of(1, 2, 56, 4,5,28)));
        pokemons.put("Player2", new ArrayList<>(List.of(3, 4, 45, 3,203,301)));

        items = new HashMap<>();
        items.put("Player1", new int[][]{{1, 5}, {2,3}});
        items.put("Player2", new int[][]{{3, 2}, {4, 1}});

        attacks = new HashMap<>();
        attacks.put("Player1", new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8,9,10,11,12,13,14,15,16,17,17,19,20,21,22,23,24)));
        attacks.put("Player2", new ArrayList<>(List.of(9, 10, 11, 12, 13, 14, 15, 16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,45)));
    }

    @Test
    @DisplayName("Test Singleton instance")
    void testSingletonInstance() {
        POOBkemon anotherInstance = POOBkemon.getInstance();
        assertSame(game, anotherInstance);
    }

    @Test
    @DisplayName("Test initial game state")
    void testInitialState() {
        assertFalse(!game.isOk());
        assertFalse(game.finishBattle());
        assertNotNull(game.teams());
    }

    @Test
    @DisplayName("Test successful game initialization")
    void testInitGameSuccess() {
        try {
            game.initGame(trainers, pokemons, items, attacks, false);
        }catch (POOBkemonException e){
            System.out.println(e.getMessage());
        }
        assertTrue(game.isOk());
        assertEquals(2, game.teams().size());
        assertEquals("Start Game", game.getMoves().get(0));
        assertEquals(2, game.getOrder().size());
    }



    @Test
    @DisplayName("Test Attack other Pokemon")
    void shouldAttackAnotherPokemon() throws POOBkemonException {
        // Arrange - Configuración inicial del juego
        game.initGame(trainers, pokemons, items, attacks, false);
        game.coin();

        // Obtener el orden de turno y verificar
        ArrayList<Integer> turnOrder = game.getOrder();
        assertEquals(2, turnOrder.size(), "Debe haber exactamente 2 entrenadores");

        // Identificar equipos y entrenadores
        Team attackingTeam = null;
        Team defendingTeam = null;

        for (Team team : game.getTeams()) {
            if (team.getTrainer().getId() == turnOrder.get(0)) {
                attackingTeam = team;
            } else if (team.getTrainer().getId() == turnOrder.get(1)) {
                defendingTeam = team;
            }
        }

        assertNotNull(attackingTeam, "Equipo atacante no encontrado");
        assertNotNull(defendingTeam, "Equipo defensor no encontrado");

        // Obtener Pokémon atacante y defensor
        Pokemon attacker = attackingTeam.getPokemonById(attackingTeam.getTrainer().getCurrentPokemonId());
        Pokemon defender = defendingTeam.getPokemonById(defendingTeam.getTrainer().getCurrentPokemonId());

        assertTrue(attacker.getActive(), "Pokémon atacante debe estar activo");
        assertTrue(defender.getActive(), "Pokémon defensor debe estar activo");

        // Obtener el primer ataque del Pokémon atacante
        Attack attackToUse = attacker.getAttacks().get(3);
        int initialDefenderHP = defender.currentHealth;
        int initialPP = attackToUse.getPPActual();

        // Act - Preparar y ejecutar la decisión de ataque
        // Formato correcto: {"Attack", "idAtaque", "idAtacante"}
        String[] attackDecision = {
                "Attack",
                String.valueOf(attackToUse.getIdInside()),
                String.valueOf(attackingTeam.getTrainer().getId()),
                String.valueOf(attacker.getId())
        };

        assertDoesNotThrow(() -> game.takeDecision(attackDecision),
                "El ataque no debería lanzar excepción");

        // Assert - Verificaciones post-ataque
        // 1. Verificar que el PP del ataque disminuyó
        assertEquals(initialPP - 1, attackToUse.getPPActual(),
                "El PP del ataque debería haber disminuido en 1");

        // 2. Verificar que el defensor recibió daño
        assertTrue(defender.currentHealth < initialDefenderHP,
                "El Pokémon defensor debería tener menos HP después del ataque");
        System.out.println(initialDefenderHP);
        System.out.println(defender.currentHealth);

        // 3. Verificar que el atacante sigue activo
        assertTrue(attacker.getActive(),
                "El Pokémon atacante debería seguir activo después del ataque");

        // 4. Verificar que el defensor sigue activo (a menos que se debilitara)
        if (defender.currentHealth > 0) {
            assertTrue(defender.getActive(),
                    "El Pokémon defensor debería seguir activo si no fue debilitado");
        }
        if(defender.currentHealth == 0){
            assertTrue(defender.getWeak());
            assertFalse(defender.getActive());
        }
    }

    @Test
    @DisplayName("Test game initialization with missing trainer data")
    void testInitGameMissingTrainerData() {
        assertThrows(POOBkemonException.class, () -> {
            game.initGame(null, pokemons, items, attacks, false);
        });
    }

    @Test
    @DisplayName("Test createPokemon method")
    void testCreatePokemon() {
        Pokemon pokemon = game.createPokemon(1, new ArrayList<>(List.of(1, 2, 3, 4)));
        assertNotNull(pokemon);
        //en conjunto se han creado 16 hasta ahora, pero individualmente es 0
        assertEquals(16, pokemon.getId()); // nid comienza en 0
    }

    @Test
    @DisplayName("Test createPokemons with insufficient attacks")
    void testCreatePokemonsWithInsufficientAttacks() {
        assertThrows(IllegalArgumentException.class, () -> {
            game.createPokemons(
                    new ArrayList<>(List.of(1, 2)),
                    new ArrayList<>(List.of(1, 2, 3)) // Solo 3 ataques para 2 pokémones (necesita 8)
            );
        });
    }

    @Test
    @DisplayName("Test coin method for turn order")
    void testCoinMethod() throws POOBkemonException {
        game.initGame(trainers, pokemons, items, attacks, false);

        ArrayList<Integer> order = game.getOrder();
        assertEquals(2, order.size());
        assertNotEquals(order.get(0), order.get(1));
    }

    @Test
    @DisplayName("Test takeDecision with attack action")
    void testTakeDecisionAttack() throws POOBkemonException {
        game.initGame(trainers, pokemons, items, attacks, false);

        String[] decision1 = {"Attack", "1", "1"}; // Atacar con movimiento 1 al Pokémon 1
        String[] decision2 = {"Attack", "1", "0"}; // Atacar con movimiento 1 al Pokémon 0

        game.takeDecision(decision1);

        assertTrue(game.getMoves().size() > 1);
        assertTrue(game.getMoves().get(1).contains("usó ataque"));
    }

    @Test
    @DisplayName("Test changePokemon method")
    void testChangePokemon() {
        try {
            game.initGame(trainers, pokemons, items, attacks, false);

            int trainerId = game.getOrder().get(0);
            Trainer entrenador = game.getTeams().get(0).getTrainer();
            int actualPokemon = entrenador.getCurrentPokemonId();

            /**HashMap<Integer, String[]> activo = game.getCurrentPokemons();
            for (String[] p : activo.values()) {
                for (String s : p) {
                    System.out.println(s);
                }
            }
            **/
            String[] decision1 = {"ChangePokemon", "1"};
            String[] decision2 = {"Attack", "1", "0"};
            /**
            HashMap<Integer, String[]> activo1 = game.getCurrentPokemons();
            for (String[] p : activo.values()) {
                for (String s : p) {
                    System.out.println(s);
                }
            }**/
            game.takeDecision(decision1);
            int cambiodePokemon = entrenador.getCurrentPokemonId();
            System.out.println(cambiodePokemon);
            assertTrue(cambiodePokemon != actualPokemon);
        }catch(POOBkemonException e){
            System.out.println(e.getMessage());
        }

    }

    @Test
    @DisplayName("Test getCurrentPokemons method")
    void testGetCurrentPokemons() throws POOBkemonException {
        game.initGame(trainers, pokemons, items, attacks, false);

        HashMap<Integer, String[]> currentPokemons = game.getCurrentPokemons();
        assertEquals(2, currentPokemons.size());

        for (String[] pokemonInfo : currentPokemons.values()) {
            assertNotNull(pokemonInfo);
            assertTrue(pokemonInfo.length > 0);
        }
    }

    @Test
    @DisplayName("Test getPokemonsInactive method")
    void testGetPokemonsInactive() throws POOBkemonException {
        game.initGame(trainers, pokemons, items, attacks, false);

        int trainerId = game.getOrder().get(0);
        int[] inactivePokemons = game.getPokemonsInactive(trainerId);

        assertTrue(inactivePokemons.length > 0);
    }

    @Test
    @DisplayName("Test run action ends battle")
    void testRunAction() throws POOBkemonException {
        game.initGame(trainers, pokemons, items, attacks, false);

        String[] decision1 = {"Run","1"};
        game.takeDecision(decision1);
        assertTrue(game.finishBattle());
    }

    @Test
    @DisplayName("Test getPokemonInfo method")
    void testGetPokemonInfo() throws POOBkemonException {
        game.initGame(trainers, pokemons, items, attacks, false);
        int pokemon = -1;
        int trainerId = game.getOrder().get(0);
        for(Team t : game.getTeams()){
            if(t.getTrainer().getId() == trainerId){
                pokemon = t.getPokemons().get(0).getId();
            }
        }
        String[] pokemonInfo = game.getPokemonInfo(trainerId, pokemon);

        for(String info : pokemonInfo){
            System.out.println(info);
        }

        assertNotNull(pokemonInfo);
        assertTrue(pokemonInfo.length > 0);
    }

    @Test
    @DisplayName("Test repository information methods")
    void testRepositoryMethods() {
        assertNotNull(game.getPokInfo());
        assertNotNull(game.getItemInfo());
        assertNotNull(game.getMoveInfo(1));
    }
}