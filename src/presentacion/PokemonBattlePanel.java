package presentacion;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;

import domain.*;

import static java.awt.SystemColor.text;

public class PokemonBattlePanel extends JPanel implements Auxiliar{
    private static final String CHARACTER = "resources/personaje/";
    private static final String MENU = "resources/menu/";
    private static final String POKEMONES =  "resources/pokemones/Emerald/";
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private POOBkemon game;
    private BattleListener battleListener;
    private ArrayList<Integer> order;
    private int fondo = 1;
    private int currentPlayer;

    public interface BattleListener {
        void onBattleEnd(boolean playerWon);
    }

    public PokemonBattlePanel(POOBkemon game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
        this.order = game.getOrder();
        this.currentPlayer = this.order.get(0);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel, BorderLayout.CENTER);

        JPanel battleView = createBattleView();
        JPanel pokemonView = createPokemonView();
        JPanel attackView = createAtaquesView();

        mainPanel.add(battleView, "battle");
        mainPanel.add(pokemonView, "pokemon");
        mainPanel.add(attackView, "attack");
    }

    public void setBattleListener(BattleListener listener) {
        this.battleListener = listener;
    }

    private JPanel createBattleView(){
        HashMap<Integer,String[]> currentPokemons = this.game.getCurrentPokemons();
        String[] player = currentPokemons.get(this.order.get(0));
        String[] enemy = currentPokemons.get(this.order.get(1));
        String playerPokemon;
        String enemyPokemon;
        if(player[16].equals("true")){
            playerPokemon = POKEMONES+"BackShiny/"+player[2]+".png";
        } else {
            playerPokemon = POKEMONES + "Back/" + player[2] + ".png";
        }
        if(enemy[16].equals("true")){
            enemyPokemon = POKEMONES+"Shiny/"+enemy[2]+".png";
        } else {
            enemyPokemon = POKEMONES + "Normal/" +enemy[2]+ ".png";
        }
        String currentPlayer = CHARACTER+this.currentPlayer+".png";
        Image bg = new ImageIcon(MENU+"battle"+this.fondo+".png").getImage();
        ImageIcon playerIcon = new ImageIcon(playerPokemon);
        BufferedImage playerBufferedImg = toBufferedImage(playerIcon.getImage());
        Image enemyImg = new ImageIcon(enemyPokemon).getImage();
        Image currentPlayerImg = new ImageIcon(currentPlayer).getImage();
        int lowestVisibleY = findAbsoluteLowestVisibleY(playerBufferedImg);
        final double TARGET_HEIGHT_RATIO = 0.72; // 72% de la altura
        final int DESIRED_MARGIN = 15; // Margen adicional
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                final int w = getWidth();
                final int h = getHeight();

                super.paintComponent(g);
                g.drawImage(bg, 0, 0, w, h, this);
                g.drawImage(enemyImg, (int)(w*0.62), (int)(h*0.073), (int)(w*0.27), (int)(h*0.4), this);
                int displayWidth = (int)(w*0.25);
                int displayHeight = (int)(h*0.3);
                double scaleY = (double)displayHeight / playerBufferedImg.getHeight();
                int targetY = (int)(h * TARGET_HEIGHT_RATIO) - (int)(lowestVisibleY * scaleY) - DESIRED_MARGIN;
                g.drawImage(playerBufferedImg,
                        (int)(w*0.12),
                        targetY,
                        displayWidth,
                        displayHeight,
                        this);
                g.drawImage(currentPlayerImg, (int)(w*0.88), (int)(h*0.01), (int)(w*0.12), (int)(h*0.15), this);
            }
        };
        JLabel battleText = new JLabel("¿Qué debería hacer " +currentPokemons.get(this.currentPlayer)[1] + "?");//game.getPlayerCurrentPokemonName()
        battleText.setFont(Auxiliar.cargarFuentePixel(5));
        battleText.setOpaque(false);
        panel.add(battleText);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.GRAY);
        buttonContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));
        panel.add(buttonContainer);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);
        String[] options = {"ATACAR", "MOCHILA", "POKÉMON", "HUIR"};

        for (String option : options) {
            JButton btn = new JButton(option);
            btn.setFont(Auxiliar.cargarFuentePixel(16));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.DARK_GRAY);

            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    btn.setBackground(new Color(211, 211, 211));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 3));
                }

                public void mouseExited(MouseEvent evt) {
                    btn.setBackground(Color.WHITE);
                    btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                }
            });

            if (option.equals("POKÉMON")) {
                btn.addActionListener(e -> cardLayout.show(mainPanel, "pokemon"));
            } else if(option.equals("ATACAR")) {
                btn.addActionListener(e -> cardLayout.show(mainPanel, "attack"));
            } else if(option.equals("HUIR")) {
                btn.addActionListener(e -> {
                    if (battleListener != null) {
                        battleListener.onBattleEnd(false);
                    }
                });
            }

            buttonPanel.add(btn);
        }

        buttonContainer.add(buttonPanel, BorderLayout.CENTER);

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

        panel.add(enemyNameLabel);
        panel.add(enemyLevelLabel);
        panel.add(enemyHPBar);
        panel.add(enemyHPLabel);
        panel.add(playerNameLabel);
        panel.add(playerLevelLabel);
        panel.add(playerHPBar);
        panel.add(playerHPLabel);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int fontSize = Math.max(12, h / 24);

                battleText.setFont(Auxiliar.cargarFuentePixel(20));
                battleText.setForeground(Color.WHITE);
                battleText.setBounds((int)(w * 0.038), (int)(h * 0.73), (int)(w * 0.48), (int)(h * 0.22));

                buttonContainer.setBounds((int)(w * 0.52), (int)(h * 0.715), (int)(w * 0.478), (int)(h * 0.265));

                enemyNameLabel.setBounds((int)(w * 0.09), (int)(h * 0.09), (int)(w * 0.25), 30);
                enemyNameLabel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyLevelLabel.setBounds((int)(w * 0.31), (int)(h * 0.09), (int)(w * 0.15), 30);
                enemyLevelLabel.setFont(Auxiliar.cargarFuentePixel(18));
                enemyHPBar.setBounds((int)(w * 0.12), (int)(h * 0.16), (int)(w * 0.3), 15);
                enemyHPLabel.setBounds((int)(w * 0.12), (int)(h * 0.19), (int)(w * 0.3), 30);
                enemyHPLabel.setFont(Auxiliar.cargarFuentePixel(18));

                playerNameLabel.setBounds((int)(w * 0.6), (int)(h * 0.478), (int)(w * 0.25), 30);
                playerNameLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerLevelLabel.setBounds((int)(w * 0.82), (int)(h * 0.478), (int)(w * 0.15), 30);
                playerLevelLabel.setFont(Auxiliar.cargarFuentePixel(18));
                playerHPBar.setBounds((int)(w * 0.63), (int)(h * 0.55), (int)(w * 0.3), 15);
                playerHPLabel.setBounds((int)(w * 0.63), (int)(h * 0.58), (int)(w * 0.3), 30);
                playerHPLabel.setFont(Auxiliar.cargarFuentePixel(18));
            }
        });

        return panel;
    }

    private JPanel createPokemonView() {
        HashMap<Integer,String[]> currentPokemons = this.game.getCurrentPokemons();
        String[] curentplayer = currentPokemons.get(this.currentPlayer);
        JPanel panel = new ImagePanel(null,MENU+"p.png");
        JButton confirmButton = Auxiliar.crearBotonEstilizado("Confirm", new Rectangle(1,1,1,1), new Color(4, 132, 25));
        JButton backButton = Auxiliar.crearBotonTransparente("Back",new Rectangle(1,1,1,1), false);
        JLabel message = new JLabel("Chose a Pokemon");
        confirmButton.setVisible(false);
        JPanel currentPokemonPanel = new JPanel(null);
        currentPokemonPanel.setOpaque(false);
        JPanel selectedPokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" +curentplayer[2]+ ".png");
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
        currentPokemonPanel.add(selectedPokemonImage);
        currentPokemonPanel.add(selectedNameLabel);
        currentPokemonPanel.add(selectedLevel);
        currentPokemonPanel.add(selectedHPLabel);
        currentPokemonPanel.add(selectedHpBar);
        currentPokemonPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                selectedPokemonImage.setBounds(0, 0,  (int)(panel.getWidth() * 0.12), (int)(panel.getHeight() * 0.17));
                selectedNameLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.38), (int)(currentPokemonPanel.getHeight() *0.20),  (int)(panel.getWidth() * 0.3), 20);
                selectedLevel.setBounds((int)(currentPokemonPanel.getWidth() *0.38), (int)(currentPokemonPanel.getHeight() *0.40),  (int)(panel.getWidth() * 0.3), 20);
                selectedHPLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.76),  (int)(panel.getWidth() * 0.3), 20);
                selectedHpBar.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.63),  (int)(panel.getWidth() * 0.3), 15);
                }
        });
        System.out.println(this.currentPlayer);
        int[] pokeTeam= game.getPokemonsInactive(this.currentPlayer); //(equipo desacttivado) (metodo de equipo desactivado)
        System.out.println(Arrays.toString(pokeTeam));

        System.out.println(Arrays.toString(game.getPokemonsInactive(0)));

        System.out.println(Arrays.toString(game.getPokemonsInactive(1)));
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
        }catch (POOBkemonException e){
            System.out.println("Error al obtener datos del equipo");//implementar bien
        }
        final int[] newindex = {0};
        ArrayList<JPanel> inactivePokemons = new ArrayList<>();
        for(int i= 0 ;i < pokeTeam.length; i++  ){
            final int id = i;
            JPanel pokemonPanel = new JPanel(null);
            pokemonPanel.setFont(Auxiliar.cargarFuentePixel(20));
            pokemonPanel.setOpaque(true);
            JPanel PokemonImage = new ImagePanel(null, "resources/pokemones/Emerald/Icon/" +pokemonIdPokedex[i]+ ".png");
            selectedPokemonImage.setBackground(Color.BLACK);/////////////
            selectedPokemonImage.setOpaque(true);//////////////////
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
            pokemonPanel.add(PokemonImage);
            pokemonPanel.add(NameLabel);
            pokemonPanel.add(Level);
            pokemonPanel.add(HPLabel);
            pokemonPanel.add(HpBar);
            pokemonPanel.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    PokemonImage.setBounds(-10, -25,  (int)(panel.getWidth() * 0.1), (int)(panel.getHeight() * 0.15));
                    NameLabel.setBounds((int)(pokemonPanel.getWidth() *0.12), (int)(pokemonPanel.getHeight() *0.10),  (int)(panel.getWidth() * 0.2), 20);
                    Level.setBounds((int)(pokemonPanel.getWidth() *0.12), (int)(pokemonPanel.getHeight() *0.42),  (int)(panel.getWidth() * 0.2), 20);
                    HPLabel.setBounds((int)(pokemonPanel.getWidth() *0.45), (int)(pokemonPanel.getHeight() *0.40),  (int)(panel.getWidth() * 0.3), 20);
                    HpBar.setBounds((int)(pokemonPanel.getWidth() *0.48), (int)(pokemonPanel.getHeight() *0.10),  (int)(panel.getWidth() * 0.285), 15);
                }
            });
            pokemonPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    newindex[0]=pokemonId[id];
                    confirmButton.setVisible(true);
                    message.setText("Chose "+pokemonNames[id]);
                    //confirmButton.setActionLister();
                }
            });
            pokemonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            inactivePokemons.add(pokemonPanel);
            panel.add(pokemonPanel);
        }
        backButton.addActionListener(e -> {cardLayout.show(mainPanel,"battle");});
        panel.add(backButton);
        panel.add(currentPokemonPanel);
        panel.add(confirmButton);
        panel.add(message);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int fontSize = Math.max(12, h / 24);

                currentPokemonPanel.setFont(Auxiliar.cargarFuentePixel(20));
                currentPokemonPanel.setForeground(Color.WHITE);
                currentPokemonPanel.setBounds((int) (panel.getWidth() * 0.05), (int) (panel.getHeight() * 0.16), (int) (panel.getWidth() * 0.315), (int) (panel.getHeight() * 0.28));
                float b = 0.065f;
                for (int i = 0; i < pokeTeam.length; i++, b += 0.15f){
                    inactivePokemons.get(i).setBounds((int) (panel.getWidth() * 0.43), (int) (panel.getHeight() * b), (int) (panel.getWidth() * 0.55), (int) (panel.getHeight() * 0.115));
                }
                confirmButton.setBounds((int)(currentPokemonPanel.getWidth() *0.35), (int)(panel.getHeight() *0.5),  (int)(panel.getWidth() * 0.2), 50);
                confirmButton.setFont(Auxiliar.cargarFuentePixel(20));
                message.setBounds((int)(currentPokemonPanel.getWidth() *0.08), (int)(panel.getHeight() *0.84),  (int)(panel.getWidth() * 0.69), (int) (panel.getHeight() * 0.115));
                message.setFont(Auxiliar.cargarFuentePixel(30));
                backButton.setBounds((int)(panel.getWidth() *0.82), (int)(panel.getHeight() *0.86),  (int)(panel.getWidth() * 0.15), 40);
                backButton.setFont(Auxiliar.cargarFuentePixel(20));
            }
        });
        return panel;
    }

    private JPanel createAtaquesView() {
        JPanel panel = new JPanel(null) {
            private Image bgImage = new ImageIcon("resources/battle_bg.jpg").getImage();
            private Image playerImg = new ImageIcon("resources/pokemones/Emerald/Normal/" + 1+ ".png").getImage();//getPlayerCurrentPokemonId()
            private Image enemyImg = new ImageIcon("resources/pokemones/Emerald/Normal/" +  2 + ".png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                g.drawImage(bgImage, 0, 0, w, h, this);
                g.drawImage(enemyImg, (int)(w * 0.58), (int)(h * 0.09), (int)(w * 0.27), (int)(h * 0.4), this);
                g.drawImage(playerImg, (int)(w * 0.12), (int)(h * 0.47), (int)(w * 0.25), (int)(h * 0.3), this);
            }
        };

        JPanel buttonContainer = new JPanel(null);
        buttonContainer.setBackground(Color.WHITE);
        buttonContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 6));
        buttonContainer.setBounds(10, 400, 530, 150);
        panel.add(buttonContainer);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setBounds(10, 10, 510, 130);
        buttonContainer.add(buttonPanel);

        JPanel textPanel = new JPanel(null);
        textPanel.setOpaque(true);
        textPanel.setBounds(555, 405, 220, 140);
        panel.add(textPanel);

        JLabel ppLabel = new JLabel("PP");
        JLabel ppValueLabel = new JLabel();
        JLabel typeLabel = new JLabel();

        ppLabel.setFont(new Font("Arial", Font.BOLD, 25));
        ppValueLabel.setFont(new Font("Arial", Font.BOLD, 25));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 25));

        ppLabel.setForeground(Color.darkGray);
        ppValueLabel.setForeground(Color.darkGray);
        typeLabel.setForeground(Color.darkGray);

        ppLabel.setBounds(15, 10, 60, 60);
        ppValueLabel.setBounds(150, 10, 130, 60);
        typeLabel.setBounds(15, 60, 200, 60);

        textPanel.add(ppLabel);
        textPanel.add(ppValueLabel);
        textPanel.add(typeLabel);

        // Obtener movimientos del Pokémon actual
        int[] moves = {1,2,3,4};//game.getPlayerCurrentPokemonMoves();
        String[] moveNames = new String[moves.length];
        String[] moveTypes = new String[moves.length];
        String[] movePPs = new String[moves.length];

        for (int i = 0; i < moves.length; i++) {
            moveNames[i] = "Mname";//game.getMoveName(moves[i]);
            moveTypes[i] =  "type";//game.getMoveType(moves[i]);
            movePPs[i] = "pp";//game.getMoveCurrentPP(moves[i]) + "/" + game.getMoveMaxPP(moves[i]);
        }

        for (int i = 0; i < moves.length; i++) {
            final int index = i;
            JButton btn = new JButton(moveNames[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.DARK_GRAY);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    ppValueLabel.setText(movePPs[index]);
                    typeLabel.setText("TIPO: " + moveTypes[index]);
                    btn.setBackground(new Color(211, 211, 211));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 3));
                }

                public void mouseExited(MouseEvent evt) {
                    btn.setBackground(Color.WHITE);
                    btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                }
            });

            btn.addActionListener(e -> {
                /**
                boolean battleEnded = game.executePlayerAttack(index);
                if (battleEnded) {
                    if (battleListener != null) {
                        battleListener.onBattleEnd(game.playerWon());
                    }
                } else {
                    showAttackAnimation(moveNames[index]);
                }*/
            });

            buttonPanel.add(btn);
        }

        JLabel enemyNameLabel = new JLabel("name");//getEnemyCurrentPokemonName()
        JLabel enemyLevelLabel = new JLabel("Nv. " + 2);//getEnemyCurrentPokemonLevel()
        JLabel playerNameLabel = new JLabel("name2");
        JLabel playerLevelLabel = new JLabel("Nv. " + 4);

        JProgressBar enemyHPBar = new JProgressBar(0,30 );//game.getEnemyCurrentPokemonMaxHP()
        enemyHPBar.setValue(20);//game.getEnemyCurrentPokemonHP()
        enemyHPBar.setForeground(Color.GREEN);
        enemyHPBar.setBackground(Color.WHITE);
        enemyHPBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JProgressBar playerHPBar = new JProgressBar(0,22); //game.getPlayerCurrentPokemonMaxHP()
        playerHPBar.setValue(10);//game.getPlayerCurrentPokemonHP()
        playerHPBar.setForeground(Color.GREEN);
        playerHPBar.setBackground(Color.WHITE);
        playerHPBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        panel.add(enemyNameLabel);
        panel.add(enemyLevelLabel);
        panel.add(enemyHPBar);
        panel.add(playerNameLabel);
        panel.add(playerLevelLabel);
        panel.add(playerHPBar);

        JButton backButton = new JButton("VOLVER");
        backButton.setBounds(650, 520, 120, 40);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "battle"));
        panel.add(backButton);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                buttonContainer.setBounds((int)(w * 0.01), (int)(h * 0.68), (int)(w * 0.66), (int)(h * 0.25));
                textPanel.setBounds((int)(w * 0.69), (int)(h * 0.68), (int)(w * 0.29), (int)(h * 0.25));

                enemyNameLabel.setBounds((int)(w * 0.09), (int)(h * 0.1), (int)(w * 0.25), 30);
                enemyLevelLabel.setBounds((int)(w * 0.31), (int)(h * 0.1), (int)(w * 0.1), 30);
                enemyHPBar.setBounds((int)(w * 0.21), (int)(h * 0.17), (int)(w * 0.2), 15);

                playerNameLabel.setBounds((int)(w * 0.59), (int)(h * 0.48), (int)(w * 0.25), 30);
                playerLevelLabel.setBounds((int)(w * 0.80), (int)(h * 0.48), (int)(w * 0.15), 30);
                playerHPBar.setBounds((int)(w * 0.71), (int)(h * 0.56), (int)(w * 0.2), 15);

                backButton.setBounds((int)(w * 0.8), (int)(h * 0.85), (int)(w * 0.15), 40);
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

}