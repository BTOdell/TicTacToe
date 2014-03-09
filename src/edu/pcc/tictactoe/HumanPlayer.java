package edu.pcc.tictactoe;

public class HumanPlayer implements Player {
	
	@Override
	public void prepare() {
		
	}
	
	@Override
	public void turn() {
		System.out.println("Human's turn!");
	}

	@Override
	public void onGameOver(Player winner, ICell[] winCells) {
		
	}
	
}
