package tictactoe;

import tictactoe.Game.GameListener;

public interface Player extends GameListener {
	public void prepare();
	public void turn();
}
