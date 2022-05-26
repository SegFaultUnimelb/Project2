package oh_heaven.game;

import java.util.HashSet;
import java.util.Set;

public class Scoreboard{
    public final int madeBidBonus = 10;

    private int[] scores;
    private int[] tricks;
    private int[] bids;
    private int nb;
    private Oh_Heaven game;

    public Scoreboard(int nbPlayers,Oh_Heaven game) {
        this.nb= nbPlayers;
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

    public void trickUpdate(int player) {
        tricks[player]++;
        game.update(player,toText(player));
    }

    public void trickInitial(int player) {
        tricks[player] = 0;
        game.update(player,toText(player));
    }

    public void bidUpdate(int player, int bid) {
        bids[player] = bid;
        game.update(player,toText(player));
    }

    public int getBid(int player) {
        return bids[player];
    }

    public void scoreUpdate() {
        for (int i = 0; i < nb; i++) {
            scores[i] += tricks[i];
            if (tricks[i] == bids[i]) scores[i] += madeBidBonus;
            game.update(i,toText(i));
        }
    }

    public String toText(int player) {
        String text = "[" + String.valueOf(scores[player]) + "]" + String.valueOf(tricks[player]) + "/" + String.valueOf(bids[player]);
        return text;
    }

    public Set <Integer> getWinners(){
        int maxScore = 0;
        Set<Integer> winners = new HashSet<Integer>();
        for (int i = 0; i <nb; i++) if (scores[i] > maxScore) maxScore = scores[i];
        for (int i = 0; i <nb; i++) if (scores[i] == maxScore) winners.add(i);
        return winners;
    }


}
