package tictactoe;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class TicTacToe extends Game {
	
	private GameWindow gui = null;
	
	private HumanPlayer humanPlayer;
	private AIPlayer aiPlayer1, aiPlayer2;
	
	public HumanPlayer getHumanPlayer() {
		if (humanPlayer == null) {
			humanPlayer = new HumanPlayer();
		}
		return humanPlayer;
	}
	
	public AIPlayer getAIPlayer1() {
		if (aiPlayer1 == null) {
			aiPlayer1 = new AIPlayer(this, 1000);
		}
		return aiPlayer1;
	}
	
	public AIPlayer getAIPlayer2() {
		if (aiPlayer2 == null) {
			aiPlayer2 = new AIPlayer(this, 1000);
		}
		return aiPlayer2;
	}
	
	@Override
	public boolean init() {
		gui = getGUI();
		addListener(gui);
		gui.setVisible(true);
		
		if (!gui.gameMessage("Game started", "Please choose a game mode.", JOptionPane.INFORMATION_MESSAGE)) {
			gui.dispose();
			return false;
		}
		return true;
	}
	
	// internal methods
	
	private GameWindow getGUI() {
		if (gui == null) {
			try {
				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						try {
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						} catch (Exception e) {
							e.printStackTrace();
						}
						gui = new GameWindow(TicTacToe.this, getHumanPlayer());
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return gui;
	}
	
}
