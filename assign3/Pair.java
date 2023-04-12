/**
 * A subclass of the Hand class and are used to model a hand of pair
 * @author Horace
 *
 */
public class Pair extends Hand{

	/**
	 * public constructor of pair, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the pair
	 * @param cards the cards this hand contains
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	@Override
	boolean isValid() {
		if (this.size() == 2 
				&& this.getCard(0).getRank() == this.getCard(1).getRank()) {
			return true;
		}
		return false;
	}

	@Override
	String getType() {
		return "Pair";
	}
	
}
