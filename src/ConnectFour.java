import java.util.Scanner;
import java.util.HashMap;
import java.util.InputMismatchException; 

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
        private boolean gameOver = false; 

        public Board(int colums, int rows) {
            this.rows = rows; 
            this.colums = colums; 
            board = new String[colums][rows]; 
            for(int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    board[x][y] = null; 
                }
            }

            /**
             * numC and numR keep track of the marks in each row and colum
             * we only need to check for a win if there are 4 marks in a colum
             */
            this.numC = new int[this.colums];
            this.numR = new int[this.rows]; 
        }

        /**
         * function to cleanly get the input of the player
         * @param lowerBound - lowest number the person can give inclusive
         * @param upperBound - highest number the person can give incluseive
         * @return the number, making sure its within the bounds and an int
         */
        private int getIntInput(int lowerBound, int upperBound) {
            int num;  
            Scanner scan; 
            while(true) {
                try {
                    scan = new Scanner(System.in);
                    num = scan.nextInt();
                    if (num >= lowerBound && num <= upperBound) {
                        break; 
                    } else {
                        System.out.println(num + " is not between " + lowerBound + " and " +
                            upperBound + ", please try again"); 
                    }
                } catch (InputMismatchException e) {
                    System.out.println("not a valid input, try again");
                    num = 0; 
                }
            }
            return num; 
        }
        /**
         * function that plays the game
         * @param playerOne takes in the first player
         * @param playerTwo takes in the second player
         * @return the piece(String) of the player who won 
         */
        public String play(player_piece playerOne, player_piece playerTwo) {
            int turn = 0; 
            while(this.gameWon() == null) {
                this.showBoard(); 
                if (turn % 2 == 0) {
                    System.out.println("player ones turn" + "\ngive a colum between one and seven"); 
                    int inputColum = getIntInput(0, 7); 
                    if(addPiece(inputColum, playerOne, false)) {
                        addPiece(inputColum, playerOne, true);
                        turn++; 
                    } else {
                        System.out.println("that row is full"); 
                    }
                } else {
                    System.out.println("player twos turn" + "\ngive a colum between one and seven"); 
                    int inputColum = getIntInput(0, 7); 
                    if(addPiece(inputColum, playerTwo, false)) {
                        addPiece(inputColum, playerTwo, true);
                        turn++; 
                    } else {
                        System.out.println("that row is full"); 
                    }
                }
            }
            //makes sure gameWon() wont return a null value then returns gameWon(), or the winner
            return this.gameWon(); 
        }

        /**
         * logic to check that the game is over
         * if the game is not over then we return null
         * if the game is over we return the piece of the player that won
         * seperate checks for rows, colum, and diagnoals
         * for rows and colums we only check in the case that there are 4 pieces in that row or colum
         * we keep track of the amount in a row/colum with numR/numC
         * while keeping track of the rows and colums, we keep track of the largest straight run of pieces
         * the value for numC/numR is changed to the largest straight run of equal pieces
         * we do this because if we only have two straight pieces we dont want to check again untill 
         * 2 pieces have been added because until then we have no chance of a win
         * diagnal logic works through 2 different sets of nested for loops 
         * the different sets account for the two different directions a diagnal could have 
         * we "check" areas that are not actually on the 
         * @return the string of the piece that won
         */
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

        /**
         * shows the board as long as the game is not over, adds some spacing to each piece so that 
         * the board shows up clearly as well as adding colum labels at the top
         */
        public void showBoard() {
            if (gameOver) { 
                System.out.println("the board is completely full");
                return; 
            }
            System.out.print(" "); 
            for(int i = 1; i <= 7; i++) { System.out.print(i + "    "); }
            System.out.println(""); 
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

        /**
         * @return false if any single space on the board is open, returns true otherwise
         */
        private boolean compFull() {
            for(String[] col: board) {
                for(String str: col) {
                    if (str == null) { return false; }
                }
            }
            return true; 
        }
        /**
         * 
         */
        public boolean addPiece(int colum, player_piece player, boolean add) {
            //fixes the colum from what we give the player to what fits the array
            colum--;
            boolean checkOver = false; 
            for(int i = board[colum].length - 1; i >= 0; i--) {
                if (board[colum][i] == null) {
                    //if we said add is false, we don't actually add it we just say that we can
                    if(!add) {
                        return true; 
                    }
                    //if add is true we make it to this code and add the piece to the board
                    board[colum][i] = player.toString(); 
                    //change numC and numR to show that we added something to the row/colum
                    this.numC[colum]++;
                    this.numR[i]++;
                    /*
                    if we added it to the end of the colum don't return yet and note that we should 
                    check if the game is over
                    */
                    if (i == 0) { 
                        checkOver = true; 
                    } else {
                        return true; 
                    }
                }
            }
            //if and only if we just filled a colum we check if the board is completely full 
            if (checkOver) {
                if (compFull()) {
                    gameOver = true; 
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
            //allows a different color for the two players 
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
