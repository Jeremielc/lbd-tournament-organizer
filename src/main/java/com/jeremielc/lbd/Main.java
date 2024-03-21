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

import com.jeremielc.lbd.exceptions.InvalidPlayerListException;
import com.jeremielc.lbd.pojo.TournamentConfig;
import com.jeremielc.lbd.pojo.match.AbstractMatch;
import com.jeremielc.lbd.tasks.OngoingDisplayTask;

public class Main {
    private static final List<TournamentConfig> candidates = new ArrayList<>();

    public static void main(String[] args) {
    String[] men = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"/*, "K", "L", "M", "N", "O", "P"*/};
String[] women = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"/*, "10", "11", "12", "13", "14", "15"*/};
        List<String> menList = Arrays.asList(men);
        List<String> womenList = Arrays.asList(women);

        candidates.clear();

        CountDownLatch latch = new CountDownLatch(1000000);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(availableProcessors > 16 ? availableProcessors : 16);
        Instant start, stop;
        long timeElapsed = 0;

        start = Instant.now();

        OngoingDisplayTask displayTask = new OngoingDisplayTask();
        displayTask.run();

        for (int i = 0; i < latch.getCount(); i++) {
            tpe.submit(() -> {
                try {
                    List<DoublePlayerTeam> combinations = Combinator.generateMixedPairCombinations(menList, womenList);
                    List<AbstractMatch> matchList = RandomMatchMaker.generateRandomDoubleMatchList(combinations);
                    String[][] versusTable = TableMaker.generateVersusTable(menList, womenList, matches);
                    int score = ScoreComputer.computeMatchmakingScore(versusTable, menList, womenList);
                    
                    addCandidate(new TournamentConfig(score, versusTable, matchList));
                } catch (InvalidPlayerListException | InvalidCombinationsSizeException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                    return;
                } finally {
                    latch.countDown();
                }
            });
        }
            
        try {
            latch.await(4, TimeUnit.SECONDS);
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
