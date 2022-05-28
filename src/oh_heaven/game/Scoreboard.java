package oh_heaven.game;

import ch.aplu.jcardgame.CardGame;
import ch.aplu.jgamegrid.*;

import oh_heaven.game.utility.ServiceRandom;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class Scoreboard extends CardGame {
    public final int madeBidBonus = 10;

    private int[] scores;
    private int[] tricks;
    private int[] bids;
    private int nb;
    private Oh_Heaven game;

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private Actor[] scoreActors = {null, null, null, null};
    Font bigFont = new Font("Serif", Font.BOLD, 36);

    public Scoreboard(int nbPlayers, Oh_Heaven game) {
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

    public void trickUpdate(int player) {
        tricks[player]++;
        game.update(player, toText(player));
        //update(player);
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
            if (tricks[i] == bids[i]) scores[i] += madeBidBonus;
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
/*
    public void update(int player) {
        if(scoreActors[player] != null){
            removeActor(scoreActors[player]);
        }

        String text = "[" + String.valueOf(scores[player]) + "]" + String.valueOf(tricks[player]) + "/" + String.valueOf(bids[player]);
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

 */


}
