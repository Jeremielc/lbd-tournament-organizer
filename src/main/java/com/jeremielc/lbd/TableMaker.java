package com.jeremielc.lbd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.List;

import com.jeremielc.lbd.pojo.match.AbstractMatch;
import com.jeremielc.lbd.pojo.teams.AbstractTeam;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;
import com.jeremielc.lbd.pojo.teams.SinglePlayerTeam;

public class TableMaker {
    public static final String VERTICAL_SEPARATOR = "|";
    public static final String HORIZONTAL_SEPARATOR = "-";
    private static int maxLength = -1;

    public static String[][] generateVersusTable(List<String> firstPlayers, List<String> secondPlayers, List<AbstractMatch> matchList) {
        String[][] versusTable = null;

        for (AbstractMatch match : matchList) {
            AbstractTeam aTeam = match.getTeamA();
            AbstractTeam bTeam = match.getTeamB();

            int xCoordinate = -1, yCoordinate = -1;

            if (aTeam instanceof DoublePlayerTeam) {
                String aTeamFirst = ((DoublePlayerTeam) aTeam).getFirstPlayer();
                String aTeamSecond = ((DoublePlayerTeam) aTeam).getSecondPlayer();

                xCoordinate = getIndexOf(aTeamFirst, firstPlayers);
                yCoordinate = getIndexOf(aTeamSecond, secondPlayers);
            } else if (aTeam instanceof SinglePlayerTeam) {
                String aTeamPlayer = ((SinglePlayerTeam) aTeam).getPlayer();
                String bTeamPlayer = ((SinglePlayerTeam) bTeam).getPlayer();

                xCoordinate = getIndexOf(aTeamPlayer, firstPlayers);
                yCoordinate = getIndexOf(bTeamPlayer, firstPlayers);
            }

            if (xCoordinate == -1) {
                throw new InvalidParameterException("It looks like the match list does not correspond to the provided firstPlayers list.");
            }

            if (yCoordinate == -1) {
                throw new InvalidParameterException("It looks like the match list does not correspond to the provided secondPlayers list.");
            }

            if (aTeam instanceof DoublePlayerTeam) {
                if (versusTable == null) {
                    versusTable = new String[secondPlayers.size()][firstPlayers.size()];
                }

                versusTable[yCoordinate][xCoordinate] = bTeam.toString();
            } else if (aTeam instanceof SinglePlayerTeam) {
                if (versusTable == null) {
                    versusTable = new String[firstPlayers.size()][firstPlayers.size()];
                    initVersusTable(versusTable);
                }

                
                int oldValue = Integer.parseInt(versusTable[yCoordinate][xCoordinate]);
                versusTable[yCoordinate][xCoordinate] = String.valueOf(oldValue + 1);
            }
        }

        return versusTable;
    }

    public static void displayVersusTable(List<String> firstPlayers, List<String> secondPlayers, String[][] versusTable, boolean withLineSeparator) {
        if (maxLength == -1) {
            // Compute maximum width of a cell
            for (String[] row : versusTable) {
                for (String rowValue : row) {
                    maxLength = rowValue.length() > maxLength ? rowValue.length() : maxLength;
                }
            }

            //Surround each value with a space char.
            maxLength += 2;
        }
        
        // Horizontal separator
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < firstPlayers.size() + 1; i++) {
            for (int l = 0; l < maxLength; l++) {
                separator.append('-');
            }
            
            separator.append(VERTICAL_SEPARATOR);
        }

        String lineSeparator = separator.toString();
        separator = null;

        // Header
        StringBuilder header = new StringBuilder();
        header.append(Stringutils.center("", maxLength)).append(VERTICAL_SEPARATOR);

        for (int i = 0; i < firstPlayers.size(); i++) {
            header.append(Stringutils.center(firstPlayers.get(i), maxLength));
            header.append(VERTICAL_SEPARATOR);
        }

        System.out.println(header.toString());
        System.out.println(lineSeparator);

        // Table content
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < secondPlayers.size(); i++) {
            content.append(Stringutils.center(secondPlayers.get(i), maxLength)).append(VERTICAL_SEPARATOR);

            for (String cellContent : versusTable[i]) {
                content.append(Stringutils.center(cellContent, maxLength)).append(VERTICAL_SEPARATOR);
            }

            content.append("\n");

            if (withLineSeparator) {
                content.append(lineSeparator).append("\n");
            }
        }

        System.out.println(content.toString());
    }

    public static void exportVersusTableToCsv(List<String> firstPlayers, List<String> secondPlayers, String[][] versusTable) {
        char separator = ';';

        // Header
        StringBuilder header = new StringBuilder();
        header.append(separator);

        for (int i = 0; i < firstPlayers.size(); i++) {
            header.append(firstPlayers.get(i)).append(separator);
        }
        
        header.append("\n");

        // Table content
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < secondPlayers.size(); i++) {
            content.append(secondPlayers.get(i)).append(separator);

            for (String cellContent : versusTable[i]) {
                content.append(cellContent).append(separator);
            }

            content.append("\n");
        }

        String fileName = String.format(
            "%04d-%02d-%02d-%02d-%02d-%02d - Matches.csv",
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            Calendar.getInstance().get(Calendar.SECOND)
        );

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            osw.write(header.toString());
            osw.write(content.toString());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private static int getIndexOf(String value, List<String> inList) {
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).equals(value)) {
                return i;
            }
        }

        return -1;
    }

    private static void initVersusTable(String[][] versusTable) {
        for (int y = 0; y < versusTable.length; y++) {
            for (int x = 0; x < versusTable[y].length; x++) {
                versusTable[y][x] = "0";
            }
        }
    }
}
