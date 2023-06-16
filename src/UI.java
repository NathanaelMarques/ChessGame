import java.util.Scanner;

import Chess.ChessPiece;
import Chess.ChessPosition;

public class UI {
	
	public static void clearScreen() {
		System.out.println("\033[H\033[2J");
		System.out.flush();
	}
	
	public static ChessPosition readChessPosition(Scanner sc) {
		String input = sc.nextLine();
		char column = input.charAt(0);
		int row = Integer.parseInt(input.substring(1));
		return new ChessPosition(column, row);
	}
	
	public static void printBoard(ChessPiece[][] pieces) {
		for(int i = 0; i<pieces.length ;i++) {
			System.out.print((8 - i) + " ");
			for(int j=0; j<pieces.length ;j++) {
				printPiece(pieces[i][j]);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}
	private static void printPiece(ChessPiece piece) {
		if(piece == null) {
			System.out.print("-");
		}
		else {
			System.out.print(piece);
		}
		System.out.print(" ");
	}
}
