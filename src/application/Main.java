package application;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Board.BoardException;
import Chess.ChessMatch;
import Chess.ChessPiece;
import Chess.ChessPosition;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		while(!chessMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch,captured);
				System.out.println();
				System.out.println("Source: ");
				ChessPosition source = UI.readChessPosition(sc);
				
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves);
				
				System.out.println();
				System.out.println("Target: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				if(capturedPiece!=null) {
					captured.add(capturedPiece);
				}
			}catch(InputMismatchException e) {
				System.out.println("Valores invalidos");
				sc.nextLine();
			}catch(BoardException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}catch(Exception e) {
				e.printStackTrace();
				sc.nextLine();
			}
		}	
		UI.clearScreen();
		UI.printMatch(chessMatch,captured);
	}
}
