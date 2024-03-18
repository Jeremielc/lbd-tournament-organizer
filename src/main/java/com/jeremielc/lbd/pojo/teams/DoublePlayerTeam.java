package com.jeremielc.lbd.pojo.teams;

public class DoublePlayerTeam extends AbstractTeam {
    public static final String SEPARATOR = " & ";

    private final String secondPlayer;

    public DoublePlayerTeam(String firstPlayer, String secondPlayer) {
        super(firstPlayer);

        if (firstPlayer.equals(secondPlayer)) {
            throw new IllegalArgumentException("The same person cannot be twice in the same team.");
        }

        this.secondPlayer = secondPlayer;
    }

    @Override
    public String toString() {
        return super.player + DoublePlayerTeam.SEPARATOR + secondPlayer;
    }

    public String getFirstPlayer() {
        return this.player;
    }

    public String getSecondPlayer() {
        return this.secondPlayer;
    }
}
