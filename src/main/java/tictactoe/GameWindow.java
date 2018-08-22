package tictactoe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class GameWindow extends JFrame implements Game.GameListener {

	private static final long serialVersionUID = 1L;
	
	private final TicTacToe game;
	private final HumanPlayer humanPlayer;
	private final CellLabel[][] cellLabels;
	
	private JPanel contentPane;
	
	private final MouseListener cellMouseListener = new MouseListener() {
		public void mouseClicked(MouseEvent e) {
			
		}
		public void mouseEntered(MouseEvent e) {
			if (humanPlayer == null || !game.hasFocus(humanPlayer)) {
				return;
			}
			final CellLabel cellLabel = getCell(e);
			if (cellLabel != null) {
				final ICell cell = cellLabel.getCell();
				if (cell.getToken() == null) {
					cellLabel.hover(true);					
				}
			}
		}
		public void mouseExited(MouseEvent e) {
			if (humanPlayer == null || !game.hasFocus(humanPlayer)) {
				return;
			}
			final CellLabel cellLabel = getCell(e);
			if (cellLabel != null) {
				final ICell cell = cellLabel.getCell();
				if (cell.getToken() == null) {					
					cellLabel.hover(false);
				}
			}
		}
		public void mousePressed(MouseEvent e) {
			if (humanPlayer == null || !game.hasFocus(humanPlayer)) {
				return;
			}
			final CellLabel cellLabel = getCell(e);
			if (cellLabel != null) {
				final ICell cell = cellLabel.getCell();
				if (cell.getToken() == null) {
					if (game.move(humanPlayer, cell)) {
						game.turn();
						cellLabel.hover(false);
					}
				}
			}
		}
		public void mouseReleased(MouseEvent e) {
			
		}
		private CellLabel getCell(MouseEvent e) {
			final Object src = e.getSource();
			if (src instanceof CellLabel) {
				return (CellLabel) src;
			}
			return null;
		}
	};
	
	public GameWindow(final TicTacToe game, final HumanPlayer human) {
		this.game = game;
		this.humanPlayer = human;
		this.cellLabels = new CellLabel[game.getDimension()][game.getDimension()];
		
		init();
	}

	final void init() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Tic-Tac-Toe: Artificial Intelligence");
		setSize(new Dimension(500, 500));
		setMinimumSize(getSize());
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new GridLayout(game.getDimension(), game.getDimension(), 0, 0));
		
		initCells();
		
		setContentPane(contentPane);

		pack();
		setLocationRelativeTo(null);
	}
	
	private void initCells() {
		final ICell[][] cells = game.getCells();
		final int dim = game.getDimension();
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				final ICell cell = cells[i][j];
				final CellLabel cellLabel = new CellLabel(cell);
				cellLabel.addMouseListener(cellMouseListener);
				cell.addListener(new ICell.CellChangeListener() {
					public void onChanged(final ICell cell) {
						cellLabel.setTokenText(cell.getToken());
						cellLabel.defaultColor();
					}
				});
				cellLabels[i][j] = cellLabel;
				contentPane.add(cellLabel);
			}
		}
	}
	
	@Override
	public void onGameOver(Player winner, ICell[] winCells) {
		exit: {
			if (winCells != null) {
				boolean humanWon = humanPlayer == null && winner == humanPlayer;
				for (ICell cell : winCells) {
					cellLabels[cell.getX()][cell.getY()].setBackground(humanWon ? Color.GREEN : Color.RED);
				}
				if (humanPlayer != null) {
					if (!gameOverMessage((humanWon ? "You won!" : "The computer won :(") + " Try again?", humanWon ? JOptionPane.PLAIN_MESSAGE : JOptionPane.ERROR_MESSAGE)) {
						break exit;
					}
				} else {
					Token token = game.getToken(winner);
					if (!gameOverMessage(token + " won! Try again?", JOptionPane.INFORMATION_MESSAGE)) {
						break exit;
					}
				}
			} else { // cats game
				for (int i = 0; i < cellLabels.length; i++) {
					for (int j = 0; j < cellLabels[0].length; j++) {					
						cellLabels[i][j].setBackground(Color.YELLOW);
					}
				}
				if (!gameOverMessage("Cat's game! Try again?", JOptionPane.WARNING_MESSAGE)) {
					break exit;
				}
			}
			game.clear();
			game.start();
			return;
		}
		dispose();
	}
	
	private boolean gameOverMessage(String message, int messageType) {
		return gameMessage("Game over", message, messageType);
	}
	
	public boolean gameMessage(String title, String message, int messageType) {
		game.unregister();
		final int option = JOptionPane.showOptionDialog(GameWindow.this, message, title, JOptionPane.YES_NO_OPTION, messageType, null, new Object[] { "First", "Second", "AI vs AI", "Exit" }, "First");
		switch (option) {
			case JOptionPane.CLOSED_OPTION:
			case 3: // exit
				return false;
			case 0:
				// switch to first player
				game.register(game.getHumanPlayer());
				game.register(game.getAIPlayer1());
				break;
			case 1:
				// switch to second player
				game.register(game.getAIPlayer1());
				game.register(game.getHumanPlayer());
				break;
			case 2: // ai vs ai
				game.register(game.getAIPlayer1());
				game.register(game.getAIPlayer2());
				break;
		}
		return true;
	}
	
	private static class CellLabel extends JLabel {
		
		private static final long serialVersionUID = 1L;
		
		private static final Border BORDER = new MatteBorder(1, 1, 1, 1, Color.BLACK);
		private static final Font FONT = new Font("Trebuchet MS", Font.BOLD, 144);
		private static final Color DEFAULT_COLOR = Color.WHITE;
		private static final Color HOVER_COLOR = new Color(240, 240, 240);
		
		private final ICell cell;
		
		public CellLabel(ICell cell) {
			this.cell = cell;
			setBackground(DEFAULT_COLOR);
			setOpaque(true);
			setBorder(BORDER);
			setFont(FONT);
			setHorizontalAlignment(SwingConstants.CENTER);
			setTokenText(cell.getToken());
		}
		
		private ICell getCell() {
			return cell;
		}
		
		private void setTokenText(Token token) {
			setText(token != null ? token.toString() : null);
		}
		
		private void hover(boolean hovered) {
			setBackground(hovered ? HOVER_COLOR : DEFAULT_COLOR);
		}
		
		private void defaultColor() {
			setBackground(DEFAULT_COLOR);
		}
		
	}
	
}
