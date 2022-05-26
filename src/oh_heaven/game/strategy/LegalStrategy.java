package oh_heaven.game.strategy;

import ch.aplu.jcardgame.Card;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.Round;
import oh_heaven.game.player.AIPlayer;

import javax.print.attribute.standard.PrinterMakeAndModel;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LegalStrategy implements IPlayStrategy{

    public LegalStrategy() {
    }

    @Override
    public Card nextPlay(AIPlayer player, Round round) {
        // legal play
        if(round.getLead() ==null){
            return Oh_Heaven.randomCard(player.getHand());
        }
        ArrayList<Card> sameSuitAsLead = player.getHand().getCardsWithSuit((round.getLead()));

        if(sameSuitAsLead.size() > 0){
            return Oh_Heaven.randomCard(sameSuitAsLead);
        }
        else {
            return Oh_Heaven.randomCard(player.getHand());
        }
    }

}
