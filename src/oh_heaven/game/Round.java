package oh_heaven.game;


import ch.aplu.jcardgame.Card;
import oh_heaven.game.Oh_Heaven.Suit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * records all information in a game round
 * for an AI to make decisions
 */
public class Round {

    private Suit trump;

    private int winner;
    private Card winningCard;
    private Suit lead;

    private HashMap<Integer, HashSet<Card>> cardsPlayed;
    private ArrayList<Integer> scores;

    public Round(Suit trump){

        this.trump = trump;
        this.cardsPlayed = new HashMap<>();
        this.scores =  new ArrayList<>();
    }




    public Suit getTrump() {
        return trump;
    }

    public void setTrump(Suit trump) {
        this.trump = trump;
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

    public HashMap<Integer, HashSet<Card>> getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(HashMap<Integer, HashSet<Card>> cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    public void cardPlayed(int player, Card playedCard){
        if (cardsPlayed.containsKey((player))) {
            cardsPlayed.get(player).add(playedCard);
        } else{
            HashSet<Card> card = new HashSet<>();
            card.add(playedCard);
            cardsPlayed.put(player, card);
        }
    }
}
