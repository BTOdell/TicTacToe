package edu.pcc.tictactoe;

import java.awt.EventQueue;

import javax.swing.UIManager;

public class TicTacToe extends Game {
	
	private GameWindow gui = null;
	
	private final HumanPlayer humanPlayer;
	
	public TicTacToe() {
		super(3);
		this.humanPlayer = new HumanPlayer();
	}

	@Override
	public void start() {
		register(humanPlayer);
		register(new AIPlayer(this, 1000));
		//register(new AIPlayer(this, 1000));
		
		gui = getGUI();
		addListener(gui);
		gui.setVisible(true);
		
		super.start();
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
						gui = new GameWindow(TicTacToe.this, humanPlayer);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return gui;
	}
	
}
