package Chess.Pieces;

import Board.Board;
import Board.Position;
import Chess.ChessMatch;
import Chess.ChessPiece;
import Chess.Color;

public class Pawn extends ChessPiece {
	
	private ChessMatch chessMatch;
	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];		
		Position p = new Position(0, 0);
		final int movimentoParaCima = super.getColor() == Color.WHITE ? -1 : 1;
		final int movimentoParaLadoE = -1;
		final int movimentoParaLadoD = 1;
		
		p.setValues(position.getRow() + movimentoParaCima, position.getColumn());
		if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + (movimentoParaCima * 2), position.getColumn());
		Position p2 = new Position(position.getRow() + movimentoParaCima, position.getColumn());
		if(getBoard().positionExists(p) 
				&& getBoard().positionExists(p2) 
				&& !getBoard().thereIsAPiece(p) 
				&& !getBoard().thereIsAPiece(p2) 
				&& getMoveCount() == 0) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() + movimentoParaCima,position.getColumn() + movimentoParaLadoE);
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + movimentoParaCima,position.getColumn() + movimentoParaLadoD);
		if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		if ((this.getColor() == Color.WHITE && position.getRow() == 3) || (this.getColor() == Color.BLACK && position.getRow() == 4)) { //enpassant
			Position left = new Position (position.getRow(), position.getColumn() - 1);
			if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
				mat[left.getRow() + movimentoParaCima][left.getColumn()] = true;
			}
			Position right = new Position (position.getRow(), position.getColumn() + 1);
			if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
				mat[right.getRow() + movimentoParaCima][right.getColumn()] = true;
			}
		}
		return mat;
	}
	
	
	@Override
	public String toString() {
		return "P";
	}
}
