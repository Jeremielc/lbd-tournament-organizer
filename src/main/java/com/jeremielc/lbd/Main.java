package com.jeremielc.lbd;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jeremielc.lbd.exceptions.InvalidCombinationsSizeException;
import com.jeremielc.lbd.exceptions.InvalidPlayerListException;
import com.jeremielc.lbd.pojo.MatchSet;
import com.jeremielc.lbd.pojo.TournamentConfig;
import com.jeremielc.lbd.pojo.match.AbstractMatch;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;
import com.jeremielc.lbd.tasks.OngoingDisplayTask;

public class Main {
    // private static final String[] men = {"A", "B", "C", "D"};
    // private static final String[] women = {"0", "1", "2", "3"};
    private static final String[] men = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final String[] women = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    // private static final String[] men = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"};
    // private static final String[] women = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
    // private static final String[] men = {"Alain", "Bart", "Chris", "David", "Eric", "Fred", "Gabin", "Hubert", "Ian", "Jean"};
    // private static final String[] women = {"Alice", "Betty", "Célia", "Deby", "Elsa", "Fanny", "Gaële", "Hanah", "Ilda", "Julia"};

    private static final List<String> menList = Arrays.asList(men);
    private static final List<String> womenList = Arrays.asList(women);

    private static final int threadCountlimit = 1000000;
    private static final CountDownLatch latch = new CountDownLatch(threadCountlimit);
    private static final List<TournamentConfig> candidates = new ArrayList<>();

    private static int cores = Runtime.getRuntime().availableProcessors() * 4;
    private static Instant start, stop;

    public static void main(String[] args) {
        cores = cores > 16 ? cores : 16;
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);
        System.out.printf(Locale.getDefault(), "Processing will use %,d cores\n", cores);

        start = Instant.now();

        OngoingDisplayTask displayTask = new OngoingDisplayTask();
        displayTask.run();

        for (int i = 0; i < threadCountlimit; i++) {
            tpe.submit(() -> {
                try {
                    List<DoublePlayerTeam> combinations = Combinator.generateMixedPairCombinations(menList, womenList);
                    List<AbstractMatch> matchList = RandomMatchMaker.generateRandomDoubleMatchList(combinations);
                    String[][] versusTable = TableMaker.generateVersusTable(menList, womenList, matchList);
                    int score = ScoreComputer.computeMatchmakingScore(versusTable, menList, womenList);
                    
                    addCandidate(new TournamentConfig(score, versusTable, matchList));
                } catch (InvalidPlayerListException | InvalidCombinationsSizeException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                } finally {
                    latch.countDown();
                }
            });
        }
            
        try {
            latch.await(5, TimeUnit.SECONDS);
            tpe.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        } finally {
            tpe.getQueue().clear();
            tpe.shutdownNow();
            displayTask.halt();
        }

        TournamentConfig bestConfig = findBestCandidate();
        stop = Instant.now();

        System.out.printf(Locale.getDefault(), "Best configuration of %,d:\n", candidates.size());
        System.out.printf(Locale.getDefault(), " - Score: %,d\n", bestConfig.getScore());

        TableMaker.displayVersusTable(menList, womenList, bestConfig.getVersusTable(), false);

        System.out.printf(Locale.getDefault(), "Elapsed time: %,d ms\n", Duration.between(start, stop).toMillis());

        try {
            List<MatchSet> rounds = Planner.plan(4, bestConfig);

            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < rounds.size(); i++) {
                sb.append("#" + (i + 1) + ":\t");

                for (int j = 0; j < rounds.get(i).getMatchList().size(); j++) {
                    sb.append("Court #" + (j + 1) + ":\t");
                    sb.append(rounds.get(i).getMatchList().get(j));

                    if (j < rounds.get(i).getMatchList().size() - 1) {
                        sb.append("\t| ");
                    }
                }

                sb.append("\n");
            }

            System.out.println(sb.toString());
        } catch (InvalidPlayerListException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }

        System.exit(0);
    }

    private static synchronized void addCandidate(TournamentConfig config) {
        candidates.add(config);
    }

    private static TournamentConfig findBestCandidate() {
        TournamentConfig bestCandidate = new TournamentConfig(Integer.MAX_VALUE, null, null);
        
        for (TournamentConfig c : candidates) {
            if (c.getScore() == 0) {
                return c;
            } else {
                if (c.getScore() < bestCandidate.getScore()) {
                    bestCandidate = c;
                }
            }
        }

        return bestCandidate;
    }
}
