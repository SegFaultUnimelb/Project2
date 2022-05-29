package oh_heaven.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Round;

public abstract class Player {

    // visibility, no need to getter and setter, infomation expert
    protected int index;
    protected Hand hand;

    public Player(int index) {
        this.index = index;
    }

    public abstract Card playCard(Round round);

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }
}
