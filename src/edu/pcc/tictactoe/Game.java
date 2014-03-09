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
	
	public boolean move(Player player, ICell cell) {
		return move(player, cell.getX(), cell.getY());
	}
	
	public boolean move(Player player, int x, int y) {
		if (hasFocus(player)) {
			final Cell cell = cells[x][y];
			cell.setToken(player == xPlayer ? Token.X : Token.O);
			moves++;
			ICell[] winCells = win(cell);
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
	
	public ICell[] win(ICell cell) {
		final Token targetToken = cell.getToken();
		final ICell[] output = new ICell[dimension];
		columns: {
			for (int i = 0; i < dimension; i++) {
				final Cell c = cells[cell.getX()][i];
				if (c.token != targetToken) {
					break columns;
				}
				output[i] = c;
			}
			return output;
		}
		rows: {
			for (int i = 0; i < dimension; i++) {
				final Cell c = cells[i][cell.getY()];
				if (c.token != targetToken) {
					break rows;
				}
				output[i] = c;
			}
			return output;
		}
		if (cell.getX() == cell.getY()) {
			diagonal: {
				for (int i = 0; i < dimension; i++) {
					final Cell c = cells[i][i];
					if (c.token != targetToken) {
						break diagonal;
					}
					output[i] = c;
				}
				return output;
			}
		}
		final int lessDim = dimension - 1; // equals 2 for a 3x3
		if (cell.getX() + cell.getY() == lessDim) {
			antiDiagonal: {
				for (int i = 0; i < dimension; i++) {
					final Cell c = cells[i][lessDim - i];
					if (c.token != targetToken) {
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
