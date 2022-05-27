package oh_heaven.game.utility;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

public class ServiceRandom {
    private static Random random;

    private ServiceRandom() {}

    // class requires explicit initialisation and assumes usage of random is serialised
    public static void initServicesRandom(Long seed) {
        if (random == null) {
            if (seed == null) {
                random = new Random();
                System.out.println("Seed = null");
            } else { // Use specified seed
                random = new Random(seed);
                System.out.println("Seed = " + seed);
            }
        }
    }

    public static Random get() {
        return random;
    }

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list){
        int x = random.nextInt(list.size());
        return list.get(x);
    }
}
