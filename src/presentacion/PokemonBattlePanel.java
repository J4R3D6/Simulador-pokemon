package presentacion;

import domain.POOBkemon;
import domain.POOBkemonException;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PokemonBattlePanel extends JPanel implements Auxiliar {
    // Constantes de rutas
    private static final String CHARACTER = "resources/personaje/";
    private static final String MENU = "resources/menu/";
    private static final String POKEMONES = "resources/pokemones/Emerald/";
    private static final String BACK_PATH = POKEMONES + "Back/";
    private static final String BACK_SHINY_PATH = POKEMONES + "BackShiny/";
    private static final String NORMAL_PATH = POKEMONES + "Normal/";
    private static final String SHINY_PATH = POKEMONES + "Shiny/";
    private static final String PNG_EXT = ".png";

    // Componentes UI
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private final Map<String, Supplier<JPanel>> panelBuilders = new HashMap<>();
    private POOBkemon game;
    private BattleListener battleListener;
    private int currentPlayer;
    private int fondo = 1;

    // Estado de la batalla
    private String[] decisionTrainer1 = null;
    private String[] decisionTrainer2 = null;
    private boolean turnInProgress = false;
    private ArrayList<Integer> order;

    public interface BattleListener {
        void onBattleEnd(boolean playerWon);
    }

    public PokemonBattlePanel(POOBkemon game) {
        if (game == null) throw new IllegalArgumentException("Game cannot be null");
        this.game = game;
        this.order = game.getOrder();
        this.currentPlayer = game.getOrder().get(0);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel, BorderLayout.CENTER);
        // Registrar creadores de paneles
        panelBuilders.put("battle", this::createBattleView);
        panelBuilders.put("pokemon", this::createPokemonView);
        panelBuilders.put("attack", this::createAtaquesView);
        // panelBuilders.put("animation", this::createAnimationView);
        // Inicializar con un panel por defecto (opcional)
        JPanel initialPanel = panelBuilders.get("battle").get();
        initialPanel.setName("battle");
        mainPanel.add(initialPanel, "battle");
    }
    public void setBattleListener(BattleListener listener) {
        this.battleListener = listener;
    }
    //panatalla de pelea
    private JPanel createBattleView() {
        // 1. Obtener datos de los Pokémon
        HashMap<Integer, String[]> currentPokemons = this.game.getCurrentPokemons();
        final String[] playerData = currentPokemons.get((currentPlayer == order.get(1)) ? order.get(1) : order.get(0));
        final String[] enemyData = currentPokemons.get((currentPlayer == order.get(0)) ? order.get(1) : order.get(0));
        // 2. Preparar recursos gráficos
        BattleGraphicsResources resources = prepareBattleGraphics(playerData, enemyData);
        // 3. Crear panel principal con fondo personalizado
        JPanel panel = createMainBattlePanel(resources);
        // 4. Añadir componentes de la interfaz
        addBattleComponents(panel, playerData, enemyData);
        // 5. Configurar redimensionamiento
        setupBattleLayout(panel);
        return panel;
    }
    private static class BattleGraphicsResources {
        Image background;
        Image playerImage;
        Image enemyImage;
        Image trainerImage;
        int playerLowestY;
        int enemyLowestY;
    }
    private BattleGraphicsResources prepareBattleGraphics(String[] playerData, String[] enemyData) {
        BattleGraphicsResources resources = new BattleGraphicsResources();
        String playerImagePath = playerData[16].equals("true")
                ? BACK_SHINY_PATH + playerData[2] + PNG_EXT
                : BACK_PATH + playerData[2] + PNG_EXT;
        String enemyImagePath = enemyData[16].equals("true")
                ? SHINY_PATH + enemyData[2] + PNG_EXT
                : NORMAL_PATH + enemyData[2] + PNG_EXT;
        resources.background = new ImageIcon(MENU + "battle" + this.fondo + PNG_EXT).getImage();
        resources.trainerImage = new ImageIcon(CHARACTER + this.currentPlayer + PNG_EXT).getImage();
        ImageIcon playerIcon = new ImageIcon(playerImagePath);
        BufferedImage playerBufferedImg = toBufferedImage(playerIcon.getImage());
        resources.playerImage = playerBufferedImg;
        resources.playerLowestY = findAbsoluteLowestVisibleY(playerBufferedImg);
        ImageIcon enemyIcon = new ImageIcon(enemyImagePath);
        BufferedImage enemyBufferedImg = toBufferedImage(enemyIcon.getImage());
        resources.enemyImage = enemyBufferedImg;
        resources.enemyLowestY = findAbsoluteLowestVisibleY(enemyBufferedImg);
        return resources;
    }
    private JPanel createMainBattlePanel(BattleGraphicsResources resources) {
        return new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                final int w = getWidth();
                final int h = getHeight();
                super.paintComponent(g);

                // Dibujar fondo
                g.drawImage(resources.background, 0, 0, w, h, this);

                // Posicionamiento del Pokémon enemigo
                int enemyDisplayWidth = (int)(w * 0.27);
                int enemyDisplayHeight = (int)(h * 0.4);
                double enemyScaleY = (double) enemyDisplayHeight / resources.enemyImage.getHeight(this);
                int enemyTargetY = (int)(h * 0.44) - (int)(resources.enemyLowestY * enemyScaleY) - 15;

                g.drawImage(resources.enemyImage,
                        (int)(w * 0.62),
                        enemyTargetY,
                        enemyDisplayWidth,
                        enemyDisplayHeight,
                        this);

                // Posicionamiento del Pokémon jugador

                int playerDisplayWidth = (int)(w * 0.25);
                int playerDisplayHeight = (int)(h * 0.3);
                double playerScaleY = (double)playerDisplayHeight / resources.playerImage.getHeight(this);
                int playerTargetY = (int)(h * 0.72) - (int)(resources.playerLowestY * playerScaleY) - 15;

                g.drawImage(resources.playerImage,
                        (int)(w * 0.12),
                        playerTargetY,
                        playerDisplayWidth,
                        playerDisplayHeight,
                        this);

                // Dibujar entrenador actual
                g.drawImage(resources.trainerImage,
                        (int)(w * 0.88),
                        (int)(h * 0.01),
                        (int)(w * 0.12),
                        (int)(h * 0.15),
                        this);
            }
        };
    }
    private void addBattleComponents(JPanel panel, String[] playerData, String[] enemyData) {
        JLabel battleText = new JLabel("¿Qué debería hacer " + playerData[1] + "?");
        battleText.setFont(Auxiliar.cargarFuentePixel(20));
        battleText.setForeground(Color.WHITE);
        battleText.setOpaque(false);
        panel.add(battleText);
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.GRAY);
        buttonContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));
        panel.add(buttonContainer);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);
        buttonContainer.add(buttonPanel, BorderLayout.CENTER);
        createActionButtons(buttonPanel);
        addEnemyInfo(panel, enemyData);
        addPlayerInfo(panel, playerData);
    }
    private void createActionButtons(JPanel container) {
        String[] options = {"ATACAR", "MOCHILA", "POKÉMON", "HUIR"};

        for (String option : options) {
            JButton btn = createActionButton(option);
            container.add(btn);

            // Configurar acciones
            if (option.equals("POKÉMON")) {
                btn.addActionListener(e -> showPanel("pokemon"));
            } else if (option.equals("ATACAR")) {
                btn.addActionListener(e -> showPanel("attack"));//showPanel("attack"));
            } else if (option.equals("HUIR")) {
                btn.addActionListener(e -> {
                    if (battleListener != null) {
                        battleListener.onBattleEnd(false);
                    }
                });
            } else if (option.equals("MOCHILA")) {
                btn.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Funcionalidad de items no implementada");
                });
            }
        }
    }
    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Auxiliar.cargarFuentePixel(16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 3));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(211, 211, 211));
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }
    private void addEnemyInfo(JPanel panel, String[] enemyData) {
        JLabel nameLabel = new JLabel(enemyData[1]);
        JLabel levelLabel = new JLabel("Nv. " + enemyData[4]);
        JLabel hpLabel = new JLabel(enemyData[6] + "/" + enemyData[5]);
        hpLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen hpBar = new BarraVidaConImagen(Integer.parseInt(enemyData[5]));
        hpBar.setValue(Integer.parseInt(enemyData[6]));

        panel.add(nameLabel);
        panel.add(levelLabel);
        panel.add(hpBar);
        panel.add(hpLabel);

        // Guardar referencias para el layout
        panel.putClientProperty("enemyName", nameLabel);
        panel.putClientProperty("enemyLevel", levelLabel);
        panel.putClientProperty("enemyHpBar", hpBar);
        panel.putClientProperty("enemyHpLabel", hpLabel);
    }
    private void addPlayerInfo(JPanel panel, String[] playerData) {
        JLabel nameLabel = new JLabel(playerData[1]);
        JLabel levelLabel = new JLabel("Nv. " + playerData[4]);
        JLabel hpLabel = new JLabel(playerData[6] + "/" + playerData[5]);
        hpLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen hpBar = new BarraVidaConImagen(Integer.parseInt(playerData[5]));
        hpBar.setValue(Integer.parseInt(playerData[6]));

        panel.add(nameLabel);
        panel.add(levelLabel);
        panel.add(hpBar);
        panel.add(hpLabel);

        // Guardar referencias para el layout
        panel.putClientProperty("playerName", nameLabel);
        panel.putClientProperty("playerLevel", levelLabel);
        panel.putClientProperty("playerHpBar", hpBar);
        panel.putClientProperty("playerHpLabel", hpLabel);
    }
    private void setupBattleLayout(JPanel panel) {
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();

                // Texto de batalla
                JLabel battleText = (JLabel) panel.getComponent(0);
                battleText.setBounds((int)(w * 0.038), (int)(h * 0.73), (int)(w * 0.48), (int)(h * 0.22));

                // Contenedor de botones
                JPanel buttonContainer = (JPanel) panel.getComponent(1);
                buttonContainer.setBounds((int)(w * 0.52), (int)(h * 0.715), (int)(w * 0.478), (int)(h * 0.265));

                // Información enemiga
                JLabel enemyName = (JLabel) panel.getClientProperty("enemyName");
                JLabel enemyLevel = (JLabel) panel.getClientProperty("enemyLevel");
                BarraVidaConImagen enemyHpBar = (BarraVidaConImagen) panel.getClientProperty("enemyHpBar");
                JLabel enemyHpLabel = (JLabel) panel.getClientProperty("enemyHpLabel");

                enemyName.setBounds((int)(w * 0.09), (int)(h * 0.09), (int)(w * 0.25), 30);
                enemyLevel.setBounds((int)(w * 0.31), (int)(h * 0.09), (int)(w * 0.15), 30);
                enemyHpBar.setBounds((int)(w * 0.12), (int)(h * 0.16), (int)(w * 0.3), 15);
                enemyHpLabel.setBounds((int)(w * 0.12), (int)(h * 0.19), (int)(w * 0.3), 30);

                // Información del jugador
                JLabel playerName = (JLabel) panel.getClientProperty("playerName");
                JLabel playerLevel = (JLabel) panel.getClientProperty("playerLevel");
                BarraVidaConImagen playerHpBar = (BarraVidaConImagen) panel.getClientProperty("playerHpBar");
                JLabel playerHpLabel = (JLabel) panel.getClientProperty("playerHpLabel");

                playerName.setBounds((int)(w * 0.6), (int)(h * 0.478), (int)(w * 0.25), 30);
                playerLevel.setBounds((int)(w * 0.82), (int)(h * 0.478), (int)(w * 0.15), 30);
                playerHpBar.setBounds((int)(w * 0.63), (int)(h * 0.55), (int)(w * 0.3), 15);
                playerHpLabel.setBounds((int)(w * 0.63), (int)(h * 0.58), (int)(w * 0.3), 30);
                //fonts
                playerName.setFont(Auxiliar.cargarFuentePixel(18));
                playerLevel.setFont(Auxiliar.cargarFuentePixel(18));
                playerHpLabel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyName.setFont(Auxiliar.cargarFuentePixel(18));
                enemyLevel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyHpLabel.setFont(Auxiliar.cargarFuentePixel(18));

            }
        });
    }
    //cambio de pokemon
    private JPanel createPokemonView() {
        JPanel panel = new ImagePanel(null, MENU+"p.png");
        panel.setName("pokemon");
        //Preparar componentes principales
        prepareMainComponents(panel);
        //Preparar panel del Pokémon actual
        prepareCurrentPokemonPanel(panel);
        //Preparar Pokémon inactivos
        prepareInactivePokemons(panel);
        //Configurar listeners y disposición final
        setupFinalLayout(panel);
        return panel;
    }
    private void prepareMainComponents(JPanel parentPanel) {
        // Botones y mensaje principal
        JButton confirmButton = Auxiliar.crearBotonEstilizado("Confirm", new Rectangle(1,1,1,1), new Color(122, 227, 0));
        JButton backButton = Auxiliar.crearBotonTransparente("Back", new Rectangle(1,1,1,1), false);
        JLabel message = new JLabel("Choose a Pokemon");

        confirmButton.setVisible(false);
        parentPanel.add(confirmButton);
        parentPanel.add(backButton);
        parentPanel.add(message);

        // Guardar referencias para uso posterior si es necesario
        parentPanel.putClientProperty("confirmButton", confirmButton);
        parentPanel.putClientProperty("backButton", backButton);
        parentPanel.putClientProperty("message", message);

        // Configurar acción del botón de volver
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "battle");
        });
    }
    private void prepareCurrentPokemonPanel(JPanel parentPanel) {
        HashMap<Integer, String[]> currentPokemons = this.game.getCurrentPokemons();
        String[] currentPlayerData = currentPokemons.get(this.currentPlayer);
        /** Está funcional
        for(String[][] s: this.game.getActiveAttacks().values()){
            for(String[] a: s){
                for(String p: a){
                    System.out.println(p);
                }
            }
        }
         **/
        JPanel currentPokemonPanel = new JPanel(null);
        currentPokemonPanel.setOpaque(false);
        // Imagen del Pokémon
        JPanel pokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" + currentPlayerData[2] + ".png");
        // Información del Pokémon
        JLabel nameLabel = new JLabel(currentPlayerData[1]);
        JLabel levelLabel = new JLabel("Nv. " + currentPlayerData[4]);
        JLabel hpLabel = new JLabel(currentPlayerData[6] + "/" + currentPlayerData[5]);
        // Configurar estilos
        Font pixelFont = Auxiliar.cargarFuentePixel(20);
        levelLabel.setFont(pixelFont);
        nameLabel.setFont(pixelFont);
        hpLabel.setFont(pixelFont);
        levelLabel.setForeground(Color.white);
        nameLabel.setForeground(Color.white);
        hpLabel.setForeground(Color.white);
        hpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        levelLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        // Barra de vida
        BarraVidaConImagen hpBar = new BarraVidaConImagen(Integer.parseInt(currentPlayerData[5]));
        hpBar.setValue(Integer.parseInt(currentPlayerData[6]));
        // Añadir componentes al panel
        currentPokemonPanel.add(pokemonImage);
        currentPokemonPanel.add(nameLabel);
        currentPokemonPanel.add(levelLabel);
        currentPokemonPanel.add(hpLabel);
        currentPokemonPanel.add(hpBar);
        // Configurar redimensionamiento
        currentPokemonPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                pokemonImage.setBounds(0, 0, (int)(parentPanel.getWidth() * 0.12), (int)(parentPanel.getHeight() * 0.17));
                nameLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.38), (int)(currentPokemonPanel.getHeight() *0.20),
                        (int)(parentPanel.getWidth() * 0.3), 20);
                levelLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.38), (int)(currentPokemonPanel.getHeight() *0.40),
                        (int)(parentPanel.getWidth() * 0.3), 20);
                hpLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.76),
                        (int)(parentPanel.getWidth() * 0.3), 20);
                hpBar.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.63),
                        (int)(parentPanel.getWidth() * 0.3), 15);
            }
        });
        parentPanel.add(currentPokemonPanel);
        parentPanel.putClientProperty("currentPokemonPanel", currentPokemonPanel);
    }
    private void prepareInactivePokemons(JPanel parentPanel) {
        int[] inactiveTeam = game.getPokemonsInactive(this.currentPlayer);
        ArrayList<JPanel> inactivePokemonPanels = new ArrayList<>();
        for (int i = 0; i < inactiveTeam.length; i++) {
            try {
                String[] pokemonInfo = game.getPokemonInfo(this.currentPlayer, inactiveTeam[i]);
                JPanel pokemonPanel = createInactivePokemonPanel(pokemonInfo, parentPanel);
                inactivePokemonPanels.add(pokemonPanel);
                parentPanel.add(pokemonPanel);
            } catch (POOBkemonException e) {
                System.err.println("Error al obtener información del Pokémon: " + e.getMessage());
            }
        }
        parentPanel.putClientProperty("inactivePokemonPanels", inactivePokemonPanels);
    }
    private JPanel createInactivePokemonPanel(String[] pokemonInfo, JPanel parentPanel) {
        JPanel panel = new JPanel(null);
        panel.setFont(Auxiliar.cargarFuentePixel(20));
        panel.setOpaque(false);

        // Imagen del Pokémon
        JPanel pokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" + pokemonInfo[2] + ".png");

        // Información del Pokémon
        JLabel nameLabel = new JLabel(pokemonInfo[1]);
        JLabel levelLabel = new JLabel("Nv. " + pokemonInfo[4]);
        JLabel hpLabel = new JLabel(pokemonInfo[6] + "/" + pokemonInfo[5]);

        // Configurar estilos
        Font pixelFont = Auxiliar.cargarFuentePixel(20);
        levelLabel.setFont(pixelFont);
        nameLabel.setFont(pixelFont);
        hpLabel.setFont(pixelFont);

        levelLabel.setForeground(Color.white);
        nameLabel.setForeground(Color.white);
        hpLabel.setForeground(Color.white);

        hpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Barra de vida
        BarraVidaConImagen hpBar = new BarraVidaConImagen(Integer.parseInt(pokemonInfo[5]));
        hpBar.setValue(Integer.parseInt(pokemonInfo[6]));

        // Añadir componentes al panel
        panel.add(pokemonImage);
        panel.add(nameLabel);
        panel.add(levelLabel);
        panel.add(hpLabel);
        panel.add(hpBar);

        // Configurar redimensionamiento
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                pokemonImage.setBounds(0, 0, (int)(panel.getHeight()), (int)(panel.getHeight()));
                nameLabel.setBounds((int)(panel.getWidth() *0.12), (int)(panel.getHeight() *0.10),
                        (int)(parentPanel.getWidth() * 0.2), 20);
                levelLabel.setBounds((int)(panel.getWidth() *0.12), (int)(panel.getHeight() *0.42),
                        (int)(parentPanel.getWidth() * 0.2), 20);
                hpLabel.setBounds((int)(panel.getWidth() *0.45), (int)(panel.getHeight() *0.40),
                        (int)(parentPanel.getWidth() * 0.3), 20);
                hpBar.setBounds((int)(panel.getWidth() *0.48), (int)(panel.getHeight() *0.10),
                        (int)(parentPanel.getWidth() * 0.285), 15);
            }
        });

        // Configurar listeners de selección
        if (Integer.parseInt(pokemonInfo[6]) > 0) { // Solo si tiene HP > 0
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(createPokemonSelectionListener(pokemonInfo, parentPanel));
        }

        return panel;
    }
    private MouseAdapter createPokemonSelectionListener(String[] pokemonInfo, JPanel parentPanel) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton confirmButton = (JButton) parentPanel.getClientProperty("confirmButton");
                JLabel message = (JLabel) parentPanel.getClientProperty("message");

                confirmButton.setVisible(true);
                message.setText("Choose " + pokemonInfo[1]);

                // Limpiar listeners previos
                for (ActionListener al : confirmButton.getActionListeners()) {
                    confirmButton.removeActionListener(al);
                }

                // Añadir nuevo listener
                confirmButton.addActionListener(a -> {
                    String[] decision = {"ChangePokemon", ""+currentPlayer, pokemonInfo[0]};
                    setDecision(decision);
                    showPanel("battle");
                });
            }
        };
    }
    private void setupFinalLayout(JPanel panel) {
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();

                JPanel currentPokemonPanel = (JPanel) panel.getClientProperty("currentPokemonPanel");
                @SuppressWarnings("unchecked")
                ArrayList<JPanel> inactivePokemonPanels = (ArrayList<JPanel>) panel.getClientProperty("inactivePokemonPanels");
                JButton confirmButton = (JButton) panel.getClientProperty("confirmButton");
                JLabel message = (JLabel) panel.getClientProperty("message");
                JButton backButton = (JButton) panel.getClientProperty("backButton");
                // Posicionar panel del Pokémon actual
                currentPokemonPanel.setBounds((int)(w * 0.05), (int)(h * 0.16), (int)(w * 0.315), (int)(h * 0.28));
                // Posicionar Pokémon inactivos
                float yPos = 0.065f;
                for (JPanel pokemonPanel : inactivePokemonPanels) {
                    pokemonPanel.setBounds((int)(w * 0.41), (int)(h * yPos), (int)(w * 0.58), (int)(h * 0.115));
                    yPos += 0.15f;
                }
                // Posicionar otros componentes
                confirmButton.setBounds((int)(currentPokemonPanel.getWidth() *0.35), (int)(h *0.5), (int)(w * 0.2), 50);
                message.setBounds((int)(currentPokemonPanel.getWidth() *0.08), (int)(h *0.84), (int)(w * 0.69), (int)(h * 0.115));
                backButton.setBounds((int)(w *0.82), (int)(h *0.86), (int)(w * 0.15), 40);
                // Front del panel de texto
                message.setFont(Auxiliar.cargarFuentePixel(30));
            }
        });
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escPressed");

        panel.getActionMap().put("escPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "battle");
            }
        });
    }
    //
    private JPanel createAtaquesView() {
        // 1. Obtener datos de los Pokémon
        BattleData battleData = prepareBattleData();

        // 2. Crear panel principal con fondo personalizado
        JPanel panel = createMainAttackPanel(battleData);

        // 3. Añadir información de los Pokémon
        addPokemonInfo(panel, battleData);

        // 4. Configurar panel de botones de ataques
        setupAttackButtons(panel, battleData);

        // 5. Configurar panel de información de movimientos
        setupMoveInfoPanel(panel);

        // 6. Configurar teclas rápidas
        setupKeyBindings(panel);

        // 7. Configurar redimensionamiento
        setupAttackViewLayout(panel);

        return panel;
    }

    // Clase auxiliar para agrupar datos de batalla
    private class BattleData {
        String[] playerData;
        String[] enemyData;
        Image background;
        Image playerImage;
        Image enemyImage;
        Image trainerImage;
        String[][] moves;
        String[] moveNames;
        String[] movePP;
        String[] moveTypes;
        String[] moveIds;
    }

    private BattleData prepareBattleData() {
        BattleData data = new BattleData();

        // Obtener datos de los Pokémon
        HashMap<Integer, String[]> currentPokemons = this.game.getCurrentPokemons();
        data.playerData = currentPokemons.get(this.order.get(0));
        data.enemyData = currentPokemons.get(this.order.get(1));

        // Preparar imágenes
        String playerImagePath = data.playerData[16].equals("true")
                ? BACK_SHINY_PATH + data.playerData[2] + PNG_EXT
                : BACK_PATH + data.playerData[2] + PNG_EXT;

        String enemyImagePath = data.enemyData[16].equals("true")
                ? SHINY_PATH + data.enemyData[2] + PNG_EXT
                : NORMAL_PATH + data.enemyData[2] + PNG_EXT;

        data.background = new ImageIcon(MENU + "battle_" + this.fondo + PNG_EXT).getImage();
        data.trainerImage = new ImageIcon(CHARACTER + this.currentPlayer + PNG_EXT).getImage();

        ImageIcon playerIcon = new ImageIcon(playerImagePath);
        data.playerImage = toBufferedImage(playerIcon.getImage());

        ImageIcon enemyIcon = new ImageIcon(enemyImagePath);
        data.enemyImage = toBufferedImage(enemyIcon.getImage());

        // Obtener movimientos
        data.moves = game.getActiveAttacks().get(currentPlayer);
        data.moveNames = new String[data.moves.length];
        data.movePP = new String[data.moves.length];
        data.moveTypes = new String[data.moves.length];
        data.moveIds = new String[data.moves.length];

        for (int i = 0; i < data.moves.length; i++) {
            data.moveNames[i] = data.moves[i][0];
            data.movePP[i] = data.moves[i][4];
            data.moveTypes[i] = data.moves[i][1];
            data.moveIds[i] = data.moves[i][5];
        }

        return data;
    }

    private JPanel createMainAttackPanel(BattleData data) {
        return new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                final int w = getWidth();
                final int h = getHeight();
                super.paintComponent(g);

                // Dibujar fondo
                g.drawImage(data.background, 0, 0, w, h, this);

                // Dibujar Pokémon enemigo
                int enemyDisplayWidth = (int)(w * 0.27);
                int enemyDisplayHeight = (int)(h * 0.4);
                g.drawImage(data.enemyImage,
                        (int)(w * 0.62),
                        (int)(h * 0.44) - (int)(findAbsoluteLowestVisibleY(data.enemyImage) *
                                ((double)enemyDisplayHeight / data.enemyImage.getHeight(null))) - 15,
                        enemyDisplayWidth,
                        enemyDisplayHeight,
                        this);

                // Dibujar Pokémon jugador
                int playerDisplayWidth = (int)(w * 0.25);
                int playerDisplayHeight = (int)(h * 0.3);
                g.drawImage(data.playerImage,
                        (int)(w * 0.12),
                        (int)(h * 0.72) - (int)(findAbsoluteLowestVisibleY(data.playerImage) *
                                ((double)playerDisplayHeight / data.playerImage.getHeight(null))) - 15,
                        playerDisplayWidth,
                        playerDisplayHeight,
                        this);

                // Dibujar entrenador
                g.drawImage(data.trainerImage,
                        (int)(w * 0.88),
                        (int)(h * 0.01),
                        (int)(w * 0.12),
                        (int)(h * 0.15),
                        this);
            }
        };
    }

    private void addPokemonInfo(JPanel panel, BattleData data) {
        // Información del enemigo
        JLabel enemyName = new JLabel(data.enemyData[1]);
        JLabel enemyLevel = new JLabel("Nv. " + data.enemyData[4]);
        JLabel enemyHpLabel = new JLabel(data.enemyData[6] + "/" + data.enemyData[5]);
        enemyHpLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen enemyHpBar = new BarraVidaConImagen(Integer.parseInt(data.enemyData[5]));
        enemyHpBar.setValue(Integer.parseInt(data.enemyData[6]));

        // Información del jugador
        JLabel playerName = new JLabel(data.playerData[1]);
        JLabel playerLevel = new JLabel("Nv. " + data.playerData[4]);
        JLabel playerHpLabel = new JLabel(data.playerData[6] + "/" + data.playerData[5]);
        playerHpLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen playerHpBar = new BarraVidaConImagen(Integer.parseInt(data.playerData[5]));
        playerHpBar.setValue(Integer.parseInt(data.playerData[6]));

        // Añadir componentes
        panel.add(enemyName);
        panel.add(enemyLevel);
        panel.add(enemyHpBar);
        panel.add(enemyHpLabel);
        panel.add(playerName);
        panel.add(playerLevel);
        panel.add(playerHpBar);
        panel.add(playerHpLabel);

        // Guardar referencias
        panel.putClientProperty("enemyName", enemyName);
        panel.putClientProperty("enemyLevel", enemyLevel);
        panel.putClientProperty("enemyHpBar", enemyHpBar);
        panel.putClientProperty("enemyHpLabel", enemyHpLabel);
        panel.putClientProperty("playerName", playerName);
        panel.putClientProperty("playerLevel", playerLevel);
        panel.putClientProperty("playerHpBar", playerHpBar);
        panel.putClientProperty("playerHpLabel", playerHpLabel);
    }

    private void setupAttackButtons(JPanel panel, BattleData data) {
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.GRAY);
        buttonContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);

        // Crear botones para cada movimiento
        for (int i = 0; i < data.moveNames.length; i++) {
            JButton attackButton = createAttackButton(data, i);
            buttonPanel.add(attackButton);
        }

        buttonContainer.add(buttonPanel, BorderLayout.CENTER);
        panel.add(buttonContainer);
        panel.putClientProperty("buttonContainer", buttonContainer);
    }

    private JButton createAttackButton(BattleData data, int index) {
        JButton btn = new JButton(data.moveNames[index]);
        btn.setFont(Auxiliar.cargarFuentePixel(16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 3));

        // Configurar hover effects
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(211, 211, 211));
                updateMoveInfo(btn.getParent().getParent(), data, index);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                clearMoveInfo(btn.getParent().getParent());
            }
        });

        // Configurar acción del botón
        btn.addActionListener(e -> {
            String[] decision = {"Attack", ""+currentPlayer, data.moveIds[index]};
            setDecision(decision);
            showPanel("battle");
        });

        return btn;
    }

    private void updateMoveInfo(JPanel panel, BattleData data, int index) {
        JLabel pp = (JLabel) panel.getClientProperty("ppLabel");
        JLabel cantPp = (JLabel) panel.getClientProperty("ppValueLabel");
        JLabel tipo = (JLabel) panel.getClientProperty("typeLabel");

        pp.setText("PP");
        cantPp.setText(data.movePP[index]);
        tipo.setText("TYPE/" + data.moveTypes[index]);
    }

    private void clearMoveInfo(JPanel panel) {
        JLabel pp = (JLabel) panel.getClientProperty("ppLabel");
        JLabel cantPp = (JLabel) panel.getClientProperty("ppValueLabel");
        JLabel tipo = (JLabel) panel.getClientProperty("typeLabel");

        pp.setText("");
        cantPp.setText("");
        tipo.setText("");
    }

    private void setupMoveInfoPanel(JPanel panel) {
        JPanel textPanel = new JPanel(null);
        textPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));
        textPanel.setOpaque(true);

        JLabel ppLabel = new JLabel("");
        JLabel ppValueLabel = new JLabel("");
        JLabel typeLabel = new JLabel("");

        textPanel.add(ppLabel);
        textPanel.add(ppValueLabel);
        textPanel.add(typeLabel);

        panel.add(textPanel);

        // Guardar referencias
        panel.putClientProperty("textPanel", textPanel);
        panel.putClientProperty("ppLabel", ppLabel);
        panel.putClientProperty("ppValueLabel", ppValueLabel);
        panel.putClientProperty("typeLabel", typeLabel);

        // Configurar layout del panel de información
        textPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                ppLabel.setBounds((int)(textPanel.getWidth()*0.1), (int)(textPanel.getHeight()*0.06),
                        (int)(textPanel.getWidth()*0.5), (int)(textPanel.getHeight()*0.38));
                ppValueLabel.setBounds((int)(textPanel.getWidth()*0.1), (int)(textPanel.getHeight()*0.06),
                        (int)(textPanel.getWidth()*0.83), (int)(textPanel.getHeight()*0.38));
                typeLabel.setBounds((int)(textPanel.getWidth()*0.1), (int)(textPanel.getHeight()*0.55),
                        (int)(textPanel.getWidth()*0.9), (int)(textPanel.getHeight()*0.38));

                ppLabel.setFont(Auxiliar.cargarFuentePixel(25));
                ppValueLabel.setFont(Auxiliar.cargarFuentePixel(25));
                typeLabel.setFont(Auxiliar.cargarFuentePixel(25));
            }
        });
    }

    private void setupKeyBindings(JPanel panel) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escPressed");

        panel.getActionMap().put("escPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("battle");
            }
        });
    }

    private void setupAttackViewLayout(JPanel panel) {
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();

                // Posicionar información de Pokémon
                JLabel enemyName = (JLabel) panel.getClientProperty("enemyName");
                JLabel enemyLevel = (JLabel) panel.getClientProperty("enemyLevel");
                BarraVidaConImagen enemyHpBar = (BarraVidaConImagen) panel.getClientProperty("enemyHpBar");
                JLabel enemyHpLabel = (JLabel) panel.getClientProperty("enemyHpLabel");

                enemyName.setBounds((int)(w * 0.09), (int)(h * 0.09), (int)(w * 0.25), 30);
                enemyLevel.setBounds((int)(w * 0.31), (int)(h * 0.09), (int)(w * 0.15), 30);
                enemyHpBar.setBounds((int)(w * 0.12), (int)(h * 0.16), (int)(w * 0.3), 15);
                enemyHpLabel.setBounds((int)(w * 0.12), (int)(h * 0.19), (int)(w * 0.3), 30);

                JLabel playerName = (JLabel) panel.getClientProperty("playerName");
                JLabel playerLevel = (JLabel) panel.getClientProperty("playerLevel");
                BarraVidaConImagen playerHpBar = (BarraVidaConImagen) panel.getClientProperty("playerHpBar");
                JLabel playerHpLabel = (JLabel) panel.getClientProperty("playerHpLabel");

                playerName.setBounds((int)(w * 0.6), (int)(h * 0.478), (int)(w * 0.25), 30);
                playerLevel.setBounds((int)(w * 0.82), (int)(h * 0.478), (int)(w * 0.15), 30);
                playerHpBar.setBounds((int)(w * 0.63), (int)(h * 0.55), (int)(w * 0.3), 15);
                playerHpLabel.setBounds((int)(w * 0.63), (int)(h * 0.58), (int)(w * 0.3), 30);

                // Posicionar contenedores
                JPanel textPanel = (JPanel) panel.getClientProperty("textPanel");
                JPanel buttonContainer = (JPanel) panel.getClientProperty("buttonContainer");

                textPanel.setBounds((int)(w * 0.69), (int)(h * 0.715), (int)(w * 0.31), (int)(h * 0.28));
                buttonContainer.setBounds((int)(w * 0.01), (int)(h * 0.715), (int)(w * 0.69), (int)(h * 0.28));

                // Actualizar fuentes
                enemyName.setFont(Auxiliar.cargarFuentePixel(18));
                enemyLevel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyHpLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerName.setFont(Auxiliar.cargarFuentePixel(18));
                playerLevel.setFont(Auxiliar.cargarFuentePixel(18));
                playerHpLabel.setFont(Auxiliar.cargarFuentePixel(18));
            }
        });
    }

    //

    private void showAttackAnimation(String attackName) {
        JPanel attackPanel = new JPanel(null) {
            private Image bgImage = new ImageIcon("resources/battle_bg.jpg").getImage();
            private Image playerImg = new ImageIcon("resources/pokemones/Emerald/Normal/" +1+ ".png").getImage();// game.getPlayerCurrentPokemonId()
            private Image enemyImg = new ImageIcon("resources/pokemones/Emerald/Normal/" + 2+ ".png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                g.drawImage(bgImage, 0, 0, w, h, this);
                g.drawImage(enemyImg, (int)(w * 0.58), (int)(h * 0.09), (int)(w * 0.27), (int)(h * 0.4), this);
                g.drawImage(playerImg, (int)(w * 0.12), (int)(h * 0.47), (int)(w * 0.25), (int)(h * 0.3), this);
            }
        };

        JLabel attackLabel = new JLabel("¡" + "name" + " usó " + attackName + "!");//game.getPlayerCurrentPokemonName()
        attackLabel.setFont(new Font("Arial", Font.BOLD, 30));
        attackLabel.setForeground(Color.WHITE);
        attackLabel.setBounds(50, 400, 700, 50);
        attackPanel.add(attackLabel);

        mainPanel.add(attackPanel, "attack_animation");
        cardLayout.show(mainPanel, "attack_animation");

        Timer timer = new Timer(2000, e -> {
            cardLayout.show(mainPanel, "battle");
            mainPanel.remove(attackPanel);
            updateAfterAttack();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateAfterAttack() {
        // Actualizar la UI después del ataque
        if ( 10<= 0 || 30 <= 0) {//game.getEnemyCurrentPokemonHP()-game.getPlayerCurrentPokemonHP()
            if (battleListener != null) {
                //battleListener.onBattleEnd(game.playerWon());
            }
        }
    }

    // 4. NUEVO MÉTODO PARA MANEJAR DECISIONES
    private void setDecision(String[] decision) {
        if (currentPlayer == order.get(0)) {
            decisionTrainer1 = decision;
        } else {
            decisionTrainer2 = decision;
        }

        if (decisionTrainer1 != null && decisionTrainer2 != null) {
            executeTurn(decisionTrainer1, decisionTrainer2);
        } else {
            switchPlayer();
        }
    }

    // 5. NUEVO MÉTODO PARA CAMBIAR DE JUGADOR
    private void switchPlayer() {
        currentPlayer = (currentPlayer == order.get(0)) ? order.get(1) : order.get(0);
        updateUIForCurrentPlayer();
    }
    private void executeTurn(String[] decisionT1, String[] decisionT2) {
        try {
            this.game.takeDecision(decisionT1);
            showPanel("battle");
            this.game.takeDecision(decisionT2);
            showPanel("battle");
        }catch (POOBkemonException e){
            System.out.println(e);
        }
        resetForNextTurn();
    }
    // 6. NUEVO MÉTODO PARA ACTUALIZAR UI SEGÚN JUGADOR ACTUAL
    private void updateUIForCurrentPlayer() {
        // Actualizar mensajes para indicar de quién es el turno
        // Podrías cambiar colores o añadir indicadores visuales
    }

    // 7. NUEVO MÉTODO PARA EJECUTAR EL TURNO
    private void executeTurn2() {
        turnInProgress = true;

        // Ejecutar las decisiones a través del juego
        int e;
        //game.takeDecision(decisionTrainer1, decisionTrainer2);
        // Mostrar animaciones según las acciones
        animateTurn();

        // Verificar si la batalla terminó

        if (game.finishBattle()) {
            if (battleListener != null) {
                int a;
                //battleListener.onBattleEnd(game.playerWon());
            }
            return;
        }


        // Preparar siguiente turno
        resetForNextTurn();
    }

    // 8. NUEVO MÉTODO PARA ANIMAR EL TURNO
    private void animateTurn() {
        // Primero animar la acción del jugador 1
        if (decisionTrainer1[0].equals("Attack")) {
            showAttackAnimation(1, decisionTrainer1[1]);
        } else if (decisionTrainer1[0].equals("ChangePokemon")) {
            showSwitchAnimation(1, decisionTrainer1[1]);
        } else if (decisionTrainer1[0].equals("Flee")) {
            showFleeAnimation(1);
        }

        // Luego animar la acción del jugador 2
        if (decisionTrainer2[0].equals("Attack")) {
            showAttackAnimation(2, decisionTrainer2[1]);
        } else if (decisionTrainer2[0].equals("ChangePokemon")) {
            showSwitchAnimation(2, decisionTrainer2[1]);
        } else if (decisionTrainer2[0].equals("Flee")) {
            showFleeAnimation(2);
        }
    }

    // 9. NUEVO MÉTODO PARA ANIMAR ATAQUES
    private void showAttackAnimation(int playerId, String attackId) {
        // [Usar tu método existente showAttackAnimation() pero adaptado]
        // Mostrar el panel de animación
        cardLayout.show(mainPanel, "animation");

        // Configurar temporizador para volver a la vista de batalla
        Timer timer = new Timer(2000, e -> {
            cardLayout.show(mainPanel, "battle");
        });
        timer.setRepeats(false);
        timer.start();
    }

    // 10. NUEVO MÉTODO PARA ANIMAR CAMBIOS DE POKÉMON
    private void showSwitchAnimation(int playerId, String pokemonId) {
        // Similar a showAttackAnimation pero para cambios
    }

    // 11. NUEVO MÉTODO PARA ANIMAR HUIDA
    private void showFleeAnimation(int playerId) {
        // Animación para cuando un jugador huye
    }

    // 12. NUEVO MÉTODO PARA REINICIAR ESTADO DEL TURNO
    private void resetForNextTurn() {
        decisionTrainer1 = null;
        decisionTrainer2 = null;
        turnInProgress = false;
        currentPlayer = order.get(0); // Volver al primer jugador en el orden
        updateUIForCurrentPlayer();

        // Actualizar la vista de batalla con los nuevos estados
        cardLayout.show(mainPanel, "battle");
    }

    // 13. NUEVO MÉTODO PARA CREAR VISTA DE ANIMACIÓN
    private JPanel createAnimationView() {
        JPanel panel = new JPanel(new BorderLayout());
        // Configurar este panel para mostrar animaciones
        return panel;
    }
    public void showPanel(String name) {
        for (Component comp : mainPanel.getComponents()) {
            if (name.equals(comp.getName())) {
                mainPanel.remove(comp);
                break;
            }
        }
        Supplier<JPanel> builder = panelBuilders.get(name);
        if (builder == null) {
            System.err.println("No panel builder found for: " + name);
            return;
        }
        JPanel panel = builder.get();
        panel.setName(name); // ¡Muy importante!
        mainPanel.add(panel, name);
        cardLayout.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}