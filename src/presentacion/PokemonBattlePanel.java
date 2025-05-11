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
    private static final String MAP = MENU+"map/";
    private static final String FRAME_ATTACK = MENU+"frameAttack/";
    private static final String FRAME = MENU+"frame/";
    private static final String frontFloor = MENU+"frontFloor/";
    private static final String backFloor = MENU+"backFloor/";
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
    private int frame=0,fondo = 0;

    // Estado de la batalla
    private String[] decisionTrainer1 = null;
    private String[] decisionTrainer2 = null;
    private boolean turnInProgress = false;
    private ArrayList<Integer> order;

    public interface BattleListener {
        void onBattleEnd(boolean playerWon);
    }

    public PokemonBattlePanel(POOBkemon game,int fondo,int frame) {
        if (game == null) throw new IllegalArgumentException("Game cannot be null");
        this.game = game;
        this.order = game.getOrder();
        this.currentPlayer = game.getOrder().get(0);
        this.fondo = fondo;
        this.frame = frame;
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
        JPanel initialPanel = panelBuilders.get("battle").get();
        initialPanel.setName("battle");
        mainPanel.add(initialPanel, "battle");
    }
    public void setBattleListener(BattleListener listener) {
        this.battleListener = listener;
    }
    //panatalla de pelea
    private JPanel createBattleView(){
        JPanel panel = createUpPanel();
        HashMap<Integer, String[]> currentPokemons = this.game.getCurrentPokemons();

        JPanel framePanel = new ImagePanel(null,FRAME_ATTACK+this.frame+PNG_EXT);
        JLabel battleText = new JLabel("What should \n" +currentPokemons.get(this.currentPlayer)[1] + " do?");//game.getPlayerCurrentPokemonName()
        battleText.setFont(Auxiliar.cargarFuentePixel(5));
        battleText.setOpaque(false);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.GRAY);
        buttonContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);
        String[] options = {"ATTACK", "ITEM", "POKÉMON", "RUN"};

        for (String option : options) {
            JButton btn = new JButton(option);
            btn.setFont(Auxiliar.cargarFuentePixel(18));
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

            if (option.equals("POKÉMON")) {
                btn.addActionListener(e -> {
                    this.showPanel("pokemon");;
                });
            } else if(option.equals("ATTACK")) {
                btn.addActionListener(e -> showPanel("attack"));
            } else if(option.equals("RUN")) {
                btn.addActionListener(e -> {
                    if (battleListener != null) {
                        battleListener.onBattleEnd(false);
                    }
                    //setDecision(new String[]{"Flee", ""}); // Decisión de huir
                });
            } else if(option.equals("ITEM")) {
                btn.addActionListener(e -> {
                    // TODO: Implementar vista de items
                    JOptionPane.showMessageDialog(this, "Funcionalidad de items no implementada");
                });
            }
            buttonPanel.add(btn);
        }
        buttonContainer.add(buttonPanel, BorderLayout.CENTER);
        framePanel.add(buttonContainer);
        framePanel.add(battleText);
        framePanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = framePanel.getWidth();
                int h = framePanel.getHeight();
                int fontSize = Math.max(12, h / 24);
                battleText.setFont(Auxiliar.cargarFuentePixel(20));
                battleText.setForeground(Color.WHITE);
                battleText.setBounds((int)(w * 0.03), (int)(h * 0.135), (int)(w * 0.465), (int)(h * 0.730));
                buttonContainer.setBounds((int)(w * 0.51), (int)(h * 0.03), (int)(w * 0.48), (int)(h * 0.95));
            }
        });


        panel.add(framePanel);

        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();
                framePanel.setBounds((int)(w * 0), (int)(h * 0.70), (int)(w * 1), (int)(h * 0.3));
            }
        });

        return panel;
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
        JButton confirmButton = Auxiliar.crearBotonEstilizado("Confirm", new Rectangle(1,1,1,1), new Color(4, 132, 25));
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
            showPanel("battle");
        });
    }
    private void prepareCurrentPokemonPanel(JPanel parentPanel) {
        HashMap<Integer, String[]> currentPokemons = this.game.getCurrentPokemons();
        String[] currentPlayerData = currentPokemons.get(this.currentPlayer);
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
                message.setText("Chose " + pokemonInfo[1]);

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
                showPanel("battle");
            }
        });
    }
    //
    private JPanel createAtaquesView() {
        HashMap<Integer,String[]> currentPokemons = this.game.getCurrentPokemons();
        JPanel panel = createUpPanel();
        JPanel frame = new ImagePanel(null,FRAME_ATTACK+this.frame+PNG_EXT);
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.GRAY);
        buttonContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);

        JPanel textPanel = new JPanel(null);
        textPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));
        textPanel.setOpaque(true);
        panel.add(textPanel); //?
        JLabel pp = new JLabel("");
        JLabel cantPp = new JLabel("");
        JLabel tipo = new JLabel("");
        pp.setHorizontalAlignment(SwingConstants.LEFT); //Posicionamiento del texto
        cantPp.setHorizontalAlignment(SwingConstants.RIGHT);
        tipo.setHorizontalAlignment(SwingConstants.LEFT);
        textPanel.add(pp);
        textPanel.add(cantPp);
        textPanel.add(tipo);

        textPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                pp.setBounds((int)(textPanel.getWidth()*0.1), (int)(textPanel.getHeight()*0.06), (int)(textPanel.getWidth()*0.5), (int)(textPanel.getHeight()*0.38));
                cantPp.setBounds((int)(textPanel.getWidth()*0.1), (int)(textPanel.getHeight()*0.06), (int)(textPanel.getWidth()*0.83), (int)(textPanel.getHeight()*0.38));
                tipo.setBounds((int)(textPanel.getWidth()*0.1), (int)(textPanel.getHeight()*0.55),(int)(textPanel.getWidth()*0.9), (int)(textPanel.getHeight()*0.38));
                pp.setFont(Auxiliar.cargarFuentePixel(25));
                cantPp.setFont(Auxiliar.cargarFuentePixel(25));
                tipo.setFont(Auxiliar.cargarFuentePixel(25));
            }
        });

        String[][] moves = game.getActiveAttacks().get(currentPlayer);

        String[] moveNames = new String[moves.length];
        String[] movePP = new String[moves.length];
        String[] moveMaxPP = new String[moves.length];
        String[] moveType = new String[moves.length];
        String[] moveId = new String[moves.length];
        for (int i = 0; i < moves.length; i++) {
            moveNames[i] = moves[i][0];
            movePP[i] = moves[i][4];
            moveType[i] = moves[i][1];
            moveMaxPP[i] = moves[i][5];
            moveId[i] = moves[i][8]; //agregar id de movimiento
        }


        for(int i=0; i<moves.length; i++) {
            final int index = i;
            JButton btn = new JButton(moveNames[i]);
            btn.setFont(Auxiliar.cargarFuentePixel(18));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.DARK_GRAY);
            btn.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 3));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    pp.setText("PP");
                    cantPp.setText(movePP[index]+"/"+moveMaxPP[index]);
                    tipo.setText("TYPE/"+moveType[index]);
                }
                public void mouseExited(MouseEvent evt) {
                    pp.setText("");
                    cantPp.setText("");
                    tipo.setText("");
                }
            });

            btn.addActionListener(e ->{
                if(movePP[index].equals("0")) {}else{
                String[] decision = {"Attack",moveId[index],currentPokemons.get(this.currentPlayer)[0],""+currentPlayer};//moveId[index] añadir id de movimiento
                setDecision(decision);
                showPanel("battle");}
            });

            buttonPanel.add(btn);
        }
        buttonContainer.add(buttonPanel, BorderLayout.CENTER);
        frame.add(buttonContainer);
        frame.add(textPanel);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = frame.getWidth();
                int h = frame.getHeight();
                int fontSize = Math.max(12, h / 24);
                textPanel.setFont(Auxiliar.cargarFuentePixel(20));
                textPanel.setForeground(Color.WHITE);
                textPanel.setBounds((int)(w * 0.03), (int)(h * 0.135), (int)(w * 0.465), (int)(h * 0.730));
                buttonContainer.setBounds((int)(w * 0.51), (int)(h * 0.03), (int)(w * 0.48), (int)(h * 0.95));
            }
        });
        panel.add(frame);

        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();
                frame.setBounds((int)(w * 0), (int)(h * 0.70), (int)(w * 1), (int)(h * 0.3));
            }
        });
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escPressed");

        panel.getActionMap().put("escPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("battle");
            }
        });
        return panel;
    }
    //
    private JPanel createUpPanel(){
        HashMap<Integer,String[]> currentPokemons = this.game.getCurrentPokemons();
        final String[] player = this.game.getCurrentPokemons().get(this.order.get(0));
        final String[] enemy = this.game.getCurrentPokemons().get(this.order.get(1));
        final String playerPokemon = player[16].equals("true")
                ? BACK_SHINY_PATH + player[2] + PNG_EXT
                : BACK_PATH + player[2] + PNG_EXT;
        final String enemyPokemon = enemy[16].equals("true")
                ? SHINY_PATH + enemy[2] + PNG_EXT
                : NORMAL_PATH + enemy[2] + PNG_EXT;
        final Image bg = new ImageIcon(MAP + this.fondo + PNG_EXT).getImage();
        final Image currentPlayerImg = new ImageIcon(CHARACTER + this.currentPlayer + PNG_EXT).getImage();
        final ImageIcon playerIcon = new ImageIcon(playerPokemon);
        final BufferedImage playerBufferedImg = toBufferedImage(playerIcon.getImage());
        final int playerLowestY = findAbsoluteLowestVisibleY(playerBufferedImg);
        final ImageIcon enemyIcon = new ImageIcon(enemyPokemon);
        final BufferedImage enemyBufferedImg = toBufferedImage(enemyIcon.getImage());
        final int enemyLowestY = findAbsoluteLowestVisibleY(enemyBufferedImg);
        final double PLAYER_TARGET_RATIO = 0.72;  // 72% para jugador
        final double ENEMY_TARGET_RATIO = 0.44;   // 44% para enemigo
        final int MARGIN = 15;                    // Margen para ambos

        // Crear paneles para las imágenes
        JPanel playerImagePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(playerBufferedImg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        playerImagePanel.setOpaque(false);

        JPanel enemyImagePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(enemyBufferedImg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        enemyImagePanel.setOpaque(false);

        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                final int w = getWidth();
                final int h = getHeight();
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, w, h, this);
                g.drawImage(currentPlayerImg,
                        (int)(w * 0.88),
                        (int)(h * 0.01),
                        (int)(w * 0.12),
                        (int)(h * 0.15),
                        this);
            }
        };

        // Configurar el layout y añadir los paneles de imágenes
        panel.add(playerImagePanel);
        panel.add(enemyImagePanel);
        JLabel enemyNameLabel = new JLabel(enemy[1]);//getEnemyCurrentPokemonName()
        JLabel enemyLevelLabel = new JLabel("Nv. " + enemy[4]);//getEnemyCurrentPokemonLevel()
        JLabel enemyHPLabel = new JLabel(enemy[6]+"/"+enemy[5]);//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
        enemyHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel playerNameLabel = new JLabel(player[1]);
        JLabel playerLevelLabel = new JLabel("Nv. " + player[4]);
        JLabel playerHPLabel = new JLabel(player[6]+"/"+player[5]);
        playerHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen enemyHPBar = new BarraVidaConImagen(Integer.parseInt(enemy[5]));//getEnemyCurrentPokemonMaxHP()
        enemyHPBar.setValue(Integer.parseInt(enemy[6]));//getEnemyCurrentPokemonHP()enemy[6]

        BarraVidaConImagen playerHPBar = new BarraVidaConImagen(Integer.parseInt(player[5]));
        playerHPBar.setValue(Integer.parseInt(player[6]));//player[6]
        ImagePanel front = new ImagePanel(null,frontFloor+fondo+PNG_EXT);
        front.setOpaque(false);
        ImagePanel back = new ImagePanel(null,backFloor+fondo+PNG_EXT);
        front.setOpaque(false);


        ImagePanel playerPanel = new ImagePanel(null,MENU+"player"+PNG_EXT);
        playerPanel.setOpaque(false);
        JPanel playerEfects = new JPanel(new GridLayout(1,0,3,3));
        playerEfects.setOpaque(false);
        playerPanel.add(playerEfects);
        playerPanel.add(playerNameLabel);
        playerPanel.add(playerLevelLabel);
        playerPanel.add(playerHPBar);
        playerPanel.add(playerHPLabel);
        playerPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();
                int fontSize = Math.max(12, h / 24);
                playerEfects.setBounds((int)(w * 0.05), (int)(h * 0.12), (int)(w * 0.23), 30);
                playerNameLabel.setBounds((int)(w * 0.06), (int)(h * 0.02), (int)(w * 0.25), 30);
                playerLevelLabel.setBounds((int)(w * 0.28), (int)(h * 0.02), (int)(w * 0.15), 30);
                playerHPBar.setBounds((int)(playerPanel.getWidth() * 0.2), (int)(h * 0.09), (int)(w * 0.3), 15);
                playerHPLabel.setBounds((int)(playerPanel.getWidth() * 0.2), (int)(h * 0.12), (int)(w * 0.3), 30);
                playerHPLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerNameLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerLevelLabel.setFont(Auxiliar.cargarFuentePixel(18));
            }
        });
        ImagePanel enemyPanel = new ImagePanel(null,MENU+"enemy"+PNG_EXT);
        enemyPanel.setOpaque(false);
        JPanel enemyEfects = new JPanel(new GridLayout(1,0,3,3));
        enemyEfects.setOpaque(false);
        enemyPanel.add(enemyEfects);
        enemyPanel.add(enemyNameLabel);
        enemyPanel.add(enemyLevelLabel);
        enemyPanel.add(enemyHPBar);
        enemyPanel.add(enemyHPLabel);
        enemyPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();
                int fontSize = Math.max(12, h / 24);
                enemyEfects.setBounds((int)(w * 0.02), (int)(h * 0.14), (int)(w * 0.23), 30);
                enemyNameLabel.setBounds((int)(w * 0.03), (int)(h * 0.03), (int)(w * 0.25), 30);
                enemyLevelLabel.setBounds((int)(w * 0.25), (int)(h * 0.03), (int)(w * 0.15), 30);
                enemyHPBar.setBounds((int)(enemyPanel.getWidth() * 0.15), (int)(h * 0.10), (int)(w * 0.3), 15);
                enemyHPLabel.setBounds((int)(enemyPanel.getWidth() * 0.15), (int)(h * 0.13), (int)(w * 0.3), 30);
                enemyHPLabel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyNameLabel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyLevelLabel.setFont(Auxiliar.cargarFuentePixel(18));
            }
        });

        panel.add(playerPanel);
        panel.add(enemyPanel);
        panel.add(front);
        panel.add(back);
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();

                // Posicionamiento y tamaño para la imagen del enemigo
                int enemyDisplayWidth = (int)(w * 0.27);
                int enemyDisplayHeight = (int)(h * 0.4);
                double enemyScaleY = (double)enemyDisplayHeight / enemyBufferedImg.getHeight();
                int enemyTargetY = (int)(h * ENEMY_TARGET_RATIO) - (int)(enemyLowestY * enemyScaleY) - MARGIN;

                enemyImagePanel.setBounds(
                        (int)(w * 0.62),
                        enemyTargetY,
                        enemyDisplayWidth,
                        enemyDisplayHeight
                );

                // Posicionamiento y tamaño para la imagen del jugador
                int playerDisplayWidth = (int)(w * 0.25);
                int playerDisplayHeight = (int)(h * 0.3);
                double playerScaleY = (double)playerDisplayHeight / playerBufferedImg.getHeight();
                int playerTargetY = (int)(h * PLAYER_TARGET_RATIO) - (int)(playerLowestY * playerScaleY) - MARGIN;

                playerImagePanel.setBounds(
                        (int)(w * 0.12),
                        playerTargetY,
                        playerDisplayWidth,
                        playerDisplayHeight
                );
                back.setBounds((int)(w * 0.50), (int)(h * 0.355), (int)(w * 0.50), (int)(h * 0.15));
                front.setBounds(0, (int)(h * 0.577), (int)(w * 0.625), (int)(h * 0.12));
                enemyPanel.setBounds((int)(w * 0.05), (int)(h * 0.05), (int)(w * 0.43), (int)(h * 0.255));
                playerPanel.setBounds((int)(w * 0.53), (int)(h * 0.45), (int)(w * 0.43), (int)(h * 0.23));
            }
        });
        return panel;
    }
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
            showPanel("battle");
            mainPanel.remove(attackPanel);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setDecision(String[] decision) {
        if (currentPlayer == order.get(0)) {
            decisionTrainer1 = decision;
        } else {
            decisionTrainer2 = decision;
        }

        if (decisionTrainer1 != null && decisionTrainer2 != null) {
            executeTurn();
        } else {
            switchPlayer();
        }
    }

    // 5. NUEVO MÉTODO PARA CAMBIAR DE JUGADOR
    private void switchPlayer() {
        currentPlayer = (currentPlayer == order.get(0)) ? order.get(1) : order.get(0);
    }
    private void executeTurn() {
        try {
            // Primero mostrar las animaciones
            if (decisionTrainer1[0].equals("Attack")) {
                showAttackAnimation(decisionTrainer1[2]); // Usando el ID del ataque
            } else if (decisionTrainer1[0].equals("ChangePokemon")) {
                showSwitchAnimation(order.get(0), decisionTrainer1[2]); // ID del nuevo Pokémon
            }

            if (decisionTrainer2[0].equals("Attack")) {
                showAttackAnimation(decisionTrainer2[2]);
            } else if (decisionTrainer2[0].equals("ChangePokemon")) {
                showSwitchAnimation(order.get(1), decisionTrainer2[2]);
            }

            // Luego procesar las decisiones
            this.game.takeDecision(decisionTrainer1);
            this.game.takeDecision(decisionTrainer2);
            if(this.game.finishBattle()){
                System.out.println("se murio");
            }

        } catch (POOBkemonException e) {
            System.err.println("Error al procesar turno: " + e.getMessage());
        }

        // Usar un Timer para esperar que terminen las animaciones antes de resetear
        Timer timer = new Timer(5000, e -> {

        });
        resetForNextTurn();
        timer.setRepeats(false);
        timer.start();

    }


    private void showSwitchAnimation(int playerId, String pokemonId) {

    }

    private void showAttackAnimation(int playerId, String attackId) {
        // [Usar tu método existente showAttackAnimation() pero adaptado]
        // Mostrar el panel de animación


        // Configurar temporizador para volver a la vista de batalla
        Timer timer = new Timer(2000, e -> {
            showPanel("battle");
        });
        timer.setRepeats(false);
        timer.start();
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
        currentPlayer = order.get(0);
        showPanel("battle");
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
    }

}