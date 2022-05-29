package oh_heaven.game;

import oh_heaven.game.player.*;
import oh_heaven.game.strategy.*;

public class PlayerFactory {

    public static Player getPlayer(String type, int idx){
        if(type.equals("human")){
            return new InteractivePlayer(idx);
        }else{
            AIPlayer newPlayer = new AIPlayer(idx);
            switch(type){
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
