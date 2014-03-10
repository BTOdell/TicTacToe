package edu.pcc.tictactoe;

import java.util.LinkedList;
import java.util.Random;

public class AIPlayer implements Player {
	
	private final Game game;
	private final long delay;
	
	private Thread workerThread = null;
	private final Runnable runnable = new Runnable() {
		public void run() {
			outer: while (running) {
				if (!turn) {
					synchronized (waitObj) {
						while (!turn) {
							try {
								waitObj.wait();
							} catch (InterruptedException e) {
								break outer;
							}
						}
					}
				}
				turn = false;
				if (running && game.hasFocus(AIPlayer.this)) { // we have focus, go go go!
					
					// process game
					
					System.out.println("AI is thinking...");
					
					long startTime = System.currentTimeMillis();
					
					ICell nextCell = process();
					
					long elapsed = System.currentTimeMillis() - startTime;
					long sleepTime = delay - elapsed;
					
					if (sleepTime > 0) {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if (nextCell != null) {
						game.move(AIPlayer.this, nextCell);
						game.turn();
					}
					
				}
			}
		}
	};
	private volatile boolean running = false, turn = false;
	private final Object waitObj = new Object();
	
	public AIPlayer(final Game game, final long delay) {
		this.game = game;
		this.delay = delay;
	}
	
	private ICell process() {
		Token myToken = game.getToken(this);
		AICell[][] currentCells = convert(game.getCells());
		LinkedList<AICell> moves = new LinkedList<AICell>();
		int moveCount = possibleMoves(currentCells, moves);
		ICell bestMove = null;
		switch (moveCount) {
			case 1:
				bestMove = moves.getFirst();
				break;
			default:
				LinkedList<AICell> bestMoves = new LinkedList<AICell>();
				Integer bestResult = null;
				outer:
				for (int i = 0; i < moveCount; i++) {
					final AICell move = moves.pollLast();
					int result = calculate(myToken, myToken, move, moves, currentCells);
					best: {
						if (bestResult == null) {
							break best;
						}
						if (result > bestResult) {
							bestMoves.clear();
						} else if (result < bestResult) {
							continue outer;
						}
					}
					bestMoves.add(move);
					bestResult = result;
				}
				bestMove = bestMoves.get(new Random().nextInt(bestMoves.size()));
			case 0:
		}
		return bestMove;
	}
	
	private AICell[][] convert(ICell[][] cells) {
		final AICell[][] aiCells = new AICell[cells.length][cells.length > 0 ? cells[0].length : 0];
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				aiCells[i][j] = new AICell(i, j, cells[i][j].getToken());
			}
		}
		return aiCells;
	}
	
	private int calculate(Token myToken, Token currToken, AICell move, LinkedList<AICell> moves, AICell[][] cells) {
		try {
			move.token = currToken;
			ICell[] winCells = Game.win(move, cells);
			if (winCells != null) {
				return 1;
			} else {
				int moveCount = possibleMoves(cells, moves);
				if (moveCount == 0) {
					return 0;
				} else {
					Integer bestResult = null;
					final Token nextToken = currToken == Token.X ? Token.O : Token.X;
					for (int i = 0; i < moveCount; i++) {
						final AICell nextMove = moves.pollLast();
						int result = calculate(myToken, nextToken, nextMove, moves, cells);
						if (bestResult == null || result > bestResult) {
							bestResult = result;
						}
					}
					return -bestResult;
				}
			}
		} finally {
			move.token = null;
		}
	}
	
	private int possibleMoves(AICell[][] cells, LinkedList<AICell> moves) {
		int count = 0;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				final AICell cell = cells[i][j];
				if (cell.getToken() == null) {
					moves.addLast(cell);
					count++;
				}
			}
		}
		return count;
	}
	
	@Override
	public void prepare() {
		if (workerThread != null) {
			running = false;
			workerThread.interrupt();
			try {
				workerThread.join(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		running = true;
		workerThread = new Thread(runnable);
		workerThread.setDaemon(true);
		workerThread.start();
	}

	@Override
	public void turn() {
		System.out.println("AI's turn!");
		turn = true;
		synchronized (waitObj) {
			waitObj.notify();
		}
	}

	@Override
	public void onGameOver(Player winner, ICell[] winCells) {
		running = false;
		synchronized (waitObj) {
			waitObj.notify();
		}
	}
	
	private class AICell implements ICell {
		
		public final int x, y;
		public Token token;
		
		public AICell(final int x, final int y, final Token token) {
			this.x = x;
			this.y = y;
			this.token = token;
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

		@Override
		public void addListener(CellChangeListener listener) {}
		
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
		
	}
	
}
