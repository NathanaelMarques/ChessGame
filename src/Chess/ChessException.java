package Chess;

import Board.BoardException;

public class ChessException extends BoardException {
	private static final long serialVersionUID = 1L;
	
	public ChessException(String mensagem) {
		super(mensagem);
	}
}
