package oh_heaven.game;


import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Oh_Heaven.Suit;
import oh_heaven.game.player.Player;

import java.util.HashMap;
import java.util.HashSet;

/**
 * records all information in a game round
 * for an AI to make decisions
 */
public class Round {

    private Suit trumps;

    private int winner;
    private Card winningCard;
    private Suit lead;
    private Hand trick;


    private HashMap<Integer, HashSet<Card>> cardsPlayed;

    public Round(Suit trump) {
        this.trumps = trump;
        this.cardsPlayed = new HashMap<>();
    }


    public Suit getTrump() {
        return trumps;
    }

    public void setTrump(Suit trump) {
        this.trumps = trump;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public Card getWinningCard() {
        return winningCard;
    }

    public void setWinningCard(Card winningCard) {
        this.winningCard = winningCard;
    }

    public Suit getLead() {
        return lead;
    }

    public void setLead(Suit lead) {
        this.lead = lead;
    }

    public Hand getTrick() {
        return trick;
    }

    public void setTrick(Hand trick) {
        this.trick = trick;
    }

    public HashMap<Integer, HashSet<Card>> getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(HashMap<Integer, HashSet<Card>> cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
    }

    public void cardPlayed(int player, Card playedCard) {
        if (cardsPlayed.containsKey((player))) {
            cardsPlayed.get(player).add(playedCard);
        } else {
            HashSet<Card> card = new HashSet<>();
            card.add(playedCard);
            cardsPlayed.put(player, card);
        }
    }
}
