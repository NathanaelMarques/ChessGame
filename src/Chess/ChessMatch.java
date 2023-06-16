package Chess;

import Board.Board;
import Board.Piece;
import Board.Position;
import Chess.Pieces.King;
import Chess.Pieces.Rook;

public class ChessMatch {
	private Board board;
	
	public ChessMatch() {
		board = new Board(8,8);
		initialSetup();
	}
	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getRows()];
		for(int i = 0; i<board.getRows();i++) {
			for(int j=0; j<board.getColumns();j++) {
				mat[i][j] = (ChessPiece)board.piece(i, j);
			}
		}
		return mat;
	}
	
	public ChessPiece performChessMove(ChessPosition origin, ChessPosition destination) {
		validator(origin.toPosition());
		Piece capturedPiece = makeMove(origin.toPosition(), destination.toPosition());
		return (ChessPiece)capturedPiece;
	}
	private void validator(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("Não tem peça nessa posição");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("Não tem movimentos possíveis para essa peça");
		}
	}
	private Piece makeMove(Position origin, Position destination) {
		Piece p = board.removePiece(origin);
		Piece capturedPiece = board.removePiece(destination);
		board.placePiece(p, destination);
		return capturedPiece;
	}
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	private void initialSetup() {
		placeNewPiece('e', 2, new King(board, Color.BLACK));
		placeNewPiece('h', 8, new King(board, Color.WHITE));
		placeNewPiece('a', 1, new Rook(board, Color.BLACK));
	}
}
