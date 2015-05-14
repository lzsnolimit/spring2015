package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import org.w3c.dom.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

import javax.swing.*;

public class Game extends JPanel {
    
    public static final int BEVEL = 10;
    public static final Dimension PANEL_SIZE = new Dimension(14*Card.CARD_W + 15*BEVEL, 4*Card.CARD_H + 5*BEVEL);

    private Stack<List<JLabel>> undoables = new Stack<List<JLabel>>();
    private Stack<List<JLabel>> redoables = new Stack<List<JLabel>>();
    private List<Rectangle> grays = new ArrayList<Rectangle>();
    private List<Card>  deck = new ArrayList<Card>();
    private boolean lastMoveWasUndo = false;    
    private File currentOpenGame = null;
    private int shufflesAllowed = 2;
    private int played = -1;
    private int won = 0;
    private GameGUI GUI;
    
    public Game() {
        super(null);                    //no layout manager
        setPreferredSize(PANEL_SIZE);
        clearGame();
        GUI = new GameGUI(this);
        setBackground(Color.decode("#64C866"));
        newGame();
    }

    /**
     * Calculates the percentage of games that have been won
     * @return the integer percentage of games won
     */
    private int getPercentWon() {
        return played == 0 ? 0 : (won * 100) / played;
    }

    /**
     * Handles displaying messages in a JOptionPane
     * @param message the String that is the message to display
     * @param title the String that is displayed as the windows title
     */
    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(GUI, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * clears the game's board and resets it with 56 grey spots and no cards
     */
    private void clearGame() {
        removeAll();
        for (int i = 0; i < 56; i++) {
            Rectangle bounds = getBounds(i);
            JLabel gray = new JLabel(new ImageIcon("cardImages/gray.gif"));
            gray.setBounds(bounds); //set the location of the spot
            add(gray);
        }
    }

    /**
     * Generates the original first column of 4 grey spots
     * @return a list of Rectangles that are the starting free spots
     */
    private List<Rectangle> getDefaultGrays() {
        grays.clear();
        for (int i = 0; i < 4; i++)
            grays.add(getBounds(i));
        return grays;
    }

    /**
     * Generates a new deck of cards and shuffles it
     * @return a shuffled deck of cards
     */
    private List<Card> getNewDeck() {
        deck.clear();    
        for (Card.Face face : Card.Face.values())
            for (Card.Suit suit : Card.Suit.values())
                deck.add(new Card(suit, face, new MouseHandler(this),this));
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Restarts the current game over again
     */
    public void sameGame() {
        newGame(deck, getDefaultGrays());
    }

    /**
     * Starts a new game with a new deck of cards
     */
    public void newGame() {
        newGame(getNewDeck(), getDefaultGrays());
    }

    /**
     * Starts a new game with the provided deck and provided free spots
     * @param cards the List of Cards to use in this game
     * @param freespots the list of open spots to use in this game
     */
    public void newGame(List<Card> cards, List<Rectangle> freespots) {
        currentOpenGame = null;
        shufflesAllowed = 2;
        played++;
        undoables.clear();
        redoables.clear();        
        clearGame();
        dealCards(cards, freespots);
        repaint();
        checkUndoRedo();
    }

    /**
     * Deals the provided deck of cards as a new game leaving the provided free spots open
     * @param cards the list of cards to use
     * @param freespots the list of free spots to use
     */
    private void dealCards(List<Card> cards, List<Rectangle> freespots) {
        deck = cards;
        grays = freespots;
        Iterator<Card> itr = deck.iterator();
        for (int i = 0; i < 56; i++) {
            Rectangle bounds = getBounds(i);
            if(getFreeSpotAt(bounds.getLocation()) == null){
                Card c = itr.next();
                c.setBounds(bounds);
                add(c);
                setComponentZOrder(c, 0);
            }
        }
    }

    /**
     * Helper method for determining where a card is painted relative to deck position
     * @param i and Integer corresponding to the cards position in the deck
     * @return the Rectangle that corresponds to where that card should be painted
     */
    private Rectangle getBounds(int i) {
        return new Rectangle(BEVEL+(i/4)*(Card.CARD_W+BEVEL),
                             BEVEL+(i%4)*(Card.CARD_H+BEVEL),
                             Card.CARD_W, Card.CARD_H);
    }

    /**
     * Checks if the game has been won and if so displays a message
     */
    public void checkGameWon() {
        for (int i = 0; i < 4; i++) {
            Component first = getComponentAt(              //get the first card of the row,
                    BEVEL, BEVEL + i*(BEVEL+Card.CARD_H)); //should be an ace
            if (!(first instanceof Card)) { return; }
            for (int j = 1; j < 13; j++) {                 //for the rest of the row
                Component spot = getComponentAt(
                  BEVEL + j*(BEVEL+Card.CARD_W), BEVEL + i*(BEVEL+Card.CARD_H));
                //make sure the card has the right rank for its spot and matches the suit
                if (!((spot instanceof Card) && (((Card)spot).getRank() == j)
                        && (((Card)spot).getSuit().equals(((Card) first).getSuit())))) {
                    return;
                }
            }
        }
        won++;
        showMessage("You have won the game of Carpet Solitaire!","You Win!");
    }

    /**
     * takes the current game and rolls it up into a list
     * @return the list of JLabels corresponding to this game's state
     */
    public List<JLabel> rollDeck() {
        ArrayList<JLabel> rolledDeck = new ArrayList<JLabel>();
        for(int i = 0; i < 56; i++) {
            rolledDeck.add((JLabel)getComponentAt(getBounds(i).getLocation()));
        }
        return rolledDeck;                                   
    }

    /**
     * Takes a game state and unrolls it as a new game
     * @param indeck the List of JLabels corresponding to a game state
     */
    public void unrollDeck(List<JLabel> indeck) {
        ArrayList<Card> newdeck = new ArrayList<Card>();
        ArrayList<Rectangle> newgrays = new ArrayList<Rectangle>();
        for(int i = 0; i < 56; i++) {
            if(!(indeck.get(i) instanceof Card))
                newgrays.add(new Rectangle(indeck.get(i).getBounds()));
            else
                newdeck.add((Card)indeck.get(i));
        }
        clearGame();
        dealCards(newdeck, newgrays);        
        repaint();
    }

    /**
     * Returns the free spot at a given point P
     * @param p the coordinate Point to look for a free spot
     * @return the free spot (Rectangle) if there is one, else null
     */
    public Rectangle getFreeSpotAt(Point p){
        for (Rectangle free : grays)
            if (free.contains(p))
                return free;
        return null;
    }

    /**
     * adds a move to the game
     */
    public void addMove() {
        if(lastMoveWasUndo) { redoables.clear(); }
        lastMoveWasUndo = false;
        undoables.add(rollDeck());
        checkUndoRedo();
    }

    /**
     * Undo a move
     */
    public void undoMove() {
        if(!(undoables.isEmpty())) {
            redoables.push(rollDeck());
            unrollDeck(undoables.pop());
            lastMoveWasUndo = true;
        } 
        checkUndoRedo();
    }

    /**
     * Redo a move
     */
    public void redoMove() {
        if(!(redoables.isEmpty())) {
            undoables.push(rollDeck());            
            unrollDeck(redoables.pop());
            lastMoveWasUndo = false;
        }
        checkUndoRedo();
    }

    /**
     * Makes sure that the undo/redo menus are enabled/disabled
     */
    private void checkUndoRedo() {
        if(undoables.isEmpty()) GUI.disableUndoMenu();
            else GUI.enableUndoMenu();
        if(redoables.isEmpty()) GUI.disableRedoMenu();
            else GUI.enableRedoMenu();
    }

    /**
     * Saves the gave state to an XML file
     */
    public void saveGame() {
        FileHandler fh = new FileHandler(GUI);
        File file = currentOpenGame;
        if(file == null) { file = fh.saveFile(); }
        if(file != null) {
            String output = "<root>\n";
            for (int i = 0; i < 56; i++) {
                Component spot =  getComponentAt(getBounds(i).getLocation());
                if(spot instanceof Card){
                    Card c = (Card) spot;
                    output+="<card face=\""+c.getFace()+"\" suit=\""+c.getSuit()+"\"/>\n";
                } else {
                    output+="<free x=\""+spot.getX()+"\" y=\""+spot.getY()+"\"/>\n";
                }
            }
            output += "</root>";
            fh.writeFile(file, output);
            currentOpenGame = file;
        }
    }

    /**
     * Opens a game and makes it the current game
     */
    public void openGame() {
        XMLLoader xmll = new XMLLoader();
        FileHandler fh = new FileHandler(GUI);
        File file = fh.openFile();
        if(file == null) { showMessage("Error opening file", "ERROR"); return; }
        NodeList children = xmll.getNodes(file);
        if(children == null) { showMessage("Error Parsing XML", "ERROR"); return; }
        List<Card> newDeck = new ArrayList<Card>();
        List<Rectangle> frees = new ArrayList<Rectangle>();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeType() != Node.ELEMENT_NODE) continue;
            //get the card data and add it to the new deck
            if(child.getNodeName().equals("card")) {
                NamedNodeMap nnm = child.getAttributes();
                Card.Suit suit = Card.Suit.valueOf(
                        nnm.getNamedItem("suit").getNodeValue());
                Card.Face face = Card.Face.valueOf(
                        nnm.getNamedItem("face").getNodeValue());
                Card c = new Card(suit, face, new MouseHandler(this),this);
                newDeck.add(c);
            }
            //get the free spot data and add it to the new free spot
            if(child.getNodeName().equals("free")) {
                NamedNodeMap nnm = child.getAttributes();
                int x = Integer.parseInt(nnm.getNamedItem("x").getNodeValue());
                int y = Integer.parseInt(nnm.getNamedItem("y").getNodeValue());
                frees.add(new Rectangle(x, y, Card.CARD_W, Card.CARD_H));
            }
        }
        //make sure the deck is sized correctly and there are exactly 4 free spots
        if(newDeck.size()==52 && frees.size()==4) {
            newGame(newDeck,frees);
            currentOpenGame = file;            
        }
        else showMessage("Error Parsing XML", "ERROR");
    }

