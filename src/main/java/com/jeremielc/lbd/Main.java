package com.jeremielc.lbd;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jeremielc.lbd.exceptions.InvalidPlayerListException;
import com.jeremielc.lbd.pojo.matches.AbstractMatch;
import com.jeremielc.lbd.pojo.teams.AbstractTeam;
import com.jeremielc.lbd.tasks.OngoingDisplayTask;

public class Main {
    private static final List<Candidate> candidates = new ArrayList<>();

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
                    List<AbstractTeam> combinations = Combinator.generateMixedPairCombinations(menList, womenList);
                    List<AbstractMatch> matches = RandomMatchMaker.generateRandomDoubleMatches(combinations);
                    String[][] versusTable = TableMaker.generateVersusTable(menList, womenList, matches);
                    int score = ScoreComputer.computeMatchmakingScore(versusTable, menList, womenList);
                    
                    addCandidate(new Candidate(score, versusTable));
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

        Candidate bestCandidate = findBestCandidate();

        System.out.println("Best candidate of " + candidates.size() + " :");
        System.out.println(" - Score: " + bestCandidate.getScore());
        TableMaker.displayVersusTable(menList, womenList, bestCandidate.getVersusTable(), false);

        stop = Instant.now();
        timeElapsed = Duration.between(start, stop).toMillis();
        System.out.println("Elapsed time: " + timeElapsed + " ms");

        System.exit(0);
    }

    private static synchronized void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    private static Candidate findBestCandidate() {
        Candidate bestCandidate = new Candidate(Integer.MAX_VALUE, null);
        
        for (Candidate c : candidates) {
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
