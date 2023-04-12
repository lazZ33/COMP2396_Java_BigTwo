import java.util.ArrayList;

import javax.swing.JOptionPane;
/**
 * The class implements the CardGame interface and is used to model a Big Two card game
 * @author Horace
 *
 */
public class BigTwo implements CardGame{
	
	/**
	 * a constructor for creating a Big Two card game
	 */
	public BigTwo(){
		this.handOnTable = new ArrayList<Hand>();
		// create 4 players and add them to player list
		this.numOfPlayers = 4;
		this.playerList = new ArrayList<CardGamePlayer>();
		for (int i = 0; i < this.numOfPlayers; i++) {
			this.playerList.add(new CardGamePlayer());
			this.playerList.get(i).setName(null);
		}
		// create a BigTwoUI object for providing the user interface
		this.ui = new BigTwoGUI(this);
		// create a BigTwoClient object
		this.client = new BigTwoClient(this, this.ui);
	}
	
	private BigTwoClient client;
	private int numOfPlayers;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handOnTable;
	private int currentPlayerIdx;
	private BigTwoGUI ui;
	
	/**
	 * a method for getting the number of players
	 * @return the number of players
	 */
	public int getNumOfPlayers() {
		return this.numOfPlayers;
	}
	
	/**
	 * a method for retrieving the deck of cards being used
	 * @return the deck of cards being used
	 */
	public Deck getDeck() {
		return this.deck;
	}
	
	/**
	 * a method for retrieving the list of players
	 * @return the list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){
		return this.playerList;
	}

	/**
	 *  a method for retrieving the list of hands played on the table
	 *  @return the list of hands played on the table
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return this.handOnTable;
	}
	
	/**
	 * a method for retrieving the index of the current player
	 * @return the index of the current player
	 */
	public int getCurrentPlayerIdx() {
		return this.currentPlayerIdx;
	}
	
	/**
	 * a method for starting/restarting the game with a given shuffled deck of cards
	 * @param deck a shuffled deck of cards for distributing to players
	 */
	public void start(Deck deck) {
		this.ui.printSysMessage("All players ready, game starts.\n");
		// remove all the card from players and table
		this.handOnTable.removeAll(handOnTable);			
		for (CardGamePlayer player : this.playerList) {
			player.removeAllCards();
		}
		
		this.deck = deck;

		// distribute the cards to the players
		// identify player who holds the three of diamond
		// set both currentPlayerIdx of the BigTwo obj and the activePlayer of 
		// the BigTwoUI obj to the idx of the player who holds the three of diamond
		for (int i = 0; i < deck.size(); i++) {
			Card cur_card = deck.getCard(i);
			CardGamePlayer cur_player = this.playerList.get(i % this.numOfPlayers);
			cur_player.addCard(cur_card);
			if (cur_card.getRank() == 2 && cur_card.getSuit() == 0) {
				this.currentPlayerIdx = i % this.numOfPlayers;
				this.ui.setActivePlayer(i % this.numOfPlayers);
			}
		}
		
		for (int i = 0; i < this.playerList.size(); i++) {
			this.playerList.get(i).sortCardsInHand();;
		}
				
		// call the repaint() method of the BigTwoUI obj to show cards on table
		this.ui.repaint();
		// call promptActivePlayer() method of the BigTwoUI obj to prompt user to select cards and make his/her moves.
		this.ui.promptActivePlayer();
	}
	
	/**
	 * a method for connecting the game to the server, a wrapper for calling the client's connect()
	 */
	public void connectGame() {
		if (this.client == null) {
			this.client = new BigTwoClient(this, this.ui);
			return;
		}
		this.client.connect();
	}
	
	/**
	 * a method for setting the client of the BigTwo game
	 * @param client the client to be set
	 */
	public void setClient(BigTwoClient client) {
		this.client = client;
	}
	
	/**
	 * a method for getting the client of the BigTwo game
	 * @return the client of the game
	 */
	public BigTwoClient getClient() {
		return this.client;
	}
	
	/**
	 * making a move by a player with the specified index using the card specified by the list of indices
	 * @param playerIdx the player index to make the move
	 * @param cardIdx the card index to make the move
	 */
	public void makeMove(int playerIdx, int[] cardIdx) {
		// call checkMove() method with the playerIdx and cardIdx as arguments
		this.checkMove(currentPlayerIdx, cardIdx);
	}
	
