package oh_heaven.game.strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.Round;
import oh_heaven.game.player.AIPlayer;

public class RandomStrategy implements IPlayStrategy{

    @Override
    public Card nextPlay(AIPlayer player, Round round) {
        // randomly selected a card from the player's hand

        Hand hand = player.getHand();
        return Oh_Heaven.randomCard(hand);
    }
}
