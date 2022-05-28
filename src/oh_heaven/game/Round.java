package oh_heaven.game;


import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Oh_Heaven.Suit;
import oh_heaven.game.player.*;
import oh_heaven.game.utility.CardComparator;
import oh_heaven.game.utility.PropertiesLoader;

import java.util.*;

/**
 * records all information in a game round
 * for an AI to make decisions
 */
public class Round {

    /*
    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }
    
     */


    private Suit trumps;

    private int winner, currPlayer;
    private Card winningCard;
    private Card selected;
    private Suit lead;
    private Hand trick;
    private boolean enforceRules = false;
    private boolean leading;

    private List<Player> players = new ArrayList<>();
    private HashMap<Integer, HashSet<Card>> cardsPlayed;

    public Round(Properties properties, int nbPlayers){
        this.enforceRules = properties.getProperty("enforceRules") == null ? enforceRules :
                Boolean.parseBoolean(properties.getProperty("enforceRules"));
        setupPlayers(properties, players, nbPlayers);
    }

    public void init(Suit trump){
        this.trumps = trump;
        this.cardsPlayed = new HashMap<>();
    }

    private void setupPlayers(Properties properties, List<Player> players, int nbPlayers){
        int i = 0;
        PlayerFactory factory = new PlayerFactory();
        for (String type : PropertiesLoader.loadPlayers(properties, nbPlayers)){
            players.add(factory.getPlayer(type, i));
            i++;
        }
    }

    public void checkRuleViolation() {
        // Check: Following card must follow suit if possible
        if (!leading && selected.getSuit() != lead && players.get(currPlayer).getHand().getNumberOfCardsWithSuit(lead) > 0) {
            // Rule violation
            String violation = "Follow rule broken by player " + currPlayer + " attempting to play " + selected;
            System.out.println(violation);
            if (enforceRules) {
                try {
                    throw (new BrokeRuleException(violation));
                } catch (BrokeRuleException e) {
                    e.printStackTrace();
                    System.out.println("A cheating player spoiled the game!");
                    System.exit(0);
                }
            }
        }
        leading = false;
        // End Check
    }

    public void checkWinner() {
        System.out.println("winning: " + winningCard);
        System.out.println(" played: " + selected);
        Comparator cmp = new CardComparator();
        if ( // beat current winner with higher card
                (selected.getSuit() == winningCard.getSuit() && cmp.compare(selected, winningCard) > 0) ||
                        // trumped when non-trump was winning
                        (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
            System.out.println("NEW WINNER");
            winner = currPlayer;
            winningCard = selected;
        }
    }

    public void endTrick() {
        selected = null;
        lead = null;
        leading = true;
        winningCard = null;
    }

    public Card update(int nextPlayer) {
        selected = players.get(nextPlayer).playCard(this);
        currPlayer = nextPlayer;
        if(lead == null) {
            this.lead = (Suit) selected.getSuit();
            this.winner = nextPlayer;
            this.winningCard = selected;
        }
        checkRuleViolation(); // check if this card follows the rule
        checkWinner();  // check current winner of this trick
        return selected;
    }

    public boolean checkAuto(int nextPlayer) {
        return players.get(nextPlayer) instanceof AIPlayer;
    }

    public Suit getTrump() {
        return trumps;
    }

    public int getWinner() {
        return winner;
    }

    public Card getWinningCard() {
        return winningCard;
    }

    public Suit getLead() {
        return lead;
    }

    public List<Player> getPlayers() {return players;}

    public void setTrick(Hand trick) {
        this.trick = trick;
    }

    public HashMap<Integer, HashSet<Card>> getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(HashMap<Integer, HashSet<Card>> cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
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
