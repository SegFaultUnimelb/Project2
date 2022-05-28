package oh_heaven.game.player;

import ch.aplu.jcardgame.Card;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.Round;
import oh_heaven.game.strategy.*;

/* Act as the context object in a strategy pattern structure
 */

public class AIPlayer extends Player {

    private IPlayStrategy playStrategy;
    private final int thinkingTime = 2000;

    //Constructor
    public AIPlayer(int index) {
        super(index);

    }

    public void setPlayStrategy(IPlayStrategy strategy) {
        this.playStrategy = strategy;
    }

    @Override
    public Card playCard(Round round) {
        Oh_Heaven.callDelay(thinkingTime);
        return playStrategy.nextPlay(this, round);
    }
}
