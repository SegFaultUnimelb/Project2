package oh_heaven.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import oh_heaven.game.utility.PropertiesLoader;
import oh_heaven.game.utility.ServiceRandom;

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


	private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
		Hand pack = deck.toHand(false);
		// pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
		for (int i = 0; i < nbCardsPerPlayer; i++) {
			for (int j=0; j < nbPlayers; j++) {
				if (pack.isEmpty()) return;
				Card dealt = ServiceRandom.randomCard(pack);
				// System.out.println("Cards = " + dealt);
				dealt.removeFromHand(false);
				hands[j].insert(dealt, false);
				// dealt.transfer(hands[j], true);
			}
		}
	}


	public static void callDelay(long time) {
		delay(time);
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
	private Hand[] hands;
	private Location hideLocation = new Location(-500, - 500);
	private Location trumpsActorLocation = new Location(50, 50);

	public void setStatus(String string) { setStatusText(string); }

	private Scoreboard scoreboard = Scoreboard.getInstance(nbPlayers, this);
	private Round round;

	Font bigFont = new Font("Serif", Font.BOLD, 36);


	public void update(int player, String text) {
		if(scoreActors[player] != null){
			removeActor(scoreActors[player]);
		}
		scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}


	private Card selected;

	private void initRound() {
		for (int j = 0; j < nbPlayers; j++) scoreboard.trickInitial(j);
		hands = new Hand[nbPlayers];
		for (int i = 0; i < nbPlayers; i++) {
			hands[i] = new Hand(deck);
			round.getPlayers().get(i).setHand(hands[i]);
		}
		dealingOut(hands, nbPlayers, nbStartCards);
		for (int i = 0; i < nbPlayers; i++) {
			hands[i].sort(Hand.SortType.SUITPRIORITY, true);
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
		//for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide the cards in a hand (make them face down)
		//	hands[i].setVerso(true);			// You do not need to use or change this code.
		// End graphics
	}

	private void playRound() {
		// Select and display trump suit
		final Oh_Heaven.Suit trumps = ServiceRandom.randomEnum(Oh_Heaven.Suit.class);
		final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
		addActor(trumpsActor, trumpsActorLocation);
		// End trump suit
		round.init(trumps);

		Hand trick;
		int nextPlayer = ServiceRandom.get().nextInt(nbPlayers); // randomly select player to lead for this round
		scoreboard.initBids(nextPlayer, nbStartCards);
		for (int i = 0; i < nbStartCards; i++) {
			trick = new Hand(deck);
			round.setTrick(trick);
			for (int j = 0; j < nbPlayers; j++) {
				if (nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
				selected = null;

				if (!round.checkAuto(nextPlayer)) {  // Select lead depending on player type
					setStatus("Player 0 double-click on card to lead.");
				} else {
					setStatusText("Player " + nextPlayer + " thinking...");
				}

				//selected = players.get(nextPlayer).playCard(round);
				selected = round.update(nextPlayer);  // update game conditions

				// Follow with selected card
				trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
				trick.draw();
				selected.setVerso(false);  // In case it is upside down
				selected.transfer(trick, true); // transfer to trick (includes graphic effect)
				nextPlayer++;

			}
			delay(600);
			trick.setView(this, new RowLayout(hideLocation, 0));
			trick.draw();
			nextPlayer = round.getWinner() ;
			setStatusText("Player " + nextPlayer + " wins trick.");
			scoreboard.trickUpdate(nextPlayer);
			round.endTrick();
		}
		removeActor(trumpsActor);
	}

	public Oh_Heaven(Properties properties)
	{
		super(700, 700, 30);
		setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");

		parseProperties(properties);
		round = new Round(properties, nbPlayers);

		for (int i=0; i <nbRounds; i++) {
			initRound();
			playRound();
			scoreboard.scoreUpdate();
		};
		Set <Integer> winners = scoreboard.getWinners();
		String winText;
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

	private void parseProperties(Properties properties){

		this.nbStartCards = properties.getProperty("nbStartCards") == null ? nbStartCards :
				Integer.parseInt(properties.getProperty("nbStartCards"));

		this.nbRounds = properties.getProperty("rounds") == null ? nbRounds :
				Integer.parseInt(properties.getProperty("rounds"));

		String seedProp = properties.getProperty("seed");
		Long seed = null;
		if (seedProp != null) seed = Long.parseLong(seedProp);
		ServiceRandom.initServicesRandom(seed);

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
