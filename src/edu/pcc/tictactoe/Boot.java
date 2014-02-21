package edu.pcc.tictactoe;

public class Boot {

	public static void main(String[] args) {
		try {
			Game game = new TicTacToe();
			game.init();
			game.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
