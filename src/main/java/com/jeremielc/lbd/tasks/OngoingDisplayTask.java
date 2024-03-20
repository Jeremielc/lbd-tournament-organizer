package com.jeremielc.lbd.tasks;

public class OngoingDisplayTask extends CustomTaskWrapper {
    final Runnable task = () -> {
        System.out.print("Computing");

        while (!shouldStop) {
            System.out.print(".");

            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }

        System.out.println(".");
        latch.countDown();
    };

    public OngoingDisplayTask() {
        super();
    }

    public void run() {
        setTask(task);
        super.run();
    }
}
