package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import java.awt.Point;

public class Pair {

	  private Point From;
	  private Point To;
	  private Card card;

	  public Pair(Point From, Point To, Card card) {
	    this.From = From;
	    this.To = To;
	    this.card = card;
	  }

	  public Point getFrom() { 
		  return From; 
		  }
	  
	  public Point getTo() { 
		  return To; 
		  }
	  
	  public Card getCard() {
		  return card;
	  }


}