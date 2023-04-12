/**
 * A subclass of the Hand class and are used to model a hand of triple
 * @author Horace
 *
 */
public class Triple extends Hand{

	/**
	 * public constructor of triple, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the triple
	 * @param cards the cards this hand contains
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	@Override
	boolean isValid() {
		if (this.size() == 3 
				&& this.getCard(0).getRank() == this.getCard(1).getRank() 
				&& this.getCard(1).getRank() == this.getCard(2).getRank()) {
			return true;
		}
		return false;
	}

	@Override
	String getType() {
		return "Triple";
	}
	
}
