package edu.pcc.tictactoe;

public abstract class Game {
	
	private final int dimension;
	private final Cell[][] cells;
	
	private Player xPlayer = null, oPlayer = null, currentPlayer = null;
	
	public Game() {
		this(3);
	}
	
	public Game(final int dimension) {
		this.dimension = dimension;
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
		return true;
	}
	
	public boolean mark(Player player, int x, int y) {
		if (hasFocus(player)) {
			cells[x][y].token = player == xPlayer ? Token.X : Token.O;
			return true;
		}
		return false;
	}
	
	public boolean hasFocus(Player player) {
		return currentPlayer != null && currentPlayer == player;
	}
	
	public abstract void init() throws Exception;
	
	public abstract void start();
	
	private class Cell implements ICell {
		
		private final int x, y;
		private Token token = null;
		
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

	}
	
}
