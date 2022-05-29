package oh_heaven.game;

import oh_heaven.game.utility.ServiceRandom;

import java.util.HashSet;
import java.util.Set;


public class Scoreboard {
    public static final int MADE_BID_BONUS = 10;

    private int[] scores;
    private int[] tricks;
    private int[] bids;
    private int nb;
    private Oh_Heaven game;

    private static Scoreboard instance = null;

    private Scoreboard(int nbPlayers, Oh_Heaven game) {
        this.nb = nbPlayers;
        this.game = game;
        this.scores = new int[nbPlayers];
        this.tricks = new int[nbPlayers];
        this.bids = new int[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            tricks[i] = 0;
            bids[i] = 0;
        }
    }

    public static Scoreboard getInstance(int nbPlayers, Oh_Heaven game) {
        if (instance == null) {
            instance = new Scoreboard(nbPlayers, game);
        }
        return instance;
    }


    public void trickUpdate(int player) {
        tricks[player]++;
        game.update(player, toText(player));
    }

    public void trickInitial(int player) {
        tricks[player] = 0;
        game.update(player, toText(player));
    }

    public void initBids(int nextPlayer, int nbStartCards) {
        int total = 0;
        for (int i = nextPlayer; i < nextPlayer + nb; i++) {
            int iP = i % nb;
            bids[iP] = nbStartCards / 4 + ServiceRandom.get().nextInt(2);
            total += bids[iP];
        }
        if (total == nbStartCards) {  // Force last bid so not every bid possible
            int iP = (nextPlayer + nb) % nb;
            if (bids[iP] == 0) {
                bids[iP] = 1;
            } else {
                bids[iP] += ServiceRandom.get().nextBoolean() ? -1 : 1;
            }
        }

        for (int i = 0; i < nb; i++) {
            game.update(i, toText(i));
        }
    }


    public void scoreUpdate() {
        for (int i = 0; i < nb; i++) {
            scores[i] += tricks[i];
            if (tricks[i] == bids[i]) scores[i] += MADE_BID_BONUS;
            game.update(i, toText(i));
        }
    }

    public String toText(int player) {
        String text = "[" + String.valueOf(scores[player]) + "]" + String.valueOf(tricks[player]) + "/" + String.valueOf(bids[player]);
        return text;
    }

    public Set<Integer> getWinners() {
        int maxScore = 0;
        Set<Integer> winners = new HashSet<Integer>();
        for (int i = 0; i < nb; i++) if (scores[i] > maxScore) maxScore = scores[i];
        for (int i = 0; i < nb; i++) if (scores[i] == maxScore) winners.add(i);
        return winners;
    }


}
