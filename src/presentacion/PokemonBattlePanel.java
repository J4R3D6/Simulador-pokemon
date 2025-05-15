package presentacion;

import domain.Log;
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
    private static final String status = "resources/menu/status/";
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
    private boolean newTurn = true;
    private int frame=0,fondo = 0;

    // Estado de la batalla
    private String[] decisionTrainer1 = null;
    private String[] decisionTrainer2 = null;
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
        newTurn = false;
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel, BorderLayout.CENTER);
        // Registrar creadores de paneles
        panelBuilders.put("battle", this::createBattleView);
        panelBuilders.put("pokemon", this::createPokemonView);
        panelBuilders.put("attack", this::createAtaquesView);
        panelBuilders.put("items", this::createItemsView);
        JPanel initialPanel = panelBuilders.get("battle").get();
        initialPanel.setName("battle");
        mainPanel.add(initialPanel, "battle");
    }
    public void setBattleListener(BattleListener listener) {
        this.battleListener = listener;
    }
    //pantalla de pelea
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
                btn.addActionListener(e -> showPanel("items"));
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
                battleText.setForeground(Color.white);
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
        HashMap<Integer,String[]> currentPokemons = this.game.getCurrentPokemons();
        String[] curentplayer = currentPokemons.get(this.currentPlayer);
        JPanel panel = new ImagePanel(null,MENU+"p.png");
        JButton confirmButton = Auxiliar.crearBotonEstilizado("Confirm", new Rectangle(1,1,1,1), new Color(4, 132, 25));
        JButton backButton = Auxiliar.crearBotonTransparente("Back",new Rectangle(1,1,1,1), false);
        JLabel message = new JLabel("Choose a Pokemon");
        confirmButton.setVisible(false);
        JPanel currentPokemonPanel = new JPanel(null);
        currentPokemonPanel.setOpaque(false);
        JPanel selectedPokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" +curentplayer[2]+ ".png");
        selectedPokemonImage.setOpaque(false);
        JLabel selectedNameLabel = new JLabel(curentplayer[1]);//getPlayerCurrentPokemonName()
        JLabel selectedLevel = new JLabel("Nv. " + curentplayer[4]);//getPlayerCurrentPokemonLevel()
        JLabel selectedHPLabel = new JLabel(curentplayer[6]+"/"+curentplayer[5]);//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
        selectedLevel.setFont(Auxiliar.cargarFuentePixel(20));
        selectedNameLabel.setFont(Auxiliar.cargarFuentePixel(20));
        selectedHPLabel.setFont(Auxiliar.cargarFuentePixel(20));
        selectedLevel.setForeground(Color.white);
        selectedNameLabel.setForeground(Color.white);
        selectedHPLabel.setForeground(Color.white);
        selectedHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        selectedLevel.setHorizontalAlignment(SwingConstants.LEFT);
        selectedNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        BarraVidaConImagen selectedHpBar = new BarraVidaConImagen( Integer.parseInt(curentplayer[5]));//getPlayerCurrentPokemonMaxHP())
        selectedHpBar.setValue(Integer.parseInt(curentplayer[6]));//getPlayerCurrentPokemonHP() // game.getPlayerCurrentPokemonHP() <(game.getPlayerCurrentPokemonMaxHP()

        JPanel currentPokemonEfect = new ImagePanel(null,status+"brn"+PNG_EXT);
        currentPokemonPanel.add(selectedPokemonImage);
        currentPokemonPanel.add(selectedNameLabel);
        currentPokemonPanel.add(selectedLevel);
        currentPokemonPanel.add(selectedHPLabel);
        currentPokemonPanel.add(selectedHpBar);
        currentPokemonPanel.add(currentPokemonEfect);
        currentPokemonPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                selectedPokemonImage.setBounds(0, 0,  (int)(panel.getWidth() * 0.12), (int)(panel.getHeight() * 0.17));
                selectedNameLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.38), (int)(currentPokemonPanel.getHeight() *0.20),  (int)(panel.getWidth() * 0.3), 20);
                selectedLevel.setBounds((int)(currentPokemonPanel.getWidth() *0.38), (int)(currentPokemonPanel.getHeight() *0.40),  (int)(panel.getWidth() * 0.3), 20);
                selectedHPLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.76),  (int)(panel.getWidth() * 0.3), 20);
                selectedHpBar.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.63),  (int)(panel.getWidth() * 0.3), 15);
                currentPokemonEfect.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.78),  (int)(panel.getWidth() * 0.065), (int)(panel.getHeight() * 0.038));
            }
        });
        int[] pokeTeam= game.getPokemonsInactive(this.currentPlayer);
        String[] pokemonNames = new String[pokeTeam.length];
        int[] pokemonLevels = new int[pokeTeam.length];
        int[] pokemonHPs = new int[pokeTeam.length];
        int[] pokemonMaxHPs = new int[pokeTeam.length];
        int[] pokemonIdPokedex = new int[pokeTeam.length];
        int[] pokemonId = new int[pokeTeam.length];
        try {
            for (int i = 0; i < pokeTeam.length; i++) {

                pokemonNames[i] = game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[1];//game.getPokemonName(pokeTeam[i]);
                pokemonLevels[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[4]);//game.getPokemonLevel(team[i]);
                pokemonHPs[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[6]);//game.getPokemonHP(team[i]);
                pokemonMaxHPs[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[5]);//game.getPokemonMaxHP(team[i]);
                pokemonIdPokedex[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[2]);
                pokemonId[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[0]);
            }
        } catch (POOBkemonException e) {
            Log.record(e);
            throw new RuntimeException(e);
        }
        final int[] newindex = {0};
        ArrayList<JPanel> inactivePokemons = new ArrayList<>();
        for(int i= 0 ;i < pokeTeam.length; i++  ){
            final int index = i;
            JPanel pokemonPanel = new JPanel(null);
            pokemonPanel.setFont(Auxiliar.cargarFuentePixel(20));
            pokemonPanel.setOpaque(false);
            JPanel PokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" +pokemonIdPokedex[i]+ ".png");
            PokemonImage.setOpaque(false);
            JLabel NameLabel = new JLabel(pokemonNames[i]);//getPlayerCurrentPokemonName()
            JLabel Level = new JLabel("Nv. " + pokemonLevels[i]);//getPlayerCurrentPokemonLevel()
            JLabel HPLabel = new JLabel(pokemonHPs[i]+"/"+pokemonMaxHPs[i]);//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
            Level.setFont(Auxiliar.cargarFuentePixel(20));
            NameLabel.setFont(Auxiliar.cargarFuentePixel(20));
            HPLabel.setFont(Auxiliar.cargarFuentePixel(20));
            Level.setForeground(Color.white);
            NameLabel.setForeground(Color.white);
            HPLabel.setForeground(Color.white);
            HPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            Level.setHorizontalAlignment(SwingConstants.CENTER);
            NameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            BarraVidaConImagen HpBar = new BarraVidaConImagen( pokemonMaxHPs[i]);//getPlayerCurrentPokemonMaxHP())
            HpBar.setValue(pokemonHPs[i]);//getPlayerCurrentPokemonHP() // game.getPlayerCurrentPokemonHP() <(game.getPlayerCurrentPokemonMaxHP()
            JPanel pokemonEfect = new ImagePanel(null,status+"slp"+PNG_EXT);
            pokemonPanel.add(pokemonEfect);
            pokemonPanel.add(PokemonImage);
            pokemonPanel.add(NameLabel);
            pokemonPanel.add(Level);
            pokemonPanel.add(HPLabel);
            pokemonPanel.add(HpBar);
            pokemonPanel.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    PokemonImage.setBounds(0, 0,  (int)(pokemonPanel.getHeight()), (int)(pokemonPanel.getHeight()));
                    NameLabel.setBounds((int)(pokemonPanel.getWidth() *0.12), (int)(pokemonPanel.getHeight() *0.10),  (int)(panel.getWidth() * 0.2), 20);
                    Level.setBounds((int)(pokemonPanel.getWidth() *0.12), (int)(pokemonPanel.getHeight() *0.42),  (int)(panel.getWidth() * 0.2), 20);
                    HPLabel.setBounds((int)(pokemonPanel.getWidth() *0.45), (int)(pokemonPanel.getHeight() *0.40),  (int)(panel.getWidth() * 0.3), 20);
                    HpBar.setBounds((int)(pokemonPanel.getWidth() *0.48), (int)(pokemonPanel.getHeight() *0.10),  (int)(panel.getWidth() * 0.285), 15);
                    pokemonEfect.setBounds((int)(pokemonPanel.getWidth() *0.48), (int)(pokemonPanel.getHeight() *0.43),  (int)(panel.getWidth() * 0.065), (int)(panel.getHeight() * 0.038));
                }
            });
            pokemonPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if(pokemonHPs[index] > 0){
                        newindex[0]=pokemonId[index];
                        confirmButton.setVisible(true);
                        message.setText("Choose "+pokemonNames[index]);
                        confirmButton.addActionListener(a -> {
                            String[] decision = {"ChangePokemon", ""+currentPlayer, ""+pokemonId[index]};
                            setDecision(decision);
                            showPanel("battle");
                        });
                    }
                }
            });
            pokemonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            inactivePokemons.add(pokemonPanel);
            panel.add(pokemonPanel);
        }
        backButton.addActionListener(e -> {
            showPanel("battle");
        });
        panel.add(backButton);
        panel.add(currentPokemonPanel);
        panel.add(confirmButton);
        panel.add(message);
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int fontSize = Math.max(12, h / 24);

                currentPokemonPanel.setFont(Auxiliar.cargarFuentePixel(20));
                currentPokemonPanel.setForeground(Color.WHITE);
                currentPokemonPanel.setBounds((int) (panel.getWidth() * 0.05), (int) (panel.getHeight() * 0.16), (int) (panel.getWidth() * 0.315), (int) (panel.getHeight() * 0.28));
                float b = 0.065f;
                for (int i = 0; i < pokeTeam.length; i++, b += 0.15f){
                    inactivePokemons.get(i).setBounds((int) (panel.getWidth() * 0.41), (int) (panel.getHeight() * b), (int) (panel.getWidth() * 0.58), (int) (panel.getHeight() * 0.115));
                }
                confirmButton.setBounds((int)(currentPokemonPanel.getWidth() *0.35), (int)(panel.getHeight() *0.5),  (int)(panel.getWidth() * 0.2), 50);
                confirmButton.setFont(Auxiliar.cargarFuentePixel(20));
                message.setBounds((int)(currentPokemonPanel.getWidth() *0.08), (int)(panel.getHeight() *0.84),  (int)(panel.getWidth() * 0.69), (int) (panel.getHeight() * 0.115));
                message.setFont(Auxiliar.cargarFuentePixel(30));
                backButton.setBounds((int)(panel.getWidth() *0.82), (int)(panel.getHeight() *0.86),  (int)(panel.getWidth() * 0.15), 40);
                backButton.setFont(Auxiliar.cargarFuentePixel(20));
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
                    if(movePP[index].equals("-1")){}else {cantPp.setText("Inf");
                    cantPp.setText(movePP[index]+"/"+moveMaxPP[index]);}
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
    private JPanel createItemsView() {
        JPanel panel = new ImagePanel(null,MENU+"i"+PNG_EXT);
        JButton use = Auxiliar.crearBotonTransparente("Confirm", new Rectangle(1, 1, 1, 1), false);
        int[] pokeTeam = game.getPokemonsPerTrainer(this.currentPlayer);
        String[] pokemonNames = new String[pokeTeam.length];
        int[] pokemonLevels = new int[pokeTeam.length];
        int[] pokemonHPs = new int[pokeTeam.length];
        int[] pokemonMaxHPs = new int[pokeTeam.length];
        int[] pokemonIdPokedex = new int[pokeTeam.length];
        int[] pokemonId = new int[pokeTeam.length];
        try {
            for (int i = 0; i < pokeTeam.length; i++) {
                pokemonNames[i] = game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[1];//game.getPokemonName(pokeTeam[i]);
                pokemonLevels[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[4]);//game.getPokemonLevel(team[i]);
                pokemonHPs[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[6]);//game.getPokemonHP(team[i]);
                pokemonMaxHPs[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[5]);//game.getPokemonMaxHP(team[i]);
                pokemonIdPokedex[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[2]);
                pokemonId[i] = Integer.parseInt(game.getPokemonInfo(this.currentPlayer, pokeTeam[i])[0]);
            }
        } catch (POOBkemonException e) {
            throw new RuntimeException(e);
        }
        final int[] newindex = {0};
        final int[] health = {0};
        final boolean[] selectPokemon = {false};
        ArrayList<JPanel> inactivePokemons = new ArrayList<>();
        for (int i = 0; i < pokeTeam.length; i++) {
            final int index = i;
            JPanel pokemonPanel = new JPanel(null);
            pokemonPanel.setFont(Auxiliar.cargarFuentePixel(20));
            pokemonPanel.setOpaque(false);
            JPanel PokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" + pokemonIdPokedex[i] + ".png");
            PokemonImage.setOpaque(false);
            JLabel NameLabel = new JLabel(pokemonNames[i]);//getPlayerCurrentPokemonName()
            JLabel Level = new JLabel("Nv. " + pokemonLevels[i]);//getPlayerCurrentPokemonLevel()
            JLabel HPLabel = new JLabel(pokemonHPs[i] + "/" + pokemonMaxHPs[i]);//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
            Level.setFont(Auxiliar.cargarFuentePixel(20));
            NameLabel.setFont(Auxiliar.cargarFuentePixel(20));
            HPLabel.setFont(Auxiliar.cargarFuentePixel(20));
            Level.setForeground(Color.white);
            NameLabel.setForeground(Color.white);
            HPLabel.setForeground(Color.white);
            HPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            Level.setHorizontalAlignment(SwingConstants.CENTER);
            NameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            BarraVidaConImagen HpBar = new BarraVidaConImagen(pokemonMaxHPs[i]);//getPlayerCurrentPokemonMaxHP())
            HpBar.setValue(pokemonHPs[i]);//getPlayerCurrentPokemonHP() // game.getPlayerCurrentPokemonHP() <(game.getPlayerCurrentPokemonMaxHP()
            JPanel pokemonEfect = new ImagePanel(null,status+"slp"+PNG_EXT);
            pokemonPanel.add(pokemonEfect);
            pokemonPanel.add(PokemonImage);
            pokemonPanel.add(NameLabel);
            pokemonPanel.add(Level);
            pokemonPanel.add(HPLabel);
            pokemonPanel.add(HpBar);
            pokemonPanel.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    PokemonImage.setBounds(0, 0, (int) (pokemonPanel.getHeight()), (int) (pokemonPanel.getHeight()));
                    NameLabel.setBounds((int) (pokemonPanel.getWidth() * 0.12), (int) (pokemonPanel.getHeight() * 0.10), (int) (panel.getWidth() * 0.2), 20);
                    Level.setBounds((int) (pokemonPanel.getWidth() * 0.12), (int) (pokemonPanel.getHeight() * 0.42), (int) (panel.getWidth() * 0.2), 20);
                    HPLabel.setBounds((int) (pokemonPanel.getWidth() * 0.45), (int) (pokemonPanel.getHeight() * 0.40), (int) (panel.getWidth() * 0.3), 20);
                    HpBar.setBounds((int) (pokemonPanel.getWidth() * 0.48), (int) (pokemonPanel.getHeight() * 0.10), (int) (panel.getWidth() * 0.285), 15);
                    pokemonEfect.setBounds((int)(pokemonPanel.getWidth() *0.48), (int)(pokemonPanel.getHeight() *0.43),  (int)(panel.getWidth() * 0.065), (int)(panel.getHeight() * 0.038));
                }
            });
            pokemonPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    health[0] = pokemonHPs[index];
                    newindex[0] = pokemonId[index];
                    selectPokemon[0] = true;

                }
            });
            pokemonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            inactivePokemons.add(pokemonPanel);
            panel.add(pokemonPanel);
        }
        //ArrayList<ArrayList<String>>
        String[][] items= null;
        try {
            items = this.game.getInfoItems(this.currentPlayer);
        } catch (POOBkemonException e) {
            Log.record(e);
        }
        final String[] itemName = {"0"};
        final String[] itemAmount = {"0"};
        final  boolean[] selectItem = {false};

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        JPanel itemsPanel = new JPanel(new GridBagLayout());
        itemsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        final int ITEM_HEIGHT = 50;
        final int ITEM_WIDTH = 280;
        for(int i = 0; i < items.length; i++){
            final int index = i;
            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setPreferredSize(new Dimension(ITEM_WIDTH, ITEM_HEIGHT));
            itemPanel.setMaximumSize(new Dimension(ITEM_WIDTH, ITEM_HEIGHT));
            itemPanel.setOpaque(false);
            JPanel imagePanel = new ImagePanel(null, "resources/Items/"+items[index][0]+PNG_EXT);
            imagePanel.setPreferredSize(new Dimension(50, 40));
            JLabel nameLabel = new JLabel(items[index][0]+" X "+items[index][1]);
            nameLabel.setFont(Auxiliar.cargarFuentePixel(14));
            itemPanel.add(imagePanel, BorderLayout.WEST);
            itemPanel.add(nameLabel, BorderLayout.CENTER);
            String[][] finalItems = items;
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    itemName[0] = finalItems[index][0];
                    itemAmount[0] = finalItems[index][1];
                    selectItem[0] = true;
                }
            });
            itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            itemsPanel.add(itemPanel, gbc);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)), gbc); // Espacio entre items
        }
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scrollPane);
        panel.add(use);
        use.addActionListener(e -> {
                    if (selectItem[0] && selectPokemon[0]) {
                        if (itemName[0].toLowerCase().equalsIgnoreCase("revive") && health[0] > 0) {
                            Auxiliar.mostrarError("Item", "This item cannot be used on Pokémon that are not fainted.");
                        } else if (itemName[0].toLowerCase().contains("potion") && health[0] <= 0) {
                            Auxiliar.mostrarError("Item", "This item cannot be used on Pokémon that are fainted.");
                        } else {
                            String[] decision = {"UseItem",String.valueOf(this.currentPlayer), String.valueOf(newindex[0]),itemName[0]};
                            setDecision(decision);
                            showPanel("battle");
                        }
                    }else if (selectItem[0] && !selectPokemon[0]) {
                        Auxiliar.mostrarError("Item", "unselected Pokemon");
                    }else if (!selectItem[0] && selectPokemon[0]) {
                        Auxiliar.mostrarError("Item", "unselected Item");
                    }else {Auxiliar.mostrarError("Item", "no option was selected");}
        });
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                scrollPane.setBounds((int) (panel.getWidth() * 0.06), (int) (panel.getHeight() * 0.16), (int) (panel.getWidth() * 0.3), (int) (panel.getHeight() * 0.61));
                use.setBounds((int) (panel.getWidth() * 0.09), (int) (panel.getHeight() * 0.86), (int) (panel.getWidth() * 0.24), (int) (panel.getHeight() * 0.09));
                use.setHorizontalAlignment(SwingConstants.RIGHT);
                float b = 0.065f;
                for (int i = 0; i < pokeTeam.length; i++, b += 0.15f){
                    inactivePokemons.get(i).setBounds((int) (panel.getWidth() * 0.41), (int) (panel.getHeight() * b), (int) (panel.getWidth() * 0.58), (int) (panel.getHeight() * 0.115));
                }
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
        HashMap currentPokemons = this.game.getCurrentPokemons();
        //final String[] player = (String[]) currentPokemons.get(this.order.get(0));
        //final String[] enemy = (String[]) currentPokemons.get(this.order.get(1));
        final String[] player = (String[]) currentPokemons.get(this.currentPlayer==this.order.get(0)?this.order.get(0):this.order.get(1));
        final String[] enemy = (String[]) currentPokemons.get(this.currentPlayer==this.order.get(0)?this.order.get(1):this.order.get(0));

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
        final double PLAYER_TARGET_RATIO = 0.72;
        final double ENEMY_TARGET_RATIO = 0.44;
        final int MARGIN = 15;

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
                g.drawImage(currentPlayerImg, (int)(w * 0.88), (int)(h * 0.01), (int)(w * 0.12), (int)(h * 0.15), this);
            }
        };

        panel.add(playerImagePanel);
        panel.add(enemyImagePanel);

        JLabel enemyNameLabel = new JLabel(enemy[1]);
        JLabel enemyLevelLabel = new JLabel("Nv. " + enemy[4]);
        JLabel enemyHPLabel = new JLabel(enemy[6] + "/" + enemy[5]);
        enemyHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel playerNameLabel = new JLabel(player[1]);
        JLabel playerLevelLabel = new JLabel("Nv. " + player[4]);
        JLabel playerHPLabel = new JLabel(player[6] + "/" + player[5]);
        playerHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen enemyHPBar = new BarraVidaConImagen(Integer.parseInt(enemy[5]));
        enemyHPBar.setValue(Integer.parseInt(enemy[6]));

        BarraVidaConImagen playerHPBar = new BarraVidaConImagen(Integer.parseInt(player[5]));
        playerHPBar.setValue(Integer.parseInt(player[6]));

        ImagePanel front = new ImagePanel(null, frontFloor + fondo + PNG_EXT);
        front.setOpaque(false);
        ImagePanel back = new ImagePanel(null, backFloor + fondo + PNG_EXT);
        back.setOpaque(false);

        ImagePanel playerPanel = new ImagePanel(null, MENU + "player" + PNG_EXT);
        playerPanel.setOpaque(false);
        JPanel playerEfects = new ImagePanel(null,status+"brn"+PNG_EXT);
        playerEfects.setOpaque(false);
        playerPanel.setVisible(true);
        playerPanel.add(playerEfects);
        playerPanel.add(playerNameLabel);
        playerPanel.add(playerLevelLabel);
        playerPanel.add(playerHPBar);
        playerPanel.add(playerHPLabel);

        playerPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();
                playerEfects.setBounds((int)(playerPanel.getWidth() * 0.20), (int)(h * 0.135), (int)(w * 0.065), (int)(h * 0.04));
                playerNameLabel.setBounds((int)(w * 0.06), (int)(h * 0.02), (int)(w * 0.25), 30);
                playerLevelLabel.setBounds((int)(w * 0.28), (int)(h * 0.02), (int)(w * 0.15), 30);
                playerHPBar.setBounds((int)(playerPanel.getWidth() * 0.2), (int)(h * 0.09), (int)(w * 0.3), 15);
                playerHPLabel.setBounds((int)(playerPanel.getWidth() * 0.2), (int)(h * 0.12), (int)(w * 0.3), 30);
                playerHPLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerNameLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerLevelLabel.setFont(Auxiliar.cargarFuentePixel(18));
            }
        });

        ImagePanel enemyPanel = new ImagePanel(null, MENU + "enemy" + PNG_EXT);
        enemyPanel.setOpaque(false);
        JPanel enemyEfects = new ImagePanel(null,status+"brn"+PNG_EXT);
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
                enemyEfects.setBounds((int)(w * 0.065), (int)(h * 0.145), (int)(w * 0.065), (int)(h * 0.04));
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

                int enemyDisplayWidth = (int)(w * 0.27);
                int enemyDisplayHeight = (int)(h * 0.4);
                double enemyScaleY = (double)enemyDisplayHeight / enemyBufferedImg.getHeight();
                int enemyTargetY = (int)(h * ENEMY_TARGET_RATIO) - (int)(enemyLowestY * enemyScaleY) - MARGIN;

                enemyImagePanel.setBounds((int)(w * 0.62), enemyTargetY, enemyDisplayWidth, enemyDisplayHeight);

                int playerDisplayWidth = (int)(w * 0.25);
                int playerDisplayHeight = (int)(h * 0.3);
                double playerScaleY = (double)playerDisplayHeight / playerBufferedImg.getHeight();
                int playerTargetY = (int)(h * PLAYER_TARGET_RATIO) - (int)(playerLowestY * playerScaleY) - MARGIN;

                playerImagePanel.setBounds((int)(w * 0.12), playerTargetY, playerDisplayWidth, playerDisplayHeight);

                back.setBounds((int)(w * 0.50), (int)(h * 0.355), (int)(w * 0.50), (int)(h * 0.15));
                front.setBounds(0, (int)(h * 0.577), (int)(w * 0.625), (int)(h * 0.12));
                enemyPanel.setBounds((int)(w * 0.05), (int)(h * 0.05), (int)(w * 0.43), (int)(h * 0.255));
                playerPanel.setBounds((int)(w * 0.53), (int)(h * 0.45), (int)(w * 0.43), (int)(h * 0.23));
            }
        });
        if (newTurn) {
            newTurn = false;

            playerPanel.setVisible(false);
            enemyPanel.setVisible(false);

            panel.addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent e) {
                    int w = panel.getWidth();
                    int h = panel.getHeight();

                    int targetEnemyX = (int)(w * 0.62);
                    int targetBackX = (int)(w * 0.50);
                    int targetPlayerX = (int)(w * 0.12);
                    int targetFrontX = 0;

                    int targetEnemyY = enemyImagePanel.getY();
                    int targetBackY = back.getY();
                    int targetPlayerY = playerImagePanel.getY();
                    int targetFrontY = front.getY();

                    enemyImagePanel.setLocation(-enemyImagePanel.getWidth(), targetEnemyY);
                    back.setLocation(-back.getWidth(), targetBackY);
                    playerImagePanel.setLocation(w, targetPlayerY);
                    front.setLocation(w, targetFrontY);

                    Timer animationTimer = new Timer(10, null);
                    animationTimer.addActionListener(new ActionListener() {
                        int frame = 0;
                        final int totalFrames = 30;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame++;
                            double progress = (double) frame / totalFrames;

                            enemyImagePanel.setLocation((int)(-enemyImagePanel.getWidth() * (1 - progress) + targetEnemyX * progress), targetEnemyY);
                            back.setLocation((int)(-back.getWidth() * (1 - progress) + targetBackX * progress), targetBackY);
                            playerImagePanel.setLocation((int)(w * (1 - progress) + targetPlayerX * progress), targetPlayerY);
                            front.setLocation((int)(w * (1 - progress) + targetFrontX * progress), targetFrontY);

                            if (frame >= totalFrames) {
                                animationTimer.stop();
                                enemyImagePanel.setLocation(targetEnemyX, targetEnemyY);
                                back.setLocation(targetBackX, targetBackY);
                                playerImagePanel.setLocation(targetPlayerX, targetPlayerY);
                                front.setLocation(targetFrontX, targetFrontY);
                                playerPanel.setVisible(true);
                                enemyPanel.setVisible(true);
                            }
                        }
                    });
                    animationTimer.start();
                }
            });
        }

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
            newTurn = true;
        } else {
            switchPlayer();
        }
    }

    // 5. NUEVO MÉTODO PARA CAMBIAR DE JUGADOR
    private void switchPlayer() {
        newTurn = true;
        currentPlayer = (currentPlayer == order.get(0)) ? order.get(1) : order.get(0);
    }
    private void executeTurn() {
        try {
            // Primero mostrar las animaciones
            if (decisionTrainer1[0].equals("Attack")) {
                //showAttackAnimation(decisionTrainer1[2]); // Usando el ID del ataque
            } else if (decisionTrainer1[0].equals("ChangePokemon")) {
                //showSwitchAnimation(order.get(0), decisionTrainer1[2]); // ID del nuevo Pokémon
            }else if (decisionTrainer1[0].equals("UseItem")) {
                //showSwitchAnimation(order.get(0), decisionTrainer1[2]); // ID del nuevo Pokémon
            }

            if (decisionTrainer2[0].equals("Attack")) {
                //showAttackAnimation(decisionTrainer2[2]);
            }else if (decisionTrainer2[0].equals("ChangePokemon")) {
                //showSwitchAnimation(order.get(1), decisionTrainer2[2]);
            }else if (decisionTrainer2[0].equals("UseItem")) {
                //showSwitchAnimation(order.get(0), decisionTrainer1[2]); // ID del nuevo Pokémon
            }

            // Luego procesar las decisiones
            this.game.takeDecision(decisionTrainer1);
            this.game.takeDecision(decisionTrainer2);

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


    // 11. NUEVO MÉTODO PARA ANIMAR HUIDA
    private void showFleeAnimation(int playerId) {
        // Animación para cuando un jugador huye
    }

    // 12. NUEVO MÉTODO PARA REINICIAR ESTADO DEL TURNO
    private void resetForNextTurn() {
        newTurn = true;
        decisionTrainer1 = null;
        decisionTrainer2 = null;
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
        Timer timer = new Timer(00, e -> {
            cardLayout.show(mainPanel, name);
        });
        timer.setRepeats(false);
        timer.start();
    }

}