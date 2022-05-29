package oh_heaven.game.strategy;

import ch.aplu.jcardgame.Card;
import oh_heaven.game.Round;
import oh_heaven.game.player.AIPlayer;
import oh_heaven.game.utility.CardComparator;
import oh_heaven.game.utility.ServiceRandom;

import java.util.*;
import java.util.Comparator;

public class SmartStrategy implements IPlayStrategy {


    @Override
    public Card nextPlay(AIPlayer player, Round round) {
        // randomly choose one as the lead
        if (round.getLead() == null) {
            return ServiceRandom.randomCard(player.getHand());
        }

        Comparator<Card> cmp = new CardComparator();
        ArrayList<Card> sameSuitAsLead = player.getHand().getCardsWithSuit((round.getLead()));
        sameSuitAsLead.sort(cmp);
        ArrayList<Card> sameSuitAsTrump = player.getHand().getCardsWithSuit((round.getTrump()));
        sameSuitAsLead.sort(cmp);
        ArrayList<Card> allCards = new ArrayList<>(player.getHand().getCardList());
        allCards.sort(cmp);

        Card winningCard = round.getWinningCard();

        int size = sameSuitAsLead.size();
        // check whether there are card with lead suit
        // with lead suit cards
        if (size > 0) {
            // check current winning card
            if (winningCard.getSuit() == round.getLead()) {
                //current winning card is lead suit
                for (Card card : sameSuitAsLead) {
                    // check if any larger than the winning card
                    if (cmp.compare(card, winningCard) > 0) {
                        return card;
                    }
                }
                // no one is larger than the winning card
                // play the least value
                return sameSuitAsLead.get(0);
            } else {
                // current winning card is trump suit
                // the least value with lead
                return sameSuitAsLead.get(0);
            }
        }

        size = sameSuitAsTrump.size();
        // without lead suit
        if (size > 0) {
            // check current winning card
            if (winningCard.getSuit() == round.getLead()) {
                //current winning card is lead suit
                // any trump card will win
                return ServiceRandom.randomCard(sameSuitAsTrump);
            } else {
                //current winning card is lead suit
                for (Card card : sameSuitAsTrump) {
                    // play the one is larger than the winning card
                    if (cmp.compare(card, winningCard) > 0) {
                        return card;
                    }
                }
                // no one is larger than the winning card
                // play the least rank one in hand
                allCards.removeAll(sameSuitAsTrump);
                return allCards.get(0);
            }
        } else {
            // no trump suit or no lead suit in hand
            // choose the one with the least rank
            return allCards.get(0);
        }

    }
}
