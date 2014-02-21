package edu.pcc.tictactoe;

import java.awt.EventQueue;

import javax.swing.UIManager;

public class TicTacToe extends Game {
	
	private GameWindow gui = null;
	
	public TicTacToe() {
		
	}
	
	public final void init() throws Exception {
		getGUI().setVisible(true);
	}
	
	@Override
	public boolean mark(Player player, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean hasFocus(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
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
						gui = new GameWindow(TicTacToe.this);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return gui;
	}
	
}
