package presentacion;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

import domain.Log;
import domain.POOBkemon;

public class PokemonBattlePanel extends JPanel {
    private static final String CHARACTER = "resources/personaje/";
    private static final String MENU = "resources/menu/";
    private static final String POKEMONES =  "resources/pokemones/Emerald/";
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private POOBkemon game;
    private BattleListener battleListener;

    public interface BattleListener {
        void onBattleEnd(boolean playerWon);
    }

    public PokemonBattlePanel(POOBkemon game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
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
        String playerPokemon = POKEMONES+"Back/"+4+ ".png";//game.getPlayerCurrentPokemonId()
        String enemyPokemon = POKEMONES+"Normal/" +1+ ".png";
        String currentPlayer = CHARACTER+0+ ".png";//game.getCurrentPlayer()
        Image bg = new ImageIcon(MENU+"battle0.png").getImage();
        Image playerImg = new ImageIcon(playerPokemon).getImage();
        Image enemyImg = new ImageIcon(enemyPokemon).getImage();
        Image currentPlayerImg = new ImageIcon(currentPlayer).getImage();
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                final int w = getWidth(), h = getHeight();
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, w, h, this);
                g.drawImage(enemyImg, (int)(w * 0.6), (int)(h * 0.07), (int)(w * 0.27), (int)(h * 0.4), this);
                g.drawImage(playerImg, (int)(w * 0.12), (int)(h * 0.465), (int)(w * 0.25), (int)(h * 0.3), this);
                g.drawImage(currentPlayerImg, (int)(w * 0.88), (int)(h * 0.01), (int)(w * 0.12), (int)(h * 0.15), this);
            }
        };
        JLabel battleText = new JLabel("¿Qué debería hacer " +" name" + "?");//game.getPlayerCurrentPokemonName()
        battleText.setFont(cargarFuentePixel(5));
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
            btn.setFont(cargarFuentePixel(16));
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

        JLabel enemyNameLabel = new JLabel("name");//getEnemyCurrentPokemonName()
        JLabel enemyLevelLabel = new JLabel("Nv. " + 5);//getEnemyCurrentPokemonLevel()
        JLabel enemyHPLabel = new JLabel("32/122");//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
        enemyHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel playerNameLabel = new JLabel("name2");
        JLabel playerLevelLabel = new JLabel("Nv. " + 6);
        JLabel playerHPLabel = new JLabel("32/122");
        playerHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        BarraVidaConImagen enemyHPBar = new BarraVidaConImagen(100);//getEnemyCurrentPokemonMaxHP()
        enemyHPBar.setValue(30);//getEnemyCurrentPokemonHP()

        BarraVidaConImagen playerHPBar = new BarraVidaConImagen(100);
        playerHPBar.setValue(10);

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

                battleText.setFont(cargarFuentePixel(20));
                battleText.setForeground(Color.WHITE);
                battleText.setBounds((int)(w * 0.038), (int)(h * 0.73), (int)(w * 0.48), (int)(h * 0.22));

                buttonContainer.setBounds((int)(w * 0.52), (int)(h * 0.715), (int)(w * 0.478), (int)(h * 0.265));

                enemyNameLabel.setBounds((int)(w * 0.09), (int)(h * 0.09), (int)(w * 0.25), 30);
                enemyNameLabel.setFont(cargarFuentePixel(18));
                enemyLevelLabel.setBounds((int)(w * 0.31), (int)(h * 0.09), (int)(w * 0.1), 30);
                enemyLevelLabel.setFont(cargarFuentePixel(18));
                enemyHPBar.setBounds((int)(w * 0.12), (int)(h * 0.16), (int)(w * 0.3), 15);
                enemyHPLabel.setBounds((int)(w * 0.12), (int)(h * 0.19), (int)(w * 0.3), 30);
                enemyHPLabel.setFont(cargarFuentePixel(18));

                playerNameLabel.setBounds((int)(w * 0.6), (int)(h * 0.475), (int)(w * 0.25), 30);
                playerNameLabel.setFont(cargarFuentePixel(18));
                playerLevelLabel.setBounds((int)(w * 0.83), (int)(h * 0.475), (int)(w * 0.15), 30);
                playerLevelLabel.setFont(cargarFuentePixel(18));
                playerHPBar.setBounds((int)(w * 0.63), (int)(h * 0.55), (int)(w * 0.3), 15);
                playerHPLabel.setBounds((int)(w * 0.63), (int)(h * 0.58), (int)(w * 0.3), 30);
                playerHPLabel.setFont(cargarFuentePixel(18));
            }
        });

        return panel;
    }

    private JPanel createPokemonView() {

        JPanel panel = new ImagePanel(null,MENU+"p.png");

        JPanel currentPokemonPanel = new JPanel(null);
        currentPokemonPanel.setOpaque(false);
        ImageIcon seledtedPOkemonIcon = scaleIcon(new ImageIcon("resources/pokemones/Emerald/Icon/" +1+ ".png"),100,100);//getPlayerCurrentPokemonId()
        JLabel selectedPokemonImage = new JLabel(seledtedPOkemonIcon);
        JLabel selectedNameLabel = new JLabel("actual");//getPlayerCurrentPokemonName()
        JLabel selectedLevel = new JLabel("Nv. " + 2);//getPlayerCurrentPokemonLevel()
        JLabel selectedHPLabel = new JLabel("32/122");//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
        selectedLevel.setFont(cargarFuentePixel(20));
        selectedNameLabel.setFont(cargarFuentePixel(20));
        selectedHPLabel.setFont(cargarFuentePixel(20));
        selectedLevel.setForeground(Color.white);
        selectedNameLabel.setForeground(Color.white);
        selectedHPLabel.setForeground(Color.white);
        selectedHPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        selectedLevel.setHorizontalAlignment(SwingConstants.LEFT);
        selectedNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        BarraVidaConImagen selectedHpBar = new BarraVidaConImagen( 30);//getPlayerCurrentPokemonMaxHP())
        selectedHpBar.setValue(2);//getPlayerCurrentPokemonHP() // game.getPlayerCurrentPokemonHP() <(game.getPlayerCurrentPokemonMaxHP()
        currentPokemonPanel.add(selectedPokemonImage);
        currentPokemonPanel.add(selectedNameLabel);
        currentPokemonPanel.add(selectedLevel);
        currentPokemonPanel.add(selectedHPLabel);
        currentPokemonPanel.add(selectedHpBar);
        currentPokemonPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                selectedPokemonImage.setBounds(3, -35,  (int)(panel.getWidth() * 0.1), (int)(panel.getHeight() * 0.25));
                selectedNameLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.3), (int)(currentPokemonPanel.getHeight() *0.20),  (int)(panel.getWidth() * 0.3), 20);
                selectedLevel.setBounds((int)(currentPokemonPanel.getWidth() *0.3), (int)(currentPokemonPanel.getHeight() *0.40),  (int)(panel.getWidth() * 0.3), 20);
                selectedHPLabel.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.76),  (int)(panel.getWidth() * 0.3), 20);
                selectedHpBar.setBounds((int)(currentPokemonPanel.getWidth() *0.04), (int)(currentPokemonPanel.getHeight() *0.63),  (int)(panel.getWidth() * 0.3), 15);
            }
        });
        int[] pokeTeam= {1,1,1,1,1}; //(equipo desacttivado)
        String[] pokemonNames = new String[pokeTeam.length];
        int[] pokemonLevels = new int[pokeTeam.length];
        int[] pokemonHPs = new int[pokeTeam.length];
        int[] pokemonMaxHPs = new int[pokeTeam.length];
        int[] pokemonIcons = new int[pokeTeam.length];
        ArrayList<JPanel> inactivePokemons = new ArrayList<>();
        for (int i = 0; i < pokeTeam.length; i++) {
            pokemonNames[i] = "na";//game.getPokemonName(team[i]);
            pokemonLevels[i] = 2;//game.getPokemonLevel(team[i]);
            pokemonHPs[i] = 10;//game.getPokemonHP(team[i]);
            pokemonMaxHPs[i] = 8;//game.getPokemonMaxHP(team[i]);
            pokemonIcons[i] = 1;
        }
        for(int i= 0 ;i < pokeTeam.length; i++  ){
            JPanel pokemonPanel = new JPanel(null);
            pokemonPanel.setFont(cargarFuentePixel(20));
            pokemonPanel.setForeground(Color.WHITE);
            pokemonPanel.setOpaque(false);
            ImageIcon POkemonIcon = scaleIcon(new ImageIcon("resources/pokemones/Emerald/Icon/" +pokemonIcons[i]+ ".png"),80,80);//getPlayerCurrentPokemonId()
            JLabel PokemonImage = new JLabel(POkemonIcon);
            JLabel NameLabel = new JLabel(pokemonNames[i]);//getPlayerCurrentPokemonName()
            JLabel Level = new JLabel("Nv. " + pokemonLevels[i]);//getPlayerCurrentPokemonLevel()
            JLabel HPLabel = new JLabel(pokemonHPs[i]+"/"+pokemonMaxHPs[i]);//getEnemyCurrentPokemonHP()/getEnemyCurrentPokemonMaxHP()
            Level.setFont(cargarFuentePixel(20));
            NameLabel.setFont(cargarFuentePixel(20));
            HPLabel.setFont(cargarFuentePixel(20));
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
            inactivePokemons.add(pokemonPanel);
        }

        panel.add(currentPokemonPanel);
        for (int i = 0; i < pokeTeam.length; i++) {
            panel.add(inactivePokemons.get(i));
        }
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int fontSize = Math.max(12, h / 24);

                currentPokemonPanel.setFont(cargarFuentePixel(20));
                currentPokemonPanel.setForeground(Color.WHITE);
                currentPokemonPanel.setBounds((int) (panel.getWidth() * 0.05), (int) (panel.getHeight() * 0.16), (int) (panel.getWidth() * 0.315), (int) (panel.getHeight() * 0.28));
                inactivePokemons.get(0).setBounds((int) (panel.getWidth() * 0.43), (int) (panel.getHeight() * 0.065), (int) (panel.getWidth() * 0.55), (int) (panel.getHeight() * 0.115));
                inactivePokemons.get(1).setBounds((int) (panel.getWidth() * 0.43), (int) (panel.getHeight() * 0.215), (int) (panel.getWidth() * 0.55), (int) (panel.getHeight() * 0.115));
                inactivePokemons.get(2).setBounds((int) (panel.getWidth() * 0.43), (int) (panel.getHeight() * 0.365), (int) (panel.getWidth() * 0.55), (int) (panel.getHeight() * 0.115));
                inactivePokemons.get(3).setBounds((int) (panel.getWidth() * 0.43), (int) (panel.getHeight() * 0.515), (int) (panel.getWidth() * 0.55), (int) (panel.getHeight() * 0.115));
                inactivePokemons.get(4).setBounds((int) (panel.getWidth() * 0.43), (int) (panel.getHeight() * 0.665), (int) (panel.getWidth() * 0.55), (int) (panel.getHeight() * 0.115));}
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
    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    private static Font cargarFuentePixel(int tamaño) {
        try {
            Font fuenteBase = Font.createFont(Font.TRUETYPE_FONT,
                    new File("resources/fonts/themevck-text.ttf"));
            Font fuenteNegrita = fuenteBase.deriveFont(Font.BOLD, tamaño);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fuenteNegrita);
            return fuenteNegrita;

        } catch (FontFormatException | IOException e) {
            System.out.println("Font no encontrado");
            Log.record(e);
            return new Font("Monospaced", Font.BOLD, tamaño);
        }
    }
}