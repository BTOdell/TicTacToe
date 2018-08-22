package tictactoe;

public interface ICell {
	public int getX();
	public int getY();
	public Token getToken();
	public void addListener(CellChangeListener listener);
	
	public interface CellChangeListener {
		public void onChanged(ICell cell);
	}
}
