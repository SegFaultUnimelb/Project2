package oh_heaven.game.utility;

import ch.aplu.jcardgame.Card;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    public int compare(Card card1, Card card2){
        return card2.getRankId() - card1.getRankId();
    }
}
