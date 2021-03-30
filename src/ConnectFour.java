import java.util.Scanner;
import java.util.Arrays; 
import java.util.HashMap; 

public class ConnectFour {

    public static void main(String[] args) {
        Board boardOne = new Board(7, 6); 
        player_piece playerOne = new player_piece("x", "player one");
        player_piece playerTwo = new player_piece("o", "player two");
        HashMap<String, String> dict = new HashMap<>(); 
        dict.put(playerOne.toString(), playerOne.getUsername()); 
        dict.put(playerTwo.toString(), playerTwo.getUsername()); 
        String winner = boardOne.play(playerOne, playerTwo); 
        boardOne.showBoard(); 
        System.out.println(dict.get(winner) + " has won");
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

        public String play(player_piece playerOne, player_piece playerTwo) {
            Scanner scanner = new Scanner(System.in); 
            int turn = 0; 
            while(this.gameWon() == null) {
                showBoard(); 
                if (turn % 2 == 0) {
                    System.out.println("player ones turn" + "\ngive a colum between one and seven"); 
                    int inputColum = scanner.nextInt(); 
                    while(!(inputColum >= 1 && inputColum <= 7)) {
                        System.out.println("give a number between 1 and 7");
                        inputColum = scanner.nextInt(); 
                    }
                    if(addPiece(inputColum, playerOne, false)) {
                        addPiece(inputColum, playerOne, true);
                        turn++; 
                    } else {
                        System.out.println("that row is full"); 
                    }
                } else {
                    System.out.println("player twos turn" + "\ngive a colum between one and seven"); 
                    int inputColum = scanner.nextInt(); 
                    while(!(inputColum >= 1 && inputColum <= 7)) {
                        System.out.println("give a number between 1 and 7");
                        inputColum = scanner.nextInt(); 
                    }
                    if(addPiece(inputColum, playerTwo, false)) {
                        addPiece(inputColum, playerTwo, true);
                        turn++; 
                    } else {
                        System.out.println("that row is full"); 
                    }
                }
            }
            return this.gameWon(); 
        }

        public String gameWon() {
            for(int i = 0; i < 7; i++) {
                if (numC[i] >= 4) {
                    String last = null; 
                    int count = 1; 
                    int largestCount = 0; 
                    for(String str: this.board[i]) {
                        if (count > largestCount) { largestCount = count; }
                        if (str == null) {
                            count = 1; 
                        } else {
                            if (str.equals(last)) {
                                count++; 
                            } else {
                                count = 1; 
                            }
                        }
                        last = str; 
                        if (count >= 4) { return str; } 
                    }
                    numC[i] = largestCount; 
                }
            }
            for(int i = 0; i < 6; i ++) {
                if (numR[i] >= 4) { 
                    String last = null; 
                    int count = 1;
                    int largestCount = 0; 
                    for(int b = 0; b < 7; b ++) {
                        String str = this.board[b][i];
                        if (count > largestCount) { largestCount = count; } 
                        if(str == null) { 
                            count = 1; 
                        } else {
                            if (str.equals(last)) {
                                count++; 
                            } else {
                                count = 1; 
                            }
                        }
                        last = str; 
                        if (count >= 4) {  return str;  }
                    }
                    numR[i] = largestCount; 
                }
            }
            for(int c = -2; c <= 3; c++) {
                String last = null; 
                int count = 1; 
                for(int i = 0; i < 6; i++ ) {
                    if (i + c < 0 || i + c > 6) { continue; }
                    String str = board[c + i][i];
                    if (str == null) {
                        count = 1; 
                    } else {
                        if(str.equals(last)) {
                            count++; 
                        } else {
                            count = 1; 
                        }
                    }
                    last = str; 
                    if(count >= 4) { return str; }
                }
            }
            for(int c = 3; c <= 8; c++) {
                String last = null; 
                int count = 1; 
                for(int i = 0; i < 6; i ++) {
                    if (c - i < 0 || c - i > 6) { continue; }
                    String str = board[c - i][i];
                    if (str == null) {
                        count = 1; 
                    } else {
                        if (str.equals(last)) {
                            count++; 
                        } else {
                            count = 1; 
                        }
                    }
                    last = str; 
                    if (count >= 4) { return str; }
                }
            }
            return null;
        }

        public void showBoard() {
            for(int i = 0; i < 6; i++) {
                for(int j = 0; j < 7; j++) {
                    if(board[j][i] != null) {
                        System.out.print(board[j][i] + "    "); 
                    } else {
                        System.out.print(board[j][i] + " "); 
                    }
                }
                System.out.println(""); 
            }
        }

        public boolean addPiece(int colum, player_piece player, boolean add) {
            colum--;
            for(int i = board[colum].length - 1; i >= 0; i--) {
                if (board[colum][i] == null) {
                    if(!add) {
                        return true; 
                    }
                    board[colum][i] = player.toString(); 
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
        private final String piece, color_code, username; 
        public player_piece(String piece, String username) {
            this.piece = piece; 
            this.username = username; 
            if (player_count == 0) {
                this.color_code = "\u001B[31m";
            } else {
                this.color_code = "\u001B[34m"; 
            }
            player_count++; 
        }

        public String getUsername() { return this.username; }

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