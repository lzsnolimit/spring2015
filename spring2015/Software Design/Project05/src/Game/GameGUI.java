package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameGUI extends JFrame {

	private JMenuItem undo;
	private JMenuItem redo;
	private Game myGame;
	Object[] options = { "Yes, I want to save", "No thank you" };

	public GameGUI(Game g) {
		myGame = g;
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		createMenuBar();
		setVisible(true);
		
		add(myGame);
		pack();
	}

	/**
	 * creates the menubar of the myGame game
	 */
	private void createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");
		// create each menu item, with a shortcut and a method to call (its
		// action)

		JMenuItem newgame = new JMenuItem("New Game");
		newgame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.newGame();
			}
		});

		JMenuItem replay = new JMenuItem("Replay");
		replay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.sameGame();

			}
		});

		JMenuItem shuffle = new JMenuItem("Shuffle");
		shuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.shuffleGame();
			}
		});

		JMenuItem saveas = new JMenuItem("Save As...");
		saveas.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
		saveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.saveGame();

			}
		});

		JMenuItem open = new JMenuItem("Open...");
		open.setAccelerator(KeyStroke.getKeyStroke("control O"));
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.openGame();

			}
		});

		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showOptionDialog(null,
						"Do you want to save your game?", "Dont do it!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, // do not use a
															// custom Icon
						options, // the titles of buttons
						options[0]); // default button title
				if (n == JOptionPane.YES_OPTION) {
					myGame.saveGame();
				}

				else
					System.exit(0);
			}
		});

		undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.undoMove();

			}
		});

		redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.redoMove();

			}
		});

		JMenuItem statistics = new JMenuItem("Statistics...");
		statistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.displayStats();
			}
		});

		JMenuItem about = new JMenuItem("About...");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.displayAbout();

			}
		});

		JMenuItem rules = new JMenuItem("Rules...");
		rules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame.displayRules();

			}
		});

		// populate the file menu
		file.add(newgame);
		file.add(replay);
		file.add(shuffle);
		file.add(saveas);
		file.add(open);
		file.add(quit);
		// populate the edit menu
		edit.add(undo);
		edit.add(redo);
		edit.add(statistics);
		// populate the help menu
		help.add(rules);
		help.add(about);
		// populate the menubar
		menubar.add(file);
		menubar.add(edit);
		menubar.add(help);

		setJMenuBar(menubar);
	}

	public void enableUndoMenu() {
		undo.setEnabled(true);
	}

	public void disableUndoMenu() {
		undo.setEnabled(false);
	}

	public void enableRedoMenu() {
		redo.setEnabled(true);
	}

	public void disableRedoMenu() {
		redo.setEnabled(false);
	}
}
