package oh_heaven.game.utility;

import oh_heaven.game.enums.Suit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesLoader {

    static Properties prop = new Properties();

    public static final String DEFAULT_DIRECTORY_PATH = "properties/";

    public static Properties loadPropertiesFile(String propertiesFile) {
        if (propertiesFile == null) {
            try (InputStream input = new FileInputStream(DEFAULT_DIRECTORY_PATH + "runmode.properties")) {

                // load a properties file
                prop.load(input);

                propertiesFile = DEFAULT_DIRECTORY_PATH + prop.getProperty("current_mode");
                System.out.println(propertiesFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try (InputStream input = new FileInputStream(propertiesFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<String> loadPlayers(Properties properties, int nbPlayers) {
        List<String> players = new ArrayList<>();


        for (int i = 0; i < nbPlayers; i++) {
            String playerType = properties.getProperty("players." + i);
            if (playerType == null) {
                playerType = "legal";
            }
            players.add(playerType);
        }

        return players;
    }

    // Load the nbStartCards from the properties file
    public static int loadNbStartCards(Properties properties) {
        int nbStartCards = 0;
        try {
            nbStartCards = Integer.parseInt(properties.getProperty("nbStartCards"));
        } catch (NumberFormatException e) {
            System.out.println("Error: nbStartCards is not a number");
        }
        return nbStartCards;
    }

    // Load the rounds from the properties file
    public static int loadRounds(Properties properties) {
        int rounds = 0;
        try {
            rounds = Integer.parseInt(properties.getProperty("rounds"));
        } catch (NumberFormatException e) {
            System.out.println("Error: rounds is not a number");
        }
        return rounds;
    }

    //Load the enforceRules from the properties file
    public static boolean loadEnforceRules(Properties properties) {
        boolean enforceRules = false;
        try {
            enforceRules = Boolean.parseBoolean(properties.getProperty("enforceRules"));
        } catch (NumberFormatException e) {
            System.out.println("Error: enforceRules is not a boolean");
        }
        return enforceRules;
    }

    //Load the trumps from the properties file
    public static List<Suit> loadTrumps(Properties properties) {
        List<Suit> trumps = new ArrayList<>();
        if (properties.getProperty("trumps") != null) {
            String trumpsString = properties.getProperty("trumps");
            String[] trumpStrings = trumpsString.split(",");
            for (String trumpString : trumpStrings) {
                trumps.add(Suit.valueOf(trumpString));
            }
        }
        return trumps;
    }

    //load the firstPlayer from the properties file
    public static int loadFirstPlayer(Properties properties) {
        int firstPlayer = 0;
        try {
            firstPlayer = Integer.parseInt(properties.getProperty("firstPlayer"));
        } catch (NumberFormatException e) {
            System.out.println("Error: firstPlayer is not a number");
        }
        return firstPlayer;
    }

    //load the nbRounds from the properties file
    public static int loadNbRounds(Properties properties) {
        int nbRounds = 0;
        try {
            nbRounds = Integer.parseInt(properties.getProperty("nbRounds"));
        } catch (NumberFormatException e) {
            System.out.println("Error: nbRounds is not a number");
        }
        return nbRounds;
    }

    // load the seed from the properties file
    public static int loadSeed(Properties properties) {
        int seed = 0;
        try {
            seed = Integer.parseInt(properties.getProperty("seed"));
        } catch (NumberFormatException e) {
            System.out.println("Error: seed is not a number");
        }
        return seed;
    }

}

