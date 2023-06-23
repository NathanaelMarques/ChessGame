package Chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Board.Board;
import Board.Piece;
import Board.Position;
import Chess.Pieces.Bishop;
import Chess.Pieces.King;
import Chess.Pieces.Knight;
import Chess.Pieces.Pawn;
import Chess.Pieces.Queen;
import Chess.Pieces.Rook;

public class ChessMatch {
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	
	private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
	private List<ChessPiece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8,8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	

	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
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
	public boolean[][] possibleMoves(ChessPosition p){
		Position position = p.toPosition();
		validateSource(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition origin, ChessPosition destination) {
		validateDestination(origin.toPosition(), destination.toPosition());
		Piece capturedPiece = makeMove(origin.toPosition(), destination.toPosition());
		
		if(testCheck(currentPlayer)) {
			undoMove(origin.toPosition(), destination.toPosition(), capturedPiece);
			throw new ChessException("Se colocou que em check");
		}
		check = testCheck(opponent(currentPlayer)) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		//	throw new ChessException("CHECKMATE!");
		}else {
		nextTurn();
		}
		return (ChessPiece)capturedPiece;
	}
	
	private void validateSource(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("Não tem peça nessa posição");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("Essa peça não é sua");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("Não tem movimentos possíveis para essa peça");
		}
	}
	private void validateDestination(Position source, Position target) {	
		ChessPiece originPiece = (ChessPiece)board.piece(source);
		ChessPiece destinationPiece = (ChessPiece)board.piece(target);
		
		if(originPiece != null && destinationPiece != null) {
			if(originPiece.getColor() == destinationPiece.getColor()) {
				throw new ChessException("Não é possível capturar peça aliada");
			}
		}
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode se mover até o destino informado");
		}
	}

	private Piece makeMove(Position origin, Position destination) {
		ChessPiece p = (ChessPiece)board.removePiece(origin);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(destination);
		board.placePiece(p, destination);
		if(capturedPiece != null) {
			piecesOnTheBoard.remove((ChessPiece)capturedPiece);
			capturedPieces.add((ChessPiece)capturedPiece);
			
		}
		return capturedPiece;
	}
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove((ChessPiece)capturedPiece);
			piecesOnTheBoard.add((ChessPiece)capturedPiece);
		}
	}
	
	private Color opponent(Color color) {
		return color == Color.WHITE ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		ChessPiece king = (ChessPiece)piecesOnTheBoard.stream()
				.filter(x -> x.getColor() == color && x instanceof King)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Não era pra isso acontecer"));
		return king;
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		return piecesOnTheBoard
				.stream()
				.filter(x -> x.getColor() == opponent(color))
				.anyMatch(enemy -> {
					boolean[][] mat = enemy.possibleMoves();
					return mat[kingPosition.getRow()][kingPosition.getColumn()];
					});
	}
	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		List<ChessPiece> pecas = piecesOnTheBoard.stream() // metodo não permite a iteração e modificação da lista diretamente portanto se fez necessário o uso de uma lista auxiliar
				.filter(x -> x.getColor() == color)
				.collect(Collectors.toList());
		return pecas.stream()
				.anyMatch(piece -> {
					boolean[][] mat = piece.possibleMoves();
					for(int i = 0; i < mat.length; i++) {
						for (int y = 0; y < mat[i].length; y++) {
							if(mat[i][y]) {
								Position source = piece.getChessPosition().toPosition();
								Position target = new Position (i,y);
								Piece capturedPiece = makeMove(source, target);
								undoMove(source, target, capturedPiece);
								return testCheck(piece.getColor());
							}
						}
					}
					return true;
				});
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE);
	}
	
	private void initialSetup() {
		// Peças brancas
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));

		placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

		// Peças pretas
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));

		placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK));
	}
}
