package com.jeremielc.lbd;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jeremielc.lbd.exceptions.InvalidPlayerListException;
import com.jeremielc.lbd.pojo.matches.AbstractMatch;
import com.jeremielc.lbd.pojo.teams.AbstractTeam;

public class Main {
    private static final Set<Candidate> candidates = new HashSet<>();

    public static void main(String[] args) {
    String[] men = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"/*, "K", "L", "M", "N", "O", "P"*/};
String[] women = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"/*, "10", "11", "12", "13", "14", "15"*/};
        List<String> menList = Arrays.asList(men);
        List<String> womenList = Arrays.asList(women);

        candidates.clear();

        CountDownLatch latch = new CountDownLatch(1000000);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(availableProcessors > 4 ? availableProcessors : 4);
        Instant start, stop;
        long timeElapsed = 0;

        // ---------------- Double Player Teams ----------------
        start = Instant.now();

        for (int i = 0; i < latch.getCount(); i++) {
            tpe.submit(() -> {
                try {
                    List<AbstractTeam> combinations = Combinator.generateMixedPairCombinations(menList, womenList);
                    List<AbstractMatch> matches = RandomMatchMaker.generateRandomDoubleMatches(combinations);
                    String[][] versusTable = TableMaker.generateVersusTable(menList, womenList, matches);
                    int score = ScoreComputer.computeMatchmakingScore(versusTable, menList, womenList);
    
                    addCandidate(score, versusTable);
                    
                    if (score == 0) {
                        TableMaker.displayVersusTable(menList, womenList, versusTable, false);
                    }
                } catch (InvalidPlayerListException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                    return;
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
        }

        Candidate bestCandidate = candidates.stream().findFirst().orElse(new Candidate(Integer.MIN_VALUE, null));

        System.out.println("Best candidate of " + candidates.size() + " :");
        System.out.println(" - Score: " + bestCandidate.getScore());
        TableMaker.displayVersusTable(menList, womenList, bestCandidate.getVersusTable(), false);

        stop = Instant.now();
        timeElapsed = Duration.between(start, stop).toMillis();
        System.out.println("Elapsed time: " + timeElapsed + " ms");

        System.exit(0);

        // ---------------- Single Player Teams ----------------
        /*
        start = Instant.now();

        try {
            combinations = Combinator.generateSingleCombinations(menList);
        } catch (InvalidPlayerListException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            combinations = new ArrayList<>();
        }

        matches = RandomMatchMaker.generateSingleMatches(combinations);
        versusTable = TableMaker.generateVersusTable(menList, null, matches);

        TableMaker.displayVersusTable(menList, womenList, versusTable, false);
        ScoreComputer.computeMatchmakingScore(versusTable, menList, womenList);

        stop = Instant.now();
        timeElapsed = Duration.between(start, stop).toMillis();
        System.out.println("Elapsed time: " + timeElapsed + "ms");
        //*/
    }

    private static synchronized void addCandidate(int score, String[][] versusTable) {
        candidates.add(new Candidate(score, versusTable));
    }
}
