/**
 * A subclass of the Card class and is used to model a card used in a Big Two card game
 * @author Horace
 *
 */
public class BigTwoCard extends Card{
	
	/**
	 * a constructor for building a card with the specified suit and rank
	 * @param suit the suit of the card
	 * @param rank the rank of the card
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	/**
	 * a method for comparing the order of this card with the specified card
	 * @param card the card compares to 
	 * @return Returns a negative integer, zero, or a positive integer when this card is less than, equal to, or greater than the specified card
	 */
	public int compareTo(Card card) {

		int my_rank = this.rank;
		if (this.rank == 0 | this.rank == 1) {
			my_rank += 13;
		}
		int its_rank = card.rank;
		if (its_rank == 0 | its_rank == 1) {
			its_rank += 13;
		}
		
		if ( its_rank > my_rank) {
			return -1;
		}
		if ( its_rank < my_rank) {
			return 1;
		}

		if (this.suit < card.suit) {
			return -1;
		}
		if (this.suit > card.suit) {
			return 1;
		}		
		
		return 0;

	}
}
