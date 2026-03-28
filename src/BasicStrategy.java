import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class BasicStrategy {
    private final Table<String, String, Play> hardTotals = HashBasedTable.create();
    private final Table<String, String, Play> softTotals = HashBasedTable.create();
    private final Table<String, String, Play> pairSplitting = HashBasedTable.create();
    private final Table<String, String, Play> surrender = HashBasedTable.create();

    public Play getSoftTotalPlay(String dealerTotal, String playerTotal) {
        return this.softTotals.get(dealerTotal, playerTotal);
    }

    public Play getHardTotalPlay(String dealerTotal, String playerTotal) {
        return this.hardTotals.get(dealerTotal, playerTotal);
    }

    public Play getPairSplittingTotalPlay(String dealerTotal, String playerTotal) {
        return this.pairSplitting.get(dealerTotal, playerTotal);
    }

    public Play getSurrenderTotalPlay(String dealerTotal, String playerTotal) {
        return this.surrender.get(dealerTotal, playerTotal);
    }


    public BasicStrategy(String hardTotalsFile, String softTotalsFile, String pairSplittingFile, String surrenderFile) throws IOException {
        List<String> dealerTotals = List.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "11");

        List<String> playerHardTotals = List.of("17","16","15","14","13","12","11","10","9","8","7","6","5","4","3","2","1");
        List<String> playerSoftTotals = List.of("a9","a8","a7","a6","a5","a4","a3","a2");
        List<String> pairSplitting = List.of("a","10","9","8","7","6","5","4", "3", "2");
        List<String> surrender = List.of("16", "15", "14");

        parseStrategyFile(hardTotalsFile, dealerTotals, playerHardTotals, this.hardTotals);
        parseStrategyFile(softTotalsFile, dealerTotals, playerSoftTotals, this.softTotals);
        parseStrategyFile(pairSplittingFile, dealerTotals, pairSplitting, this.pairSplitting);
        parseStrategyFile(surrenderFile, dealerTotals, surrender, this.surrender);
    }

    private void parseStrategyFile(String filename, List<String> dealerTotals, List<String> playerTotals, Table<String, String, Play> strategyTable) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String playerTotal = playerTotals.get(lineNumber);

                for (int i = 0; i < values.length; i++) {
                    String dealerTotal = dealerTotals.get(i);
                    Play play = convertValueToPlay(values[i]);
                    strategyTable.put(dealerTotal, playerTotal, play);
                }

                lineNumber++;
            }
        }
    }

    private Play convertValueToPlay(String value) {
        return switch (value) {
            case "s" -> Play.STAND;
            case "h" -> Play.HIT;
            case "d" -> Play.DOUBLE_DOWN;
            case "dd" -> Play.DOUBLE_DOWN;
            case "n" -> Play.NOTHING;
            case "y" -> Play.SPLIT;
            case "yd" -> Play.SPLIT;
            case "sur" -> Play.SURRENDER;
            default -> Play.NOTHING;
        };
    }

}
