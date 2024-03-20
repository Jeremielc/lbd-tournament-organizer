package com.jeremielc.lbd.tasks;

import java.util.concurrent.CountDownLatch;

public abstract class CustomTaskWrapper {
    private Runnable task;
    protected boolean shouldStop;
    protected CountDownLatch latch;
    
    protected CustomTaskWrapper() {
        shouldStop = false;
        latch = new CountDownLatch(1);
    }

    protected void run() {
        if (task != null) {
            new Thread(task).start();
        }
    }

    public void halt() {
        shouldStop = true;

        try {
            latch.await();
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    protected void setTask(Runnable task) {
        this.task = task;
    }
}
