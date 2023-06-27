package Chess;

import Board.Board;
import Board.Piece;
import Board.Position;
import Chess.Pieces.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
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
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	public ChessPiece getPromoted(){
		return promoted;
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
		ChessPiece movedPiece = (ChessPiece) board.piece(destination.toPosition());
		promoted = null;
		if(movedPiece instanceof Pawn && ((movedPiece.getColor() == Color.WHITE && destination.toPosition().getRow() == 0)
				||
				(movedPiece.getColor() == Color.BLACK && destination.toPosition().getRow() == 7))){
				promoted = (ChessPiece) board.piece(destination.toPosition());
				promoted = replacePromotedPiece("Q");

		}
		check =  (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}else {
		nextTurn();
		}
		
		if(movedPiece instanceof Pawn && (destination.toPosition().getRow() == origin.toPosition().getRow() - 2
				|| destination.toPosition().getRow() == origin.toPosition().getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}else {
			enPassantVulnerable = null;
		}
		return (ChessPiece)capturedPiece;
	}

	public ChessPiece replacePromotedPiece(String type){
		if(promoted == null){
			throw new IllegalStateException("Não tem peça a ser promovida");
		}
		if(!type.equals("B") && !type.equals("C") && !type.equals("R") && !type.equals("Q")){
			return promoted;
		}

		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove((ChessPiece) p);

		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		return newPiece;
	}

	private ChessPiece newPiece(String type, Color color){
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("C")) return new Knight(board, color);
		if(type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
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

		if(p instanceof  King && destination.getColumn() == origin.getColumn() + 2) {
			Position sourceT = new Position(origin.getRow(), origin.getColumn() + 3); // posição de origem Rook
			Position targetT = new Position(origin.getRow(), origin.getColumn() + 1); // posição de destino Rook
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		if(p instanceof  King && destination.getColumn() == origin.getColumn() - 2) {
			Position sourceT = new Position(origin.getRow(), origin.getColumn() - 4); // posição de origem Rook
			Position targetT = new Position(origin.getRow(), origin.getColumn() - 1); // posição de destino Rook
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		if(p instanceof Pawn && (origin.getColumn() != destination.getColumn() && capturedPiece == null)) {
				Position pawnPosition = p.getColor() == Color.WHITE
						? new Position(destination.getRow() + 1, destination.getColumn())
						: new Position(destination.getRow() - 1, destination.getColumn());
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add((ChessPiece)capturedPiece);
				piecesOnTheBoard.remove((ChessPiece)capturedPiece);
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

		if(p instanceof  King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3); // posição de origem Rook
			Position targetT = new Position(source.getRow(), source.getColumn() + 1); // posição de destino Rook
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}

		if(p instanceof  King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4); // posição de origem Rook
			Position targetT = new Position(source.getRow(), source.getColumn() - 1); // posição de destino Rook
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		if(p instanceof Pawn && (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable)) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition = p.getColor() == Color.WHITE
						? new Position(3, target.getColumn())
						: new Position(4, target.getColumn());
				board.placePiece(pawn, pawnPosition);

		}
	}
	
	private Color opponent(Color color) {
		return color == Color.WHITE ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		return piecesOnTheBoard.stream()
				.filter(x -> x.getColor() == color && x instanceof King)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Não era pra isso acontecer"));
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
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i=0; i<board.getRows(); i++) {
				for (int j=0; j<board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
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
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));

		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		// Peças pretas
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));

		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
}