    /**
     * Shuffles the current game if one is allowed
     */
    public void shuffleGame() {
        if(shufflesAllowed == 0){java.awt.Toolkit.getDefaultToolkit().beep(); return;}
        addMove();
        //the cards to shuffle
        ArrayList<Card> toShuffle = new ArrayList<Card>();
        //the spots where shuffled cards can go
        ArrayList<Rectangle> openSpots = new ArrayList<Rectangle>();
        //the new 4 open squares of the game
        ArrayList<Rectangle> newFreeSpots = new ArrayList<Rectangle>();
        for (int i = 0; i < 4; i++) {                      //for each row
            int row = BEVEL + i*(BEVEL+Card.CARD_H);
            int rankCounter = 1;
            //get the first card in the row (ace)
            Component firstInRow = getComponentAt(BEVEL, row);
            if(firstInRow instanceof Card){
                //get the suit for this row
                String rowSuit = ((Card) firstInRow).getSuit();
                Component current =                          //get the current card
                        getComponentAt(BEVEL + rankCounter*(BEVEL+Card.CARD_W), row);
                while(  current instanceof Card &&              
                        //make sure the card has the right suit and correct rank
                        ((Card) current).getSuit().equals(rowSuit) &&
                        ((Card) current).getRank() == rankCounter
                     ) {
                    rankCounter++;
                    current = getComponentAt(BEVEL +
                            rankCounter*(BEVEL+Card.CARD_W), row);
                }
                //first incorrect card is the new free spot for that row
                newFreeSpots.add(current.getBounds());
            } else newFreeSpots.add(firstInRow.getBounds()); //first spot is blank
            //cards in the remainder of that row are to be shuffled
            for (int x = rankCounter; x < 14; x++) {
                Component c = getComponentAt(BEVEL + x*(BEVEL+Card.CARD_W), row);
                if(c instanceof Card) {
                    toShuffle.add((Card) c);
                    openSpots.add(c.getBounds());
                    remove(c);
                }
            }
        }


        openSpots.addAll(grays);
        openSpots.removeAll(newFreeSpots);
        grays.clear();
        grays.addAll(newFreeSpots);
        Collections.shuffle(toShuffle);
        //put the cards back in a shuffled order
        for(int i = 0; i < toShuffle.size(); i++) {
            toShuffle.get(i).setBounds(openSpots.get(i));
            add(toShuffle.get(i));
            setComponentZOrder(toShuffle.get(i), 0);
        }
        repaint();
        shufflesAllowed--;
        checkUndoRedo();
    }

