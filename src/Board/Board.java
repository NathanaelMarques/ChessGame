package Board;

public class Board {
	private int rows;
	private int columns;

	private Piece[][] pieces;
	
	
	public Board(int rows, int columns) {
		if(rows < 1 || columns < 1) {
			throw new BoardException("Falha ao criar tabuleiro.");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public Piece piece(int row, int column) {
		
		return pieces[row][column];
	}
	
	public Piece piece(Position position) {
		return piece(position.getRow(), position.getColumn());
	}
	
	public void placePiece(Piece piece, Position position) {
		thereIsAPiece(position);
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	public Piece removePiece(Position position) {
		positionExists(position);
		if(piece(position) == null) {
			return null;
		}
		Piece aux = piece(position);
		aux.position = null; //revisar depois
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}
	
	public boolean positionExists(int row, int column) {
		if(!(row >= 0 && row < rows && column >= 0 && column < columns)) {
			throw new BoardException("Posição inexistente");
		}
		return true;
	}
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(), position.getColumn());
	}
	
	public boolean thereIsAPiece(Position position) {
		if(positionExists(position) && piece(position) != null) {
			return true;
			//throw new BoardException("Lugar já ocupado");
		}
		return false;
	}
	
}
