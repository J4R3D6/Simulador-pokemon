package domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MovesRepository {
    private static final String ATTACKS_ARCHIVE = "resources/csv/movimientos.csv";
    private static TreeMap<Integer, String[]> movimientos = new TreeMap<>();

    public MovesRepository(){
        List<String> pokemonsIput = null;
        try {
            pokemonsIput = Files.readAllLines(Paths.get(ATTACKS_ARCHIVE));
        } catch (IOException e) {
            Log.record(e);
        }

        for (int i = 1; i < pokemonsIput.size(); i++) {
            //ID_0,"Name"_1,"Type1"_3,"Type2"_4,"HP"_6,"Attack"_7,"Defense"_8,"Sp. Atk"_9,"Sp. Def"_10,"Speed"_11,
            String[] valores = pokemonsIput.get(i).split(",");
            this.movimientos.put(Integer.parseInt(valores[0]),valores);
        }
    }

    public ArrayList<String[]> getMoves() {
        ArrayList<String[]> moves = new ArrayList<>();
        for (String[] s : this.movimientos.values()) {
            moves.add(s);
        }
        return moves;
    }

    public String[] getAttacksId(int id) {
        if (movimientos.containsKey(id)) {
            return movimientos.get(id);
        } else {
            return null;
        }
    }

    public String getAttackId(int id) {
        if (movimientos.containsKey(id)) {
            String[] attack = getAttacksId(id);
            if (attack[0].length() == 1) {
                attack[0] = "00" + attack[0];
            } else if (attack[0].length() == 2) {
                attack[0] = "0" + attack[0];
            }
            return attack[0] + " " + attack[1] + " - " + attack[3] + " - " + attack[4];
        } else {
            return null;
        }
    }
    public String[] getAttackDamageAndType(int id) {
         if (!movimientos.containsKey(id)) return null;
         String[] ataque = movimientos.get(id);
         String tipo = ataque[4];
         String poder = ataque[7];
         if (poder == null || poder.isEmpty()) {
             poder = "0";
         }
         String[] info = {tipo,poder};
         return info;
     }
}
