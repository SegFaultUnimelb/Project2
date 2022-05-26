package oh_heaven.game.player;

import ch.aplu.jcardgame.Card;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.Round;
import oh_heaven.game.strategy.*;

/* Act as the context object in a strategy pattern structure
 */

public class AIPlayer extends Player{

    private IPlayStrategy playStrategy;

    //Constructor
    public AIPlayer(int index, Oh_Heaven game) {
        super(index,game);

    }

    public void setPlayStrategy(IPlayStrategy strategy){
        this.playStrategy = strategy;
    }

    @Override
    public Card playCard(Round round) {
        return playStrategy.nextPlay(this, round);
    }
}
