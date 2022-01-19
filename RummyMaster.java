import java.util.Scanner;
import java.util.TreeSet;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;

public class RummyMaster {
	private static Scanner input;
	private static CardSet hand;
	private static CardSet enemyHand;
	private static CardSet played;
	private static CardSet enemyPlayed;
	private static CardSet unknown;
	private static int enemyCardCount;
	private static CardMatrix matrix;
	private static DiscardPile pile;

	private static Card userCard() {
		return new Card(input.nextInt(), input.nextInt());
	}

	private static void initUnknown() {
		unknown = new CardSet();
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 13; j++) {
				unknown.addCard(new Card(j, i));
			}
		}
	}
	private static int enemyMinTotalWorth() {
		int unknownEnemyCardCount = enemyCardCount - enemyHand.size();
		int total = enemyHand.getTotalWorth();
		ArrayList<Card> unknownList = unknown.toArrayList();
		for (int i = 0; i < unknownEnemyCardCount; i++) {
			total += eMTWHelper(unknownList);
		}
		return total;
	}
	private static int eMTWHelper(ArrayList<Card> list) {
		int index = 0;
		int minWorth = 16;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getWorth() < minWorth) {
				index = i;
				minWorth = list.get(i).getWorth();
			}
		}
		list.remove(index);
		return minWorth;
	}
	private static void fullMatrixUpdate() {
		matrix.updateMatrix(hand.toArrayList(), 'U');
		matrix.updateMatrix(pile.toStack(), 'd');
		matrix.updateMatrix(enemyHand.toArrayList(), 'E');
		matrix.updateMatrix(enemyPlayed.toArrayList(), 'e');
		matrix.updateMatrix(played.toArrayList(), 'u');
	}

	private static void printMatrix() {
		fullMatrixUpdate();
		System.out.print(matrix);
	}

	private static void discard(Card card, CardSet from) {
		pile.addCard(card);
		if (from.contains(card)) from.removeCard(card);
		if (unknown.contains(card)) unknown.removeCard(card);
	}

	private static void playCards(Card cards[], CardSet from, CardSet to) {
		for (Card card : cards) {
			if (from.contains(card)) from.removeCard(card);
			if (unknown.contains(card)) unknown.removeCard(card);
			to.addCard(card);
		}
	}
	private static void addCard(Card card, CardSet to) {
		if (unknown.contains(card)) unknown.removeCard(card);
		to.addCard(card);
	}
	private static void addCard(Card card, DiscardPile to) {
		if (unknown.contains(card)) unknown.removeCard(card);
		to.addCard(card);
	}
	public static void main(String[] args) {
		initUnknown();
		input = new Scanner(System.in);
		hand = new CardSet();
		pile = new DiscardPile();
		matrix = new CardMatrix();
		enemyHand = new CardSet();
		played = new CardSet();
		enemyPlayed = new CardSet();
		enemyCardCount = 7;
		System.out.println(matrix);
		System.out.println("Enter the 7 cards in your hand.");
		for (int i = 0; i < 7; i++) {
			addCard(userCard(), hand);
		}
		System.out.println("Enter first discard card");
		pile.addCard(userCard());
		int user = 0;
		while (user != -1) {
		    System.out.println("__________________________________________");
			printMatrix();
			System.out.println("Enemy card count: " + enemyCardCount);
			System.out.println("Enemy minimum hand worth: " + enemyMinTotalWorth());
			System.out.println("Your hand worth: " + hand.getTotalWorth());
			System.out.println("Enemy points on the board: " + enemyPlayed.getTotalWorth());
			System.out.println("Your points on the board: " + played.getTotalWorth());
			System.out.println("Options:");
			System.out.println("-1: Quit");
			System.out.println(" 1: Add card to hand");
			System.out.println(" 2: Discard card");
			System.out.println(" 3: Take from discard pile");
			System.out.println(" 4: Play cards");
			System.out.println(" 5: Enemy takes from discard pile");
			System.out.println(" 6: Enemy discards card");
			System.out.println(" 7: Enemy makes play");
			System.out.println(" 8: Increment enemy card count");
			user = input.nextInt();
			Card array[];
			switch (user) {
				case 1:
					System.out.println("Enter card to add");
					hand.addCard(userCard());
					break;
				case 2:
					System.out.println("Enter card to discard");
					discard(userCard(), hand);
					break;
				case 3:
					System.out.println("Enter card to take from discard pile");
					pile.getCard(userCard(), hand);
					break;
				case 4:
					System.out.println("How many cards to play?");
					array = new Card[input.nextInt()];
					for (int i = 0; i < array.length; i++) {
						array[i] = userCard();
					}
					playCards(array, hand, played);
					break;
				case 5:
					System.out.println("What card do they take from the discard pile?");
					enemyCardCount += pile.getCard(userCard(), enemyHand);
					break;
				case 6:
					System.out.println("What card do they discard?");
					discard(userCard(), enemyHand);
					break;
				case 7:
					System.out.println("How many cards do they play?");
					array = new Card[input.nextInt()];
					enemyCardCount -= array.length;
					for (int i = 0; i < array.length; i++) {
						array[i] = userCard();
					}
					playCards(array, enemyHand, enemyPlayed);
					break;
				case 8:
					System.out.println("Incrementing enemy card count...");
					enemyCardCount++;
					break;
			}
			if (enemyCardCount == 0 || hand.size()) {
				System.out.println("Game Over!");
				break;
			}
		}


	}
}

