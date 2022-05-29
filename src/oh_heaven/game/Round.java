package oh_heaven.game;


import ch.aplu.jcardgame.Card;
import oh_heaven.game.Oh_Heaven.Suit;
import oh_heaven.game.player.*;
import oh_heaven.game.utility.CardComparator;
import oh_heaven.game.utility.PropertiesLoader;
import oh_heaven.game.utility.ServiceRandom;

import java.util.*;

/**
 * records all information in a game round
 * for an AI to make decisions
 */
public class Round {

    private Suit trump;

    private int winner;
    private int currPlayer;
    private Card winningCard;
    private Card selected;
    private Suit lead;
    private boolean enforceRules = false;
    private boolean leading;    // check whether this turn is to select a lead suit
    private final Properties properties;

    private List<Suit> trumps = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private Map<Integer, HashSet<Card>> cardsPlayed;

    public Round(Properties properties, int nbPlayers) {
        this.properties = properties;
        this.enforceRules = properties.getProperty("enforceRules") == null ? enforceRules :
                Boolean.parseBoolean(properties.getProperty("enforceRules"));
        setupPlayers(nbPlayers);
        setupTrumps();
    }

    // initiate a round
    public Suit init() {
        if (trumps == null) {
            this.trump = ServiceRandom.randomEnum(Oh_Heaven.Suit.class);
        } else {
            this.trump = trumps.get(0);
            trumps.remove(0);
        }
        this.cardsPlayed = new HashMap<>();
        return trump;
    }

    // create
    private void setupPlayers(int nbPlayers) {
        int i = 0;
        PlayerFactory factory = new PlayerFactory();
        for (String type : PropertiesLoader.loadPlayers(properties, nbPlayers)) {
            players.add(factory.getPlayer(type, i));
            i++;
        }
    }

    //create trump list for each round
    void setupTrumps() {
        if (properties.getProperty("trumps") != null) {
            String trumpsString = properties.getProperty("trumps");
            String[] trumpStrings = trumpsString.split(",");
            for (String trumpString : trumpStrings) {
                trumps.add(Suit.valueOf(trumpString));
            }
        } else {
            trumps = null;
        }
    }

    public Card update(int nextPlayer) {
        selected = players.get(nextPlayer).playCard(this);
        currPlayer = nextPlayer;
        if (lead == null) {
            this.lead = (Suit) selected.getSuit();
            this.winner = nextPlayer;
            this.winningCard = selected;
        }
        cardPlayed();
        checkRuleViolation(); // check if this card follows the rule
        checkWinner();  // check current winner of this trick
        return selected;
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
                        (selected.getSuit() == trump && winningCard.getSuit() != trump)) {
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

    public boolean checkAuto(int nextPlayer) {
        return players.get(nextPlayer) instanceof AIPlayer;
    }

    public Suit getTrump() {
        return trump;
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

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Integer, HashSet<Card>> getCardsPlayed() {
        return cardsPlayed;
    }

    public void cardPlayed() {
        if (cardsPlayed.containsKey((currPlayer))) {
            cardsPlayed.get(currPlayer).add(selected);
        } else {
            HashSet<Card> card = new HashSet<>();
            card.add(selected);
            cardsPlayed.put(currPlayer, card);
        }
    }
}
