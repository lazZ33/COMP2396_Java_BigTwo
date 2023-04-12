/**
 * A subclass of the Hand class and are used to model a hand of single
 * @author Horace
 *
 */
public class Single extends Hand{

	/**
	 * public constructor of single, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the single
	 * @param cards the cards this hand contains
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	@Override
	boolean isValid() {
		if (this.size() == 1) {
			return true;
		}
		return false;
	}

	@Override
	String getType() {
		return "Single";
	}
	
}
