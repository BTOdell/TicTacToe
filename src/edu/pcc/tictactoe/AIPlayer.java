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
		LinkedList<ICell> cellList = new LinkedList<ICell>();
		ICell[][] cells = game.getCells();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				ICell cell = cells[i][j];
				if (cell.getToken() == null) {
					cellList.add(cell);
				}
			}
		}
		if (cellList.size() > 0) {
			return cellList.get(new Random().nextInt(cellList.size()));
		}
		return null;
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
	
}
