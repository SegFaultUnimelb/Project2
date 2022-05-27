package oh_heaven.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.Round;

public class InteractivePlayer extends Player{

    private Card selected;

    public InteractivePlayer(int index) {
        super(index);
    }

    @Override
    public Card playCard(Round round) {
        selected = null;

        hand.setTouchEnabled(true);

        while(selected == null){
            Oh_Heaven.callDelay(100);
        }
        // delay

        hand.setTouchEnabled(false);
        return selected;
    }

    public void setupCardListener(){
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) { selected = card; hand.setTouchEnabled(false); }
        };
        hand.addCardListener(cardListener);
    }

    public void setHand(Hand hand) {
        super.setHand(hand);
        setupCardListener();
    }


}
