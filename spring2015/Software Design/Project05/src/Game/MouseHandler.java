package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import javax.swing.event.MouseInputAdapter;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseInputAdapter {

    private int clickDisX; // displacement from topleft corner of card
                           // to where mouse was licked (x position)
    private int clickDisY; // displacement from topleft corner of card
                           // to where mouse was licked (y position)
    private Point origin;  // where teh card was originally before movement
    private Game carpetSolitaire;

    public MouseHandler(Game g) {
        carpetSolitaire = g;
    }
    


    /**
     * Handles the event when a card is dragged
     * @param e the MouseEvent which calls this method
     */
    public void mouseDragged(MouseEvent e) {
        // get the components we'll need to reference
        Card card = (Card) e.getComponent();
        JPanel panel = (JPanel) card.getParent();

        // set the new x and y coordinates of the card and
        // make sure it's not dragged off the panel
        int newX = Math.max(card.getX() + e.getX() - clickDisX, 0);
        newX = Math.min(newX, panel.getWidth() - Card.CARD_W);
        int newY = Math.max(card.getY() + e.getY() - clickDisY, 0);
        newY = Math.min(newY, panel.getHeight() - Card.CARD_H);

        //set the card's location
        card.setLocation(newX, newY);
    }

    /**
     * Handles the event when a card is pressed
     * @param e the MouseEvent which calls this method
     */
    public void mousePressed(MouseEvent e) {
        // get the components we'll need to reference
        Card card = (Card) e.getComponent();
        JPanel panel = (JPanel) card.getParent();
        card.setFaceUp(true);
        card.setIcon();

        // get coordinate information in anticipation of dragging
        origin = card.getLocation();
        clickDisX = e.getX();
        clickDisY = e.getY();

        // bring the card aboev all others and repaint it
        panel.setComponentZOrder(card, 0);
        panel.repaint();
    }

    /**
     * Handles the event when a card is released
     * @param e the MouseEvent which calls this method
     */
    public void mouseReleased(MouseEvent e) {
    	
        // get the components we'll need to reference        
        Card card = (Card) e.getComponent();
        JPanel panel = (JPanel) card.getParent();

        // here we want to make sure the release was inside the panel
        // and above a free spot
        Point mouseDrop = panel.getMousePosition();
        if(mouseDrop != null){
            Rectangle free = carpetSolitaire.getFreeSpotAt(mouseDrop);
            if(free != null){
                // the drop spot appears to be legal, now we check the card
                // to make sure the card is valid in this spot
                Component leftOfFree = panel.getComponentAt(
                        free.getLocation().x - Game.BEVEL - Card.CARD_W,
                        free.getLocation().y);
                if((leftOfFree == null && card.getRank() == 0) ||
                        (leftOfFree instanceof Card &&
                                ((Card)leftOfFree).getRank() == card.getRank() - 1 &&
                                ((Card)leftOfFree).getSuit().equals(card.getSuit()))) {
                    // based on the rules of the carpetSolitaire this card can be moved to this spot
                    // so move the card, update the free spots and check if the carpetSolitaire is won
                    card.setLocation(origin);
                    carpetSolitaire.addMove();
                    card.setLocation(free.getLocation());
                    free.setLocation(origin);
                    carpetSolitaire.checkGameWon();
                    return;
                }
            }
        
        // drop is not legal, return the card to its origin and beep
        card.setLocation(origin);
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    	
    	
    }
    
}
