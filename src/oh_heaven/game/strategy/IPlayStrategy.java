package oh_heaven.game.strategy;

import ch.aplu.jcardgame.Card;
import oh_heaven.game.Round;
import oh_heaven.game.player.AIPlayer;

public interface IPlayStrategy {

    Card nextPlay(AIPlayer player, Round round);

}
