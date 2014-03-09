package edu.pcc.tictactoe;

import edu.pcc.tictactoe.Game.GameListener;

public interface Player extends GameListener {
	public void prepare();
	public void turn();
}
