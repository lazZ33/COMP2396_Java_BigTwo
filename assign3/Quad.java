/**
 * A subclass of the Hand class and are used to model a hand of quad
 * @author Horace
 *
 */
public class Quad extends Hand{

	/**
	 * public constructor of quad, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the quad
	 * @param cards the cards this hand contains
	 */
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	@Override
	boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		if (!(this.getCard(1).getRank() == this.getCard(2).getRank()
				&& this.getCard(2).getRank() == this.getCard(3).getRank()
				&& this.getCard(0).getRank() == this.getCard(2).getRank() | this.getCard(4).getRank() == this.getCard(2).getRank())) {
			return false;
		}
		return true;
	}

	@Override
	String getType() {
		return "Quad";
	}
	
}
