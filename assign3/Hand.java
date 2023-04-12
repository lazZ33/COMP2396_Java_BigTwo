import java.util.ArrayList;
import java.util.Arrays;

/**
 * A subclass of the CardList class and is used to model a hand of cards.
 * @author Horace
 *
 */
public abstract class Hand extends CardList{
	
	/**
	 * a constructor for building a hand with the specified player and list of cards
	 * @param player the player owns the hand
	 * @param cards the cards this hand contains
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i = 0; i < cards.size(); i++ ) {
			this.addCard(cards.getCard(i));
		}
		this.sort();;
	}
	
	private CardGamePlayer player;
	
	/**
	 * a method for retrieving the player of this hand
	 * @return the player of this hand
	 */
	public CardGamePlayer getPlayer() {
		return this.player;
	}
	
	/**
	 * a method for retrieving the top card of this hand
	 * @return the top card of this hand
	 */
	public Card getTopCard() {
		return this.getCard(4);
	}
	
	/**
	 * a method for checking if this hand beats a specified hand
	 * @param hand the hand to be compared
	 * @return boolean of whether this hand beats the given hand
	 */
	public boolean beats(Hand hand) {
		// check for which hands win
		if (!(this.size() == hand.size())) {
			 return false;
		}
		switch (hand.size()) {
			case 1:
			case 2:
			case 3:
				if (this.getCard(0).compareTo(hand.getCard(0)) > 0){
					return true;
				}
				return false;
			case 5:
				ArrayList<String> hand_ranking = new ArrayList<String>(Arrays.asList("Straight", "Flush", "FullHouse", "Quad", "StraightFlush"));
				if (hand_ranking.indexOf(this.getType()) > hand_ranking.indexOf(hand.getType())){
					return true;
				}
				else if (this.getType() == hand.getType()){
					// check in case the same type the larger one wins
					switch (hand.getType()) {
						case "Straight":
							return this.getTopCard().compareTo(hand.getTopCard()) == 1;
						case "Flush":
							if (this.getTopCard().getSuit() > hand.getTopCard().getSuit()) {
								return true;
							}
							if (this.getTopCard().getSuit() < hand.getTopCard().getSuit()) {
								return false;
							}
							if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
								return true;
							}
							return false;
						case "FullHouse":
						case "Quad":
							int my_rank = this.getCard(2).getRank();
							if (my_rank == 0 | my_rank == 1) {
								my_rank += 13;
							}
							int its_rank = hand.getCard(2).getRank();
							if (its_rank == 0 | its_rank == 1) {
								its_rank += 13;
							}
							
							if (my_rank > its_rank) {
								return true;
							}
							return false;
						case "StraightFlush":
							return this.getTopCard().compareTo(hand.getTopCard()) == 1;
					}
				}
				return false;
		}
		
		return false;
	}
	
	/**
	 * a method for checking if this is a valid hand
	 * @return boolean of whether if this is a valid hand
	 */
	abstract boolean isValid();
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return a string specifying the type of this hand
	 */
	abstract String getType();
}
