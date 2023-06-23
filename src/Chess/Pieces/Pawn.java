package Chess.Pieces;

import Board.Board;
import Board.Position;
import Chess.ChessPiece;
import Chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];		
		Position p = new Position(0, 0);
		final int movimentoParaCima = super.getColor() == Color.WHITE ? -1 : 1;
		//final int movimentoParaBaixo = super.getColor() == Color.WHITE ? 1 : -1;
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
		return mat;
	}
	
	
	@Override
	public String toString() {
		return "P";
	}
}
