package oh_heaven.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import oh_heaven.game.player.*;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Oh_Heaven extends CardGame {

	public enum Suit
	{
		SPADES, HEARTS, DIAMONDS, CLUBS
	}

	public enum Rank
	{
		// Reverse order of rank importance (see rankGreater() below)
		// Order of cards is tied to card images
		ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
	}

	final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

	static public int seed = 30006;
	static final Random random = new Random(seed);

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

	private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
		Hand pack = deck.toHand(false);
		// pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
		for (int i = 0; i < nbCardsPerPlayer; i++) {
			for (int j=0; j < nbPlayers; j++) {
				if (pack.isEmpty()) return;
				Card dealt = randomCard(pack);
				// System.out.println("Cards = " + dealt);
				dealt.removeFromHand(false);
				hands[j].insert(dealt, false);
				// dealt.transfer(hands[j], true);
			}
		}
	}

	public boolean rankGreater(Card card1, Card card2) {
		return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
	}

	private final String version = "1.0";
	public final int nbPlayers = 4;
	public  int nbStartCards = 13;
	public  int nbRounds = 3;
	private final int handWidth = 400;
	private final int trickWidth = 40;
	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
	private final Location[] handLocations = {
			new Location(350, 625),
			new Location(75, 350),
			new Location(350, 75),
			new Location(625, 350)
	};
	private final Location[] scoreLocations = {
			new Location(575, 675),
			new Location(25, 575),
			new Location(575, 25),
			// new Location(650, 575)
			new Location(575, 575)
	};
	private Actor[] scoreActors = {null, null, null, null };

	private final Location trickLocation = new Location(350, 350);
	private final Location textLocation = new Location(350, 450);
	private final int thinkingTime = 2000;
	private Hand[] hands;
	private Location hideLocation = new Location(-500, - 500);
	private Location trumpsActorLocation = new Location(50, 50);
	private boolean enforceRules=false;

	public void setStatus(String string) { setStatusText(string); }

	private Scoreboard scoreboard = new Scoreboard(nbPlayers, this);
	private List<Player> players = new ArrayList<>();

	Font bigFont = new Font("Serif", Font.BOLD, 36);

	public void update(int player, String text) {
		if(scoreActors[player] != null){
			removeActor(scoreActors[player]);
		}
		scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}


	private void initBids(Suit trumps, int nextPlayer) {
		int total = 0;
		int value;
		for (int i = nextPlayer; i < nextPlayer + nbPlayers; i++) {
			int iP = i % nbPlayers;
			value = nbStartCards / 4 + random.nextInt(2);
			scoreboard.bidUpdate(iP, value);
			total += value;
		}
		if (total == nbStartCards) {  // Force last bid so not every bid possible
			int iP = (nextPlayer + nbPlayers) % nbPlayers;
			int prev = scoreboard.getBid(iP);
			if (prev == 0) {
				prev = 1;
			} else {
				prev += random.nextBoolean() ? -1 : 1;
			}
			scoreboard.bidUpdate(iP, prev);
		}
		// for (int i = 0; i < nbPlayers; i++) {
		// 	 bids[i] = nbStartCards / 4 + 1;
		//  }
	}

	private Card selected;

	private void initRound() {
		for (int j = 0; j < nbPlayers; j++) scoreboard.trickInitial(j);
		hands = new Hand[nbPlayers];
		for (int i = 0; i < nbPlayers; i++) {
			hands[i] = new Hand(deck);
			players.get(i).setHand(hands[i]);
		}
		dealingOut(hands, nbPlayers, nbStartCards);
		for (int i = 0; i < nbPlayers; i++) {
			hands[i].sort(Hand.SortType.SUITPRIORITY, true);
		}
		// Set up human player for interaction
		for (Player player : players){
			if (player instanceof InteractivePlayer){
				((InteractivePlayer) player).setupCardListener();
			}
		}

		// graphics
		RowLayout[] layouts = new RowLayout[nbPlayers];
		for (int i = 0; i < nbPlayers; i++) {
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(90 * i);
			// layouts[i].setStepDelay(10);
			hands[i].setView(this, layouts[i]);
			hands[i].setTargetArea(new TargetArea(trickLocation));
			hands[i].draw();
		}
		for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide the cards in a hand (make them face down)
			hands[i].setVerso(true);			// You do not need to use or change this code.
		// End graphics
	}

	private void playRound() {
		// Select and display trump suit
		final Suit trumps = randomEnum(Suit.class);
		final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
		// End trump suit
		Round round = new Round(trumps);

		Hand trick;
		int winner;
		Card winningCard;
		Suit lead;

		int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
		initBids(trumps, nextPlayer);
		// initScore();
		for (int i = 0; i < nbStartCards; i++) {
			trick = new Hand(deck);
			selected = null;
			// Lead with selected card
			if (players.get(nextPlayer) instanceof InteractivePlayer) {  // Select lead depending on player type
				setStatus("Player 0 double-click on card to lead.");
			} else {
				setStatusText("Player " + nextPlayer + " thinking...");
			}
			selected = players.get(nextPlayer).playCard(round);

			trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
			trick.draw();
			selected.setVerso(false);
			// No restrictions on the card being lead
			lead = (Suit) selected.getSuit();
			selected.transfer(trick, true); // transfer to trick (includes graphic effect)
			winner = nextPlayer;
			winningCard = selected;
			// End Lead

			// update round information
			round.cardPlayed(nextPlayer, selected);
			round.setLead(lead);
			round.setTrick(trick);
			round.setWinner((winner));
			round.setWinningCard(winningCard);
			// End Lead

			for (int j = 1; j < nbPlayers; j++) {
				if (++nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
				selected = null;

				if (players.get(nextPlayer) instanceof InteractivePlayer) {  // Select lead depending on player type
					setStatus("Player 0 double-click on card to lead.");
				} else {
					setStatusText("Player " + nextPlayer + " thinking...");
				}
				selected = players.get(nextPlayer).playCard(round);

				// Follow with selected card
				trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
				trick.draw();
				selected.setVerso(false);  // In case it is upside down
				// Check: Following card must follow suit if possible
				if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
					// Rule violation
					String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
					System.out.println(violation);
					if (enforceRules) {
						try {
							throw (new BrokeRuleException(violation));
						} catch (BrokeRuleException e) {
							e.printStackTrace();
							System.out.println("A cheating player spoiled the game!");
							System.exit(0);
						}
					}
				}
				// End Check
				selected.transfer(trick, true); // transfer to trick (includes graphic effect)
				System.out.println("winning: " + winningCard);
				System.out.println(" played: " + selected);
				// System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + (13 - winningCard.getRankId()));
				// System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " + (13 -    selected.getRankId()));
				if ( // beat current winner with higher card
						(selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
								// trumped when non-trump was winning
								(selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
					System.out.println("NEW WINNER");
					winner = nextPlayer;
					winningCard = selected;
				}
				round.cardPlayed(nextPlayer, selected);
				round.setTrick(trick);
				round.setWinner((winner));
				round.setWinningCard(winningCard);
				// End Follow
			}
			delay(600);
			trick.removeAll(true);
			//trick.setView(this, new RowLayout(hideLocation, 0));
			//trick.draw();
			nextPlayer = winner;
			setStatusText("Player " + nextPlayer + " wins trick.");
			scoreboard.trickUpdate(nextPlayer);
		}
		removeActor(trumpsActor);
	}

	//
	private void setupPlayers(Properties properties){
		int i = 0;
		PlayerFactory factory = new PlayerFactory();
		for (String type : PropertiesLoader.loadPlayers(properties, nbPlayers)){
			players.add(factory.getPlayer(type, i));
			i++;
		}
	}

	public Oh_Heaven(Properties properties)
	{
		super(700, 700, 30);
		setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");

		parseProperties(properties);
		setupPlayers(properties);

		for (int i=0; i <nbRounds; i++) {
			initRound();
			playRound();
			scoreboard.scoreUpdate();
		};
		Set <Integer> winners = scoreboard.getWinners();    String winText;
		if (winners.size() == 1) {
			winText = "Game over. Winner is player: " +
					winners.iterator().next();
		}
		else {
			winText = "Game Over. Drawn winners are players: " +
					String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toSet()));
		}
		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText(winText);
		refresh();
	}

	public static void callDelay(long time) {
		delay(time);
	}

	private void parseProperties(Properties properties){

		this.nbStartCards = properties.getProperty("nbStartCards") == null ? nbStartCards :
				Integer.parseInt(properties.getProperty("nbStartCards"));

		this.enforceRules = properties.getProperty("enforceRules") == null ? enforceRules :
				Boolean.parseBoolean(properties.getProperty("enforceRules"));

		this.nbRounds = properties.getProperty("rounds") == null ? nbRounds :
				Integer.parseInt(properties.getProperty("rounds"));

		this.seed = properties.getProperty("seed") == null ? seed :
				Integer.parseInt(properties.getProperty("seed"));

		/*
		this.nbPlayers = properties.getProperty("nbPlayers") == null ? nbStartCards :
				Integer.parseInt(properties.getProperty("nbPlayers"));

		 */

	}

	public static void main(String[] args)
	{
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		final Properties properties;
		if (args == null || args.length == 0) {
			properties = PropertiesLoader.loadPropertiesFile(null);
		} else {
			properties = PropertiesLoader.loadPropertiesFile(args[0]);
		}
		new Oh_Heaven(properties);
	}

}
