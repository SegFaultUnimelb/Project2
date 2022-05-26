package oh_heaven.game;

import oh_heaven.game.player.*;
import oh_heaven.game.strategy.*;

public class PlayerFactory {

    public Player getPlayer(String character, int idx,Oh_Heaven game){
        if(character.equals("human")){
            return new InteractivePlayer(idx,game);
        }else{
            AIPlayer newPlayer = new AIPlayer(idx,game);
            switch(character){
                case "random":
                    newPlayer.setPlayStrategy(new RandomStrategy());
                    return newPlayer;
                case "legal":
                    newPlayer.setPlayStrategy(new LegalStrategy());
                    return newPlayer;
                case "smart":
                    newPlayer.setPlayStrategy(new SmartStrategy());
                    return newPlayer;
                default:
                    throw new IllegalArgumentException("Not an expected player type");
            }
        }
    }
}
