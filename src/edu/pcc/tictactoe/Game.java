package edu.pcc.tictactoe;

import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Game {
	
	private final int dimension, dimensionSquared;
	private final Cell[][] cells;
	private int moves = 0;
	
	private Player xPlayer = null, oPlayer = null, currentPlayer = null;
	
	private CopyOnWriteArraySet<GameListener> listeners;
	
	public Game() {
		this(3);
	}
	
	public Game(final int dimension) {
		this.dimension = dimension;
		this.dimensionSquared = dimension * dimension;
		this.cells = new Cell[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				this.cells[i][j] = new Cell(i, j);
			}
		}
	}
	
	public final int getDimension() {
		return dimension;
	}
	
	public final ICell[][] getCells() {
		return cells;
	}
	
	public boolean register(Player player) {
		if (xPlayer == null) {
			xPlayer = player;
			currentPlayer = player;
		} else if (oPlayer == null) {
			oPlayer = player;
		} else {
			return false;
		}
		addListener(player);
		return true;
	}
	
	public void start() {
		
		//cells[0][1].setToken(Token.X);
		//cells[2][1].setToken(Token.O);
		//cells[0][0].setToken(Token.X);
		//cells[0][1].setToken(Token.X);
		//cells[1][1].setToken(Token.O);
		//cells[1][2].setToken(Token.O);
		//cells[2][2].setToken(Token.X);
		//cells[2][0].setToken(Token.O);
		//moves = 2;
		
		if (xPlayer != null) {
			xPlayer.prepare();
		}
		if (oPlayer != null) {
			oPlayer.prepare();
		}
		if (currentPlayer != null) {
			currentPlayer.turn();
		}
	}
	
	public Token getToken(Player player) {
		return player == xPlayer ? Token.X : player == oPlayer ? Token.O : null;
	}
	
	public boolean move(Player player, ICell cell) {
		return move(player, cell.getX(), cell.getY());
	}
	
	public boolean move(Player player, int x, int y) {
		if (hasFocus(player)) {
			final Cell cell = cells[x][y];
			cell.setToken(getToken(player));
			moves++;
			ICell[] winCells = win(cell, cells);
			if (winCells != null) {
				// player won!
				System.out.println("YOU WON!");
				fireGameOver(player, winCells);
			} else if (moves == dimensionSquared) {
				// cats game!
				System.out.println("Cats GAME!!!");
				fireGameOver(null, null);
			} else {
				return true;
			}
			// end game!
			
		}
		return false;
	}
	
	public static ICell[] win(ICell cell, ICell[][] cells) {
		final Token targetToken = cell.getToken();
		final ICell[] output = new ICell[cells.length];
		columns: {
			for (int i = 0; i < cells.length; i++) {
				final ICell c = cells[cell.getX()][i];
				if (c.getToken() != targetToken) {
					break columns;
				}
				output[i] = c;
			}
			return output;
		}
		rows: {
			for (int i = 0; i < cells.length; i++) {
				final ICell c = cells[i][cell.getY()];
				if (c.getToken() != targetToken) {
					break rows;
				}
				output[i] = c;
			}
			return output;
		}
		if (cell.getX() == cell.getY()) {
			diagonal: {
				for (int i = 0; i < cells.length; i++) {
					final ICell c = cells[i][i];
					if (c.getToken() != targetToken) {
						break diagonal;
					}
					output[i] = c;
				}
				return output;
			}
		}
		final int lessDim = cells.length - 1; // equals 2 for a 3x3
		if (cell.getX() + cell.getY() == lessDim) {
			antiDiagonal: {
				for (int i = 0; i < cells.length; i++) {
					final ICell c = cells[i][lessDim - i];
					if (c.getToken() != targetToken) {
						break antiDiagonal;
					}
					output[i] = c;
				}
				return output;
			}
		}
		return null;
	}
	
	public boolean turn() {
		if (currentPlayer == null || oPlayer == null) {
			return false;
		}
		currentPlayer = currentPlayer == xPlayer ? oPlayer : xPlayer;
		currentPlayer.turn();
		return true;
	}
	
	public boolean hasFocus(Player player) {
		return currentPlayer != null && currentPlayer == player;
	}
	
	public void clear() {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				cells[i][j].setToken(null);
			}
		}
		moves = 0;
	}
	
	public void addListener(GameListener listener) {
		if (listeners == null) {
			listeners = new CopyOnWriteArraySet<GameListener>();
		}
		listeners.add(listener);
	}
	
	private void fireGameOver(Player winner, ICell[] winCells) {
		for (GameListener listener : listeners) {
			listener.onGameOver(winner, winCells);
		}
	}
	
	public interface GameListener {
		public void onGameOver(Player winner, ICell[] winCells);
	}
	
	private class Cell implements ICell {
		
		private final int x, y;
		private Token token = null;
		private CopyOnWriteArraySet<CellChangeListener> listeners;
		
		public Cell(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int getX() {
			return x;
		}

		@Override
		public int getY() {
			return y;
		}
		
		@Override
		public Token getToken() {
			return token;
		}
		
		private void setToken(Token token) {
			if (this.token != token) {
				this.token = token;
				fireChanged();
			}
		}

		@Override
		public void addListener(CellChangeListener listener) {
			if (listeners == null) {
				listeners = new CopyOnWriteArraySet<CellChangeListener>();
			}
			listeners.add(listener);
		}
		
		private void fireChanged() {
			for (CellChangeListener list : listeners) {
				list.onChanged(this);
			}
		}

	}
	
}
