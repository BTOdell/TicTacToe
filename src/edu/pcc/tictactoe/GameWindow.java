package edu.pcc.tictactoe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import edu.pcc.tictactoe.Game.GameListener;
import edu.pcc.tictactoe.ICell.CellChangeListener;

public class GameWindow extends JFrame implements GameListener {

	private static final long serialVersionUID = 1L;
	
	private final TicTacToe game;
	private final HumanPlayer human;
	private final CellLabel[][] cellLabels;
	
	private JPanel contentPane;
	
	private final MouseListener cellMouseListener = new MouseListener() {
		public void mouseClicked(MouseEvent e) {
			
		}
		public void mouseEntered(MouseEvent e) {
			if (human == null || !game.hasFocus(human)) {
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
			if (human == null || !game.hasFocus(human)) {
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
			if (human == null || !game.hasFocus(human)) {
				return;
			}
			final CellLabel cellLabel = getCell(e);
			if (cellLabel != null) {
				final ICell cell = cellLabel.getCell();
				if (cell.getToken() == null) {
					if (game.move(human, cell)) {
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
		this.human = human;
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
				cell.addListener(new CellChangeListener() {
					public void onChanged(ICell cell) {
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
		if (winCells != null) {
			for (ICell cell : winCells) {
				cellLabels[cell.getX()][cell.getY()].setBackground(Color.GREEN);
			}
		} else { // cats game
			for (int i = 0; i < cellLabels.length; i++) {
				for (int j = 0; j < cellLabels[0].length; j++) {					
					cellLabels[i][j].setBackground(Color.YELLOW);
				}
			}
		}
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