class DiscardPile {
	private Stack<Card> faceup;

	public DiscardPile() {
		faceup = new Stack<Card>();
	}

	public void addCard(Card card) {
		if (this.contains(card)) throw new RuntimeException("Duplicate Cards Detected.");
		faceup.push(card);
	}

	//Gets every card until we reach destination card.
	public int getCard(Card target, CardSet receiving) {
		int position = faceup.search(target);
		if (position == -1) throw new RuntimeException("Card Not in Stack.");
		else {
			for (int i = 0; i < position; i++) {
				receiving.addCard(faceup.pop());
			}
		}
		return position;
	}

	public boolean contains(Card target) {
		for (Card card : faceup) {
			if (card.equals(target)) return true;
		}
		return false;
	}

	public Stack<Card> toStack() {
		@SuppressWarnings("unchecked")
		Stack<Card> clone = (Stack<Card>) faceup.clone();
		return clone;
	}

	public String toString() {
		return "Discard Pile: " + faceup;
	}
}

class CardSet {
	private TreeSet<Card> clubs;
	private TreeSet<Card> diamonds;
	private TreeSet<Card> hearts;
	private TreeSet<Card> spades;

	public CardSet() {
		clubs = new TreeSet<Card>();
		diamonds = new TreeSet<Card>();
		hearts = new TreeSet<Card>();
		spades = new TreeSet<Card>();
	}

	private TreeSet<Card> getSuitSet(Card card) {
		switch (card.getSuit()) {
			case 1:
				return clubs;
			case 2:
				return diamonds;
			case 3:
				return hearts;
			default:
				return spades;
		}
	}

	public void addCard(Card card) {
		TreeSet<Card> suit = getSuitSet(card);
		if (!suit.add(card)) throw new RuntimeException("Duplicate Cards Detected.");
	}

	public void removeCard(Card card) {
		TreeSet<Card> suit = getSuitSet(card);
		if (!suit.remove(card)) throw new RuntimeException("Card not in cardset");
	}

	public void addCards(Iterable<Card> cards) {
		for (Card card : cards) {
			this.addCard(card);
		}
	}

	public void addCards(Card cards[]) {
		for (Card card : cards) {
			this.addCard(card);
		}
	}

	public String toString() {
		return "Clubs: " + clubs + "\nDiamonds: " + diamonds + "\nHearts: " + hearts + "\nSpades: " + spades;
	}