	/**
	 * checking a move made by a player, should be called from the makeMove() method
	 * @param playerIdx the player index that is making a move
	 * @param cardIdx the card index that is making a move
	 */
	public void checkMove(int playerIdx, int[] cardIdx) {
		// check a move
		if (cardIdx == null) {
			if (this.handOnTable.size() == 0) {
				this.ui.printSysMessage("Not a legal move!!!\n");
				return;
			}else if (this.handOnTable.get(this.handOnTable.size()-1).getPlayer() == this.playerList.get(playerIdx)) {
				this.ui.printSysMessage("Not a legal move!!!\n");
				return;
			}
			this.ui.printSysMessage("{Pass}\n");
			if (playerIdx == this.client.getPlayerID()) {
				this.client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, null));
			}
			this.currentPlayerIdx = (this.currentPlayerIdx + 1) % this.numOfPlayers;
			this.ui.setActivePlayer(this.currentPlayerIdx);
			this.ui.repaint();
			this.ui.promptActivePlayer();
			return;
		}

		CardGamePlayer player = this.playerList.get(playerIdx);
		CardList played_card_list = player.play(cardIdx);
		if (played_card_list == null) {
			return;
		}
		
		Hand cur_hand = BigTwo.composeHand(player, played_card_list);

		if (cur_hand == null) {
			this.ui.printSysMessage("Not a legal move!!!\n");
			return;
		}
		
		do {

			if (this.handOnTable.size() == 0) {
				if (!(cur_hand.contains(new Card(0,2)))) {
					break;
				}
				this.ui.printSysMessage("{" + cur_hand.getType() +  "} ");
				for (int cur_card_idx = 0; cur_card_idx < cur_hand.size(); cur_card_idx++) {
					this.ui.printSysMessage(cur_hand.getCard(cur_card_idx).toString());
				}
				this.ui.printSysMessage("\n");
				this.handOnTable.add(cur_hand);
				player.removeCards(cur_hand);
				if (playerIdx == this.client.getPlayerID()) {
					this.client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
				}
				this.currentPlayerIdx = (this.currentPlayerIdx + 1) % this.numOfPlayers;
				this.ui.setActivePlayer(this.currentPlayerIdx);
				this.ui.repaint();
				this.ui.promptActivePlayer();
				this.ui.disable();
				if (this.endOfGame()) {
					this.gameEnds();
				}
				return;
			}

			Hand prev_hand = this.handOnTable.get(this.handOnTable.size()-1);
			
			if (cur_hand.beats(prev_hand) | prev_hand.getPlayer() == this.playerList.get(playerIdx)) {
				this.ui.printSysMessage("{" + cur_hand.getType() +  "}");
				for (int cur_card_idx = 0; cur_card_idx < cur_hand.size(); cur_card_idx++) {
					this.ui.printSysMessage(cur_hand.getCard(cur_card_idx).toString());
				}
				this.ui.printSysMessage("\n");
				this.handOnTable.add(cur_hand);
				player.removeCards(cur_hand);
				if (playerIdx == this.client.getPlayerID()) {
					this.client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
				}
				this.currentPlayerIdx = (this.currentPlayerIdx + 1) % this.numOfPlayers;
				this.ui.setActivePlayer(this.currentPlayerIdx);
				this.ui.repaint();
				this.ui.promptActivePlayer();
				this.ui.disable();
				if (this.endOfGame()) {
					this.gameEnds();
				}
				return;
			}
		}while(false);

		this.ui.printSysMessage("Not a legal move!!!\n");
		return;
	}
	
	/**
	 * checking if the game ends
	 * @return boolean of whether or not the game ends
	 */
	public boolean endOfGame() {
		// check if game ends
		for (int i = 0; i < this.playerList.size(); i++) {
			if (this.playerList.get(i).getNumOfCards() == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * a method for starting a Big Two card game
	 * @param args arguments from the command line, unused
	 */
	public static void main(String[] args) {
		BigTwo game = new BigTwo();
	}
	
	/**
	 * a method for returning a valid hand from the specified list of cards of the player
	 * @param player player that to be returned a valid hand
	 * @param cards cards specified for composing the valid hand
	 * @return a valid Hand, null if no valid hand can be composed with given parameters
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		// compose a valid hand for player with cards
		Hand cur_hand = null;
		
		if (cards == null) {
			return null;
		}
		
		switch (cards.size()) {
			case 1:
				Single single_eval = new Single(player, cards);
				if (single_eval.isValid()){
					cur_hand = single_eval;
					break;
				}
			case 2:
				Pair pair_eval = new Pair(player, cards);
				if (pair_eval.isValid()) {
					cur_hand = pair_eval;
					break;
				}
				break;
			case 3:
				Triple triple_eval = new Triple(player, cards);
				if (triple_eval.isValid()) {
					cur_hand = triple_eval;
					break;
				}
			case 5:
				StraightFlush straightflush_eval = new StraightFlush(player, cards);
				if (straightflush_eval.isValid()) {
					cur_hand = straightflush_eval;
					break;
				}
				Quad quad_eval = new Quad(player, cards);
				if (quad_eval.isValid()) {
					cur_hand = quad_eval;
					break;
				}
				FullHouse fullHouse_eval = new FullHouse(player, cards);
				if (fullHouse_eval.isValid()) {
					cur_hand = fullHouse_eval;
					break;
				}
				Flush flush_eval = new Flush(player, cards);
				if (flush_eval.isValid()) {
					cur_hand = flush_eval;
					break;
				}
				Straight straight_eval = new Straight(player, cards);
				if (straight_eval.isValid()) {
					cur_hand = straight_eval;
					break;
				}
			
		}
		
		return cur_hand;
	}
	
	private void gameEnds() {
		this.ui.disable();		
		this.ui.printSysMessage(this.getPlayerList().get(this.currentPlayerIdx).getName() + " wins!\n");

		String title;
		System.out.println((this.currentPlayerIdx+3)%4);
		if (this.client.getPlayerID() == (this.currentPlayerIdx+3)%4) {
			title = "You win!";
		}else {
			title = "You lose!";
		}

		String msg = "";
		for (int i = 0; i < this.numOfPlayers; i++) {
			CardGamePlayer cur_player = this.getPlayerList().get(i);
			int card_count = cur_player.getNumOfCards();
			String player_name;
			if (i == this.client.getPlayerID()) {
				player_name = "You";
			}else {				
				player_name = cur_player.getName();
			}

			if (card_count == 0) {
				msg += String.format("%s has no card left.\n", player_name);
			}else {
				msg += String.format("%s has %d left.\n", player_name, card_count);
			}
		}
		this.ui.windowPromptInfo(msg, title);
		this.client.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}
}
