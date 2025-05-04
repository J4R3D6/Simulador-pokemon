package domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MovesRepository {
    private static final String ATACKS_ARCHIVE = "resources/csv/movimientos.csv";
    private static TreeMap<Integer, String[]> movimientos = new TreeMap<>();

    public MovesRepository() {
        List<String> pokemonsIput = null;
        try {
            pokemonsIput = Files.readAllLines(Paths.get(ATACKS_ARCHIVE));
        } catch (IOException e) {
            Log.record(e);
        }

        for (int i = 1; i < pokemonsIput.size(); i++) {
            String[] valores = splitCSVLine(pokemonsIput.get(i));
            this.movimientos.put(Integer.parseInt(valores[0]), valores);
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

    // ✅ Esta función divide la línea CSV correctamente, incluso con comas dentro de comillas
    private static String[] splitCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim().replaceAll("^\"|\"$", ""));
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        // Agrega la última columna
        values.add(current.toString().trim().replaceAll("^\"|\"$", ""));

        return values.toArray(new String[0]);
    }
}
