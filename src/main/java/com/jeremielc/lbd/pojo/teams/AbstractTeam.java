package com.jeremielc.lbd.pojo.teams;

public abstract class AbstractTeam {
    protected String player;

    protected AbstractTeam(String player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return player;
    }

    public String getPlayer() {
        return this.player;
    }
}
