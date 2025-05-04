package domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class StatsRepository {
    private final Map<String, Map<String, Double>> typeChart = new HashMap<>();
    private static final String ROOT_STATS_LOCATION = "csv/MoveStatspok.csv";

    public StatsRepository() {
        try {
            loadTypeChart();
        } catch (Exception e) {
            Log.record(e);
        }
    }

    private void loadTypeChart() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(ROOT_STATS_LOCATION);

        if (is == null) {
            throw new IllegalArgumentException("No se pudo encontrar el archivo: " + ROOT_STATS_LOCATION);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String[] types = br.readLine().split(",");

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String defendingType = values[0];
                Map<String, Double> multipliers = new HashMap<>();

                for (int i = 1; i < values.length; i++) {
                    multipliers.put(types[i], Double.parseDouble(values[i]));
                }

                typeChart.put(defendingType, multipliers);
            }
        }
    }

    public double getMultiplier(String attackingType, String defendingType) {
        return typeChart.getOrDefault(defendingType, new HashMap<>())
                .getOrDefault(attackingType, 1.0);
    }
}
