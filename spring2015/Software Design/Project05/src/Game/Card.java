package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel {

	public enum Suit {
		Spades, Hearts, Clubs, Diamonds
	}

	public enum Face {
		ace, two, three, four, five, six, seven, eight, nine, ten, jack, queen, king
	}

	public static final int CARD_H = 97; // height of the card in pixels
	public static final int CARD_W = 73; // width of the card in pixels

	private Suit suit;
	private Face face;

	private Game caSolitaire;

	private ImageIcon fu;
	private ImageIcon fd;

	private boolean faceUp;

	public Card(Suit s, Face f, MouseHandler mh,
			Game cas) {
		super(new ImageIcon("cardImages/" + f + s + ".gif"));
		fu = new ImageIcon("cardImages/" + f + s + ".gif");
		fd = new ImageIcon("cardImages/b2.gif");
		suit = s;
		face = f;
		faceUp = false;
		this.addMouseListener(mh);
		this.addMouseMotionListener(mh);
		caSolitaire = cas;
	}

	/**
	 * returns the suit that this card is of
	 * 
	 * @return a String corresponding to this card's suit
	 */
	public String getSuit() {
		return suit.toString();
	}

	/**
	 * returns the face (rank) of this card in string form
	 * 
	 * @return a String corresponding to this card's rank
	 */
	public String getFace() {
		return face.toString();
	}

	/**
	 * returns the face (rank) of this card in integer form
	 * 
	 * @return an int corresponding to this card's rank
	 */
	public int getRank() {
		return face.ordinal();
	}

	public void setIcon() {
		if (faceUp == true) {
			this.setIcon(fu);
		} else {
			this.setIcon(fd);
		}
	}

	public void setFaceUp(boolean x) {
		faceUp = x;
	}
}