	public boolean contains(Card target) {
		return getSuitSet(target).contains(target);
	}
	public int size() {
		return clubs.size() + diamonds.size() + hearts.size() + spades.size();
	}
	public ArrayList<Card> toArrayList() {
		ArrayList<Card> wholeSet = new ArrayList<Card>(size());
		wholeSet.addAll(clubs);
		wholeSet.addAll(diamonds);
		wholeSet.addAll(hearts);
		wholeSet.addAll(spades);
		return wholeSet;
	}

	public int getTotalWorth() {
		int total = 0;
		for (Card card : clubs) total += card.getWorth();
		for (Card card : diamonds) total += card.getWorth();
		for (Card card : hearts) total += card.getWorth();
		for (Card card : spades) total += card.getWorth();
		return total;
	}
}

class CardMatrix {
	private char matrix[][];
	private HashMap<Character, String> colors;

	public CardMatrix() {
		matrix = new char[4][13];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				matrix[i][j] = '?';
			}
		}
		initColors();
	}

	public void updateMatrix(Iterable<Card> cards, char label) {
		for (Card card : cards) {
			matrix[card.getSuit() - 1][card.getValue() - 1] = label;
		}
	}

	private void initColors() {
		if (colors == null) {
			colors = new HashMap<Character, String>();
			colors.put('U', "\u001b[34m"); //Blue
			colors.put('u', "\u001b[34m"); //Blue
			colors.put('E', "\u001b[31m"); //Red
			colors.put('e', "\u001b[31m"); //Red
			colors.put('d', "\u001b[32m"); //Green
			colors.put('?', "\u001b[35m"); //Magenta
		}

	}

	private String rowString(int row, String suitChar) {

		String str = suitChar;
		for (char x : matrix[row]) {
			str += colors.get(x) + " " + x;
		}
		str += "\n\u001b[0m"; //Newline + RESET color.
		return str;
	}

	public String toString() {
		String str = "      A 2 3 4 5 6 7 8 9 X J Q K\n";
		str += rowString(0, "1 CLB");
		str += rowString(1, "2 DMD");
		str += rowString(2, "3 HRT");
		str += rowString(3, "4 SPD");
		return str;
	}
}

class Card
		implements Comparable<Card> {
	/*
	Values:
	1: Ace
	2 - 10: Respective number values
	11: Jack
	12; Queen
	13: King
	Suits:
	1: Clubs
	2: Diamonds
	3: Hearts
	4: Spades
	*/
	private int value;
	private int worth;
	private int suit;

	private void determineWorth() {
		if (value == 1) worth = 15;
		else if (value <= 9) worth = 5;
		else worth = 10;
	}

	public Card(int value, int suit) {
		if (suit > 4 || suit < 1) throw new IllegalArgumentException("Invalid Suit Number");
		if (value > 13 || value < 1) throw new IllegalArgumentException("Invalid Card Value");
		this.value = value;
		this.suit = suit;
		determineWorth();
	}

	public int getValue() {
		return value;
	}

	public int getWorth() {
		return worth;
	}

	public int getSuit() {
		return suit;
	}

	public String toString() {
		String cardString = "";
		if (value == 1) cardString += "ace";
		else if (value <= 10) cardString += String.valueOf(value);
		else if (value == 11) cardString += "jack";
		else if (value == 12) cardString += "queen";
		else cardString += "king";
		cardString += " of ";
		switch (suit) {
			case 1:
				cardString += "clubs";
				break;
			case 2:
				cardString += "diamonds";
				break;
			case 3:
				cardString += "hearts";
				break;
			default:
				cardString += "spades";
				break;
		}
		return cardString;
	}
	
	public boolean equals(Object x) {
		if (!(x instanceof Card)) return false;
		Card card = (Card) x;
		return this.getValue() == card.getValue() && this.getSuit() == card.getSuit();
	}

	public int compareTo(Card card) { //Comparison made solely on card value.
		return this.getValue() - card.getValue();
	}
}
