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

public class GameWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final TicTacToe game;
	private final ICell[][] cells;
	
	private JPanel contentPane;
	
	private boolean turnX = true;
	
	private final MouseListener cellMouseListener = new MouseListener() {
		public void mouseClicked(MouseEvent e) {
			
		}
		public void mouseEntered(MouseEvent e) {
			final CellLabel cell = getCell(e);
			if (cell != null) {
				cell.hover(true);
			}
		}
		public void mouseExited(MouseEvent e) {
			final CellLabel cell = getCell(e);
			if (cell != null) {
				cell.hover(false);
			}
		}
		public void mousePressed(MouseEvent e) {
			final CellLabel cell = getCell(e);
			if (cell != null) {
				if (cell.getToken() == null) {
					//cell.setToken(turnX ? Token.X : Token.O);
					turnX = !turnX;
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
	
	public GameWindow(final TicTacToe game) {
		this.game = game;
		this.cells = new ICell[game.getDimension()][game.getDimension()];
		
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
		final ICell[][] internalCells = game.getCells();
		final int dim = game.getDimension();
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				final CellLabel cellLabel = new CellLabel(internalCells[i][j]);
				cellLabel.addMouseListener(cellMouseListener);
				cells[i][j] = cellLabel;
				contentPane.add(cellLabel);
			}
		}
	}
	
	private static class CellLabel extends JLabel implements ICell {
		
		private static final long serialVersionUID = 1L;
		
		private static final Border BORDER = new MatteBorder(1, 1, 1, 1, Color.BLACK);
		private static final Font FONT = new Font("Trebuchet MS", Font.BOLD, 144);
		private static final Color DEFAULT_COLOR = Color.WHITE;
		private static final Color HOVER_COLOR = new Color(250, 250, 250);
		
		private final ICell cell;
		
		public CellLabel(ICell cell) {
			this.cell = cell;
			setBackground(DEFAULT_COLOR);
			setOpaque(true);
			setBorder(BORDER);
			setFont(FONT);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Token getToken() {
			return cell.getToken();
		}
		
		private void hover(boolean hovered) {
			setBackground(hovered ? HOVER_COLOR : DEFAULT_COLOR);
		}
		
	}
	
}
