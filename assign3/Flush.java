/**
 * A subclass of the Hand class and are used to model a hand of flush
 * @author Horace
 *
 */
public class Flush extends Hand{

	/**
	 * public constructor of flush, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the flush
	 * @param cards the cards this hand contains
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	@Override
	boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		for (int i = 0; i < 5 - 1; i++) {
			if (!(this.getCard(i).getSuit() == this.getCard(i + 1).getSuit())) {
				return false;
			}
		}
		return true;
	}

	@Override
	String getType() {
		return "Flush";
	}
	
}