    /**
     * Displays the 'About' message window
     */
    public void displayAbout() {
        showMessage("Project5:\n\n"+
            "A new structure for Carpet Solitaire game\n"+
            "Version 1.0 by Zhongshan Lu <zlv@uwyo.edu>", "About");
    }

    /**
     * Displays the 'Rules' message window
     */
    public void displayRules() {
        showMessage("Rules:\n\n"+
            "Arrange the cards from left to right, Ace to King.\n"+
            "Each row corresponds to a unique suit. Thus,\n" +
            "only Aces can be in the leftmost column.", "Rules");
    }

    /**
     * Displays the 'Statistics' message window
     */
    public void displayStats() {
        JButton[] buttons = {new JButton("Clear"), new JButton("OK")};
        final JDialog dialog = new JDialog(GUI, "Click a button");
        final JOptionPane optionPane = new JOptionPane("Statistics:\n\n"+
            "Games Played: "+ played +"\n"+
            "Games Won: "+ won +" ("+getPercentWon()+"%)",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[1]);

        buttons[0].addActionListener(new ActionListener()
        { public void actionPerformed(ActionEvent e) {
            played = 0;
            won = 0;
            optionPane.setMessage("Statistics:\n\n"+ "Games Played: "+
                    played +"\n"+"Games Won: "+ won +" ("+getPercentWon()+"%)");}});

        buttons[1].addActionListener(new ActionListener()
        { public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false); } });

        dialog.setContentPane(optionPane);
        dialog.pack();
        dialog.setLocationRelativeTo(GUI);
        dialog.setVisible(true);
    }
}