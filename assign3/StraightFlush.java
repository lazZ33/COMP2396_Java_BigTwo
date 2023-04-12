/**
 * A subclass of the Hand class and are used to model a hand of straight flush
 * @author Horace
 *
 */
public class StraightFlush extends Hand{

	/**
	 * public constructor of Straight flush, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the straight flush
	 * @param cards the cards this hand contains
	 */
	public StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	@Override
	boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		for (int i = 0; i < 5 - 1; i++) {
			if (!(this.getCard(i).getSuit() == this.getCard(i + 1).getSuit()
					&& this.getCard(i).getRank() == this.getCard(i + 1).getRank() - 1)) {
				return false;
			}
		}
		return true;
	}

	@Override
	String getType() {
		return "StraightFlush";
	}
	
}
