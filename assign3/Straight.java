/**
 * A subclass of the Hand class and are used to model a hand of straight
 * @author Horace
 *
 */
public class Straight extends Hand{

	/**
	 * public constructor of Straight, simply a wrapper for the super class constructor of Hand
	 * @param player the player owns the straight
	 * @param cards the cards this hand contains
	 */
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	@Override
	public Card getTopCard() {
		if (this.getCard(4).getRank() == 0 && this.getCard(3).getRank() == 12) {
			return this.getCard(4);
		}
		Card topCard = this.getCard(0);
		for (int i = 1; i < 5; i++) {
			if (this.getCard(i).getRank() > topCard.getRank()) {
				topCard = this.getCard(i);
			}
		}
		return topCard;
	}

	@Override
	boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		
		for (int i = 0; i < 5 - 1; i++) {
			if (!(this.getCard(i).getRank() == this.getCard(i + 1).getRank() - 1)) {
				if(i == 3 && this.getCard(4).getRank() == 0 && this.getCard(3).getRank() == 12) {
					continue;
				}
				if(i == 3 && this.getCard(4).getRank() == 1 && this.getCard(3).getRank() == 5) {
					continue;
				}
				if(i == 2 && this.getCard(3).getRank() == 0 && this.getCard(2).getRank() == 4) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	String getType() {
		return "Straight";
	}
	
}
