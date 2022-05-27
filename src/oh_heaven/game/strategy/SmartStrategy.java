package oh_heaven.game.strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand.SortType;
import oh_heaven.game.Round;
import oh_heaven.game.player.AIPlayer;
import oh_heaven.game.utility.ServiceRandom;

import java.util.ArrayList;

public class SmartStrategy implements IPlayStrategy{


    @Override
    public Card nextPlay(AIPlayer player, Round round) {
        // randomly choose one as the lead
        if(round.getLead() == null){
            return ServiceRandom.randomCard(player.getHand());
        }

        ArrayList<Card> sameSuitAsLead = player.getHand().getCardsWithSuit((round.getLead()));
        ArrayList<Card> sameSuitAsTrump = player.getHand().getCardsWithSuit((round.getTrump()));

        ArrayList<Card> leadSuitInTrick = round.getTrick().getCardsWithSuit((round.getLead()));
        ArrayList<Card> TrumpSuitInTrick = round.getTrick().getCardsWithSuit((round.getTrump()));

        // check whether there are card with lead suit
        if(sameSuitAsLead.size() > 0){
            // with lead suit
            // no one is larger than those already in trick >>> return the smallest one

            // has one that is larger >>> randomly choose one from the available ones
            return ServiceRandom.randomCard(sameSuitAsLead);
        }

        // without lead suit
        if(sameSuitAsTrump.size() > 0){
            // with trump suit
            // no one is larger than those already in trick  >>> return other suit

            // has one that is larger >>> randomly choose one from the available ones
            return ServiceRandom.randomCard(sameSuitAsLead);
        }
        else {
            // no trump suit or no lead suit in hand
            // choose the one with the least rank
            player.getHand().sort(SortType.RANKPRIORITY, false);
            return player.getHand().getFirst();
        }

    }
}
