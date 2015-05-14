package com.bigdata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SwingContiner {
	private JFrame mainFrame;
	private JTextField textField;
	private JButton submitButton;
	private JTextArea resultArea;
	private JLabel exampleLable;

	public SwingContiner() {
		prepareGUI();
	}

	private void prepareGUI() {
		mainFrame = new JFrame("Assignment3");
		mainFrame.setSize(600, 1200);
		mainFrame.setLayout(null);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		exampleLable = new JLabel();
		exampleLable.setText("eg. the  or \"the\" and \"are\"");
		exampleLable.setBounds(150, 20, 300, 30);

		textField = new JTextField("");
		textField.setBounds(150, 50, 300, 40);

		submitButton = new JButton("Query");
		submitButton.setBounds(250, 120, 100, 40);
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String txt = textField.getText();
				if (!txt.equals("")) {

					String singleCharacter = "(\")"; // Any Single Character "
					String wordStr = "((?:[a-z0-9][a-z0-9]+))"; // Word
					String whiteSpace = "(\\s+)"; // White Space
					String andString = "(and)"; // Word and

					Pattern p = Pattern.compile(singleCharacter + wordStr + singleCharacter + whiteSpace + andString
							+ whiteSpace + singleCharacter + wordStr + singleCharacter, Pattern.CASE_INSENSITIVE
							| Pattern.DOTALL);
					Matcher m = p.matcher(txt);
					if (m.find()) {
						String word1 = m.group(2);
						String word2 = m.group(8);
						resultArea.setText(String.valueOf(Hbase.getData(word1, word2))+" times");
					}
					else {
						resultArea.setText(Hbase.getData(txt));
					}

					
				}
			}
		});

		resultArea = new JTextArea("");
		resultArea.setBounds(100, 200, 400, 800);
		resultArea.setLineWrap(true);
		resultArea.setWrapStyleWord(true);

		mainFrame.add(exampleLable);
		mainFrame.add(textField);
		mainFrame.add(submitButton);
		mainFrame.add(resultArea);
		mainFrame.setVisible(true);
	}
}