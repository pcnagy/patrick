import java.util.Scanner;

import org.graalvm.compiler.asm.amd64.AMD64Assembler.AddressDisplacementAnnotation; 

public class ConnectFour {

final static private String red = "\u001B[31m"; 

    public static void main(String[] args) {
        Board boardOne = new Board(7, 6); 
        player_piece playerOne = new player_piece("x");
        player_piece playerTwo = new player_piece("o");
        boardOne.play(playerOne, playerTwo); 
    }

    private static class Board {
        private int rows, colums; 
        private String[][] board; 
        private final int[] numC, numR; 

        public Board(int colums, int rows) {
            this.rows = rows; 
            this.colums = colums; 
            board = new String[colums][rows]; 
            for(int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    board[x][y] = null; 
                }
            }
            this.numC = new int[this.colums];
            this.numR = new int[this.rows]; 
        }

        public void play(player_piece playerOne, player_piece playerTwo) {
            Scannaer scan = new Scanner(System.in); 
            int turn = 0; 
            while(!gameWon) {
                showBoard(); 
                turn++; 
                if (turn % 2 != 0) {
                    System.out.println("player ones turn" + "\ngive a colum between one and seven"); 
                    int inputColum = scanner.nextInt(); 
                    if(addPiece(inputColum, playerOne)) {
                        addPiece(inputColum, playerOne);
                    } else {
                        System.out.println("that row is full"); 
                    }
                } else {
                    System.out.println("player twos turn" + "\ngive a colum between one and seven"); 
                    int inputColum = scanner.nextInt(); 
                    if(addPiece(inputColum, playerTwo)) {
                        addPiece(inputColum, playerTwo);
                    } else {
                        System.out.println("that row is full"); 
                    }
                }
            }
        }

        public boolean gameWon() {
            for(int i = 0; i < 7; i++) {
                if (numC[i] >= 4) {
                    String last = null; 
                    int count = 0; 
                    largestCount = 0; 
                    for(String str: this.board[i]) {
                        if (count >= 4) { System.out.println(str + " has won"); return true; }
                        if (count > largestCount) { largestCount = count; }
                        if (str == null) {
                            count = 0; 
                            last = null;
                        } else {
                            if (str == last) {
                                count++; 
                            } else {
                                last = str; 
                                count = 0; 
                            }
                        }
                    }
                }
                numC[i] = largestCount; 
            }
            for(int i = 0; i < 6; i ++) {
                if (numR[i] >= 4) { 
                    String last = null; 
                    int count = 0;
                    int largestCount = 0; 
                    for(int a = 0; a < 6; a++) {
                        for(int b = 0; b < 7; b ++) {
                            String str = this.board[b][a];
                            if (count >= 4) { System.out.println(str + " has won"); return true; }
                            if (count > largestCount) { largestCount = 0; } 
                            if(str == null) { count = 0; }
                            if (str == last) {
                                count++; 
                            } else {
                                last = str; 
                                count = 0; 
                            }
                        }
                    }
                    numR[i] = largestCount; 
                }
            }
            return false; 
        }

        public void showBoard() {
            for(int i = 0; i < 6; i++) {
                for(int j = 0; j < 7; j++) {
                    System.out.print(board[j][i] + " "); 
                }
                System.out.println(""); 
            }
            System.out.println(numC[2]); 
        }

        public boolean addPiece(int colum, player_piece player) {
            colum--;
            for(int i = board[colum].length - 1; i >= 0; i--) {
                if (board[colum][i] == null) {
                    board[colum][i] = "  " + player.toString() + " "; 
                    this.numC[colum]++;
                    this.numR[i]++; 
                    return true; 
                }
            }
            return false; 
        }
    }

    private static class player_piece {
        private static int player_count = 0; 
        private final String piece, color_code; 
        public player_piece(String piece) {
            this.piece = piece; 
            if (player_count == 0) {
                this.color_code = "\u001B[31m";
            } else {
                this.color_code = "\u001B[34m"; 
            }
            player_count++; 
        }
        public String toString() {
            return this.color_code + piece + "\u001B[0m";
        }
    }

}

/** 
 * 1a.   1b
 * 2a .  2b
 * 3a    3b
 */