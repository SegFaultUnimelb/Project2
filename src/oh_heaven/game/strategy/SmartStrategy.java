package oh_heaven.game.strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand.SortType;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.Round;
import oh_heaven.game.player.AIPlayer;

import java.util.ArrayList;

public class SmartStrategy implements IPlayStrategy{


    @Override
    public Card nextPlay(AIPlayer player, Round round) {
        // legal play
        if(round.getLead() ==null){
            return Oh_Heaven.randomCard(player.getHand());
        }
        ArrayList<Card> sameSuitAsLead = player.getHand().getCardsWithSuit((round.getLead()));

        // have same suit as lead
        if(sameSuitAsLead.size() > 0){
            //
            return Oh_Heaven.randomCard(sameSuitAsLead);
        }
        else {
            return Oh_Heaven.randomCard(player.getHand());
        }

        // TODO: 如果肯定赢不了就出一张最小的，winning card比手牌都大，就出最小的
        // player.getHand().sort(SortType.RANKPRIORITY, false);
        // return player.getHand().getFirst();
        // TODO: 跟trump一样比较大的手牌，可以出
        // TODO: 当前这一轮最后一个人，在赢得基础上，出牌越小越好s
    }
}
