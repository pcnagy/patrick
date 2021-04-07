import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Arrays; 
import java.util.Random; 

public class ConnectFourAI {


    public static void main(String[] args) {
        Board board = new Board(); 
        board.play(board, 4);
    }

    /**
     * board class 
     * stores a board and the players as well as the bot and player moves 
     * the powering class of the connect four game
     */
    private static class Board {

        public String[][] board; /* array that keeps track of the board */
        public int[] numC, numR; /* arrays that keep track of the number of pieces in each colum or row respectively */
        public int[] availCols; /* array that holds the colum value of each available (nonfull) colum */
        public int numAvailCols; /* the number of available colums remaining */
        public Player player, bot; /* players of the game, one the actual human player one the bot */

        /**
         * constructor to create a new empty board
         */
        public Board() {
            this.board = new String[6][7]; 
            this.numC = new int[7]; 
            this.numR = new int[6]; 
            this.availCols = new int[7]; 
            this.numAvailCols = 7;
            this.player = new Player("player", "x"); 
            this.bot = new Player("bot", "o"); 
            for(int i = 0; i < 7; i++) {
                availCols[i] = i; 
            }
        }

        /**
         * copy constructor to deep copy a board
         */
        public Board(Board oldBoard) {
            this.board = new String[6][7]; 
            for(int r = 0; r < this.board.length; r++) {
                for(int c = 0; c < this.board[0].length; c++) {
                    this.board[r][c] = oldBoard.getBoard()[r][c]; 
                }
            }
            this.numC = new int[7]; 
            for(int i = 0; i < numC.length; i++) {
                this.numC[i] = oldBoard.getNumC()[i]; 
            }
            this.numR = new int[6]; 
            for(int i = 0; i < this.numR.length; i++) {
                this.numR[i] = oldBoard.getNumR()[i]; 
            }
            this.numAvailCols = oldBoard.getNumAvailCols(); 
            this.availCols = new int[this.numAvailCols]; 
            for(int i = 0; i < this.numAvailCols; i++) {
                this.availCols[i] = oldBoard.getAvailCols()[i]; 
            }
            this.player = oldBoard.getPlayer(); 
            this.bot = oldBoard.getBot(); 
        }
    
        /* getters to be used in the deep copy constructor */
        public String[][] getBoard() { return this.board; }
        public int[] getNumC() { return this.numC; }
        public int[] getNumR() { return this.numR; }
        public int[] getAvailCols() { return this.availCols; }
        public int getNumAvailCols() { return this.numAvailCols; }
        public Player getPlayer() { return this.player; }
        public Player getBot() { return this.bot; }
        

        /**
         * main function for playing game, player vs bot
         * every other iteration calls player 
         * prints the winner when the game ends
         * @param board the board that is being played 
         * @param moves the number of moves we want to check into the future 
         */
        public void play(Board board, int moves) {
            int count = 0; 
            boolean lastWasPlayer = false; 
            while(board.gameWon() == null) {
                board.showBoard(); 
                if (count % 2 == 1) {
                    board.addPlayerPiece();
                    lastWasPlayer = true; 
                } else {
                    board.addBotPiece(board, moves); 
                    lastWasPlayer = false; 
                }
                count++; 
            }
            board.showBoard(); 
            if(lastWasPlayer) {
                System.out.print("player "); 
            } else {
                System.out.print("bot "); 
            }
            System.out.println("has won"); 
        }

        /**
         * function for making the bot move
         * creates two main arrays: totals and cases 
         * each array has length based on the amount of possible moves
         * totals keeps track of the total points awarded to a move 
         * cases keeps track of the total outcomes that exist a certain time later from the move
         * both arrays are modified by the avgsArr recursive function
         * avgs is an array of same length as totals 
         * avgs = totals / cases -> the average points rewarded to the possible outcome of the move
         * the function then makes the move for the bot depending on the highest average value -> the move with the best average outcome
         * @param oldBoard the board that is being played
         * @param moves the amount of moves we want to check into the future
         */
        private void addBotPiece(Board oldBoard, int moves) {
            double[] totals = new double[numAvailCols];
            int[] cases= new int[numAvailCols];
            Board[] boards = new Board[numAvailCols]; 
            for(int i = 0; i < boards.length; i++) {
                boards[i] = new Board(oldBoard); 
            }
            for(int i = 0; i < boards.length; i++) {
                int col = oldBoard.availCols[i];  
                boards[i].add(bot.getPiece(), col); 
                avgsArr(boards[i], totals, cases, bot, player, 0, i, moves);
            }
            double[] avgs = new double[totals.length];
            for(int i = 0; i < avgs.length; i++) {
                avgs[i] = totals[i] / cases[i]; 
            }
            double highest = avgs[0]; 
            int highestPos = 0; 
            for(int i = 1; i < avgs.length; i++) {
                double avg = avgs[i]; 
                if(avg > highest) {
                    highest = avg; 
                    highestPos = i; 
                }
            }
            oldBoard.add(bot.getPiece(), oldBoard.availCols[highestPos]); 
        }


        /**
         * recursive function that modifies two arrays, one with the total weighted values and one with the total number of cases
         * modifies two arrays that it is passed rewarding each possible outcome point values 
         * @param board the board that is beeing played
         * @param totals array of the total weighted values for each move, pass an empty array of same deminsions to the remaining colums
         * @param cases array of number of possible outcomes, pass an empty array of same deminsions to totals 
         * @param bot pass a bot object of type player class
         * @param player pass a player object of type player class
         * @param index pass 0
         * @param orgMove pass the original move of the branch 
         * @param moves the amount of moves we want to check into the future
         */
        private void avgsArr(Board recBoard, double[] totals, int[] cases, Player bot, Player player, int index, int orgMove, int moves) {
            /**
             * checks if the game has been won 
             * if the bot has won rewards 100 points to totals 
             * if the bot has lost subtracts 100 points from totals 
             * checks how many turns are left 
             * adds the reward the number of open colums raised to the turns left power 
             * if a game is won we dont want to just say it's won once, otherwise the bot will not favor winning because it
             *      will get more points from winning in the future (the further we get the more cases we look at therefore the more
             *      points rewarded)
             * to fix this we raise to a power as indicated above to show that any possible future based on that move will still result 
             *      in the bot winning 
             * if the game is won we return because we don't care what else is added to the board, the game is over
             */
            if (recBoard.gameWon() != null) {
                int turnsLeft = 4 - index; 
                double addAmount = Math.pow(recBoard.numAvailCols, turnsLeft); 
                if (recBoard.gameWon() == bot.getPiece()) {
                    totals[orgMove] += addAmount * 100; 
                    cases[orgMove] += addAmount; 
                } else {
                    totals[orgMove] -= addAmount * 100; 
                    cases[orgMove] += addAmount; 
                }
                return; 
            }

            //if we are a certain amount of moves into the future check the state of the board, we have to limit scope to limit time
            if (index == 4 || recBoard.isFull()) {
                int[] consAm = {
                    0, /* number of bot 3 in a rows*/
                    0, /* number of player 3 in a rows*/
                    0, /* numer of bot 2 in a rows*/
                    0, /* number of player 2 in a rows*/
                    0, /* number of bot 1 spot*/
                    0 /* number of player 1 spot*/
                };
                for(int col = 0; col < 7; col++) {
                    String last = null; 
                    boolean nullBefore = false; 
                    int count = 0; 
                    for(int r = 0; r < 6; r++) {
                        String str = recBoard.board[r][col]; 
                        String next = "empty"; 
                        if (r != 5) {
                            next = recBoard.board[r + 1][col]; 
                        }
                        if (str == null) {
                            consAmCh(consAm, 2, count, last, nullBefore); 
                            count = 0;
                        } else {
                            if (count == 0) { count = 1; }
                            if (str.equals(last)) {
                                if (str.equals(next)) {
                                    continue; 
                                } else if (next == null) {
                                    continue; 
                                } else {
                                    consAmCh(consAm, 1, count, last, nullBefore); 
                                }
                            } else {
                                count = 1; 
                                if (last == null) {
                                    nullBefore = true; 
                                } else {
                                    nullBefore = false; 
                                }
                            }
                        }
                        last = str; 
                    }
                }
                for(int row = 0; row < 6; row++) {
                    String last = null; 
                    boolean nullBefore = false; 
                    int count = 0; 
                    for(int c = 0; c < 6; c++) {
                        String str = recBoard.board[row][c]; 
                        String next = "empty"; 
                        if (c != 7) {
                            next = recBoard.board[row][c+1]; 
                        }
                        if (str == null) {
                            consAmCh(consAm, 2, count, last, nullBefore);
                            count = 0; 
                        } else {
                            if (count == 0) { count = 1; }
                            if (str.equals(last)) {
                                count++; 
                                if (str.equals(next)) {
                                    continue; 
                                } else if (next == null) {
                                    continue; 
                                } else {
                                    consAmCh(consAm, 1, count, last, nullBefore);
                                }
                            } else {
                                count = 1; 
                                if (last == null) {
                                    nullBefore = true; 
                                } else {
                                    nullBefore = false; 
                                }
                            }
                        }
                        last = str; 
                    }
                }
                for(int c = -2; c <= 3; c++) {
                    String last = null; 
                    boolean nullBefore = false; 
                    int count = 0; 
                    for(int r = 0; r < 6; r++) {
                        if (c+r < 0 || c + r > 6) { continue; }
                        String str = recBoard.board[r][c+r]; 
                        String next = "empty"; 
                        if (r + c  < 6 && r + c >= 0) {
                            next = recBoard.board[r][c+r+1]; 
                        }
                        if(str == null) {
                            consAmCh(consAm, 2, count, last, nullBefore); 
                            count = 0;
                        } else {
                            if (count == 0) { count = 1; }
                            if (str.equals(last)) {
                                if (str.equals(next)) {
                                    continue; 
                                } else if (next == null) {
                                    continue;
                                } else {
                                    consAmCh(consAm, 1, count, last, nullBefore); 
                                }
                            } else {
                                count = 1; 
                                if (last == null) {
                                    nullBefore = true; 
                                } else {
                                    nullBefore = false; 
                                }
                            }
                        last = str; 
                        }
                    }
                }
                for (int c = 8; c >= 3; c--) {
                    String last = null; 
                    boolean nullBefore = false; 
                    int count = 0; 
                    for(int r = 0; r < 6; r++) {
                        if (c - r < 0 || c - r > 6) {
                            continue; 
                        }
                        String str = recBoard.board[r][c-r];
                        String next = "empty"; 
                        if (c - r < 6 && c - r >= 0) { 
                            next = recBoard.board[r][c - r + 1];
                        }
                        if (str == null) {
                            consAmCh(consAm, 2, count, last, nullBefore);
                            count = 0;  
                        } else {
                            if (count == 0) { count = 1; }
                            if (str.equals(last)) {
                                if (str.equals(next)) {
                                    continue; 
                                } else if (next == null) {
                                    continue; 
                                } else {
                                    consAmCh(consAm, 1, count, last, nullBefore);
                                }
                            } else {
                                count = 1; 
                                if (last == null) {
                                    nullBefore = true; 
                                } else {
                                    nullBefore = false; 
                                }
                            }
                        }
                        last = str; 
                    }
                }   
                /**
                 * this is where the real math of the alg occurs 
                 * we take note of the diffrential for three in a row, two in a row, and ones that are open(not blocked in)
                 * if a three is open on both sides its worth two, on one side worth one, and if its blocked on either side its worthless
                 * same for two in a row
                 * ones are worth a certain amount of points (1-8) for how many sides are unblocked
                 * threes, twos, and ones are each weighted differently and decay differently 
                 * the difference between 0 threes and 1 three is more significant than the difference between 4 threes and 3 threes
                 * each time points are awarded for a number in a row the amount of points added decays
                 * after playing around with these numbers I found that these seem to work the best 
                 * I could use calculus to make these better but I don't feel like it 
                 * the total points rewarded (with the weights taken into account) is then added at the proper totals slot
                 */
                int threeDif = consAm[0] - consAm[1]; 
                int twoDif = consAm[2] - consAm[3]; 
                int oneDif = consAm[4] - consAm[5]; 
                double threeOrg = 25; 
                double threeDecay = .8; 
                double threeTotal = 0;
                for(int i = 0; i < Math.abs(threeDif); i++) {
                    threeTotal += threeOrg; 
                    threeOrg *= threeDecay; 
                }
                double twoOrg = 15; 
                double twoDecay = .5;
                double twoTotal = 0; 
                for(int i = 0; i < Math.abs(twoDif); i++) {
                    twoTotal += twoOrg; 
                    twoOrg *= twoDecay; 
                }
                double oneOrg = 2; 
                double oneDecay = .9; 
                double oneTotal = 0; 
                for(int i = 0; i < Math.abs(oneDif); i++) {
                    oneTotal += oneOrg; 
                    oneOrg *= oneDecay; 
                }
                if (threeDif < 0) { threeTotal *= -1; }
                if (twoDif < 0) { twoTotal *= -1; }
                if (oneDif < 0) { oneTotal *= -1; }
                double addVal = threeTotal + twoTotal + oneTotal; 
                if (addVal > 100 || addVal < -100) { 
                }
                totals[orgMove] += addVal;
                cases[orgMove]++;  
                return; 
            } else {

                /**
                 * this is where the hypothetical boards occur 
                 * we create an array of boards, one for each possible move
                 * we then copy our old board to each spot in the array with our deep copy constructor array
                 * we then make a move on the board and then run avgsArr for the new board, one index further along
                 * every other turn we add a player piece instead of a bot piece 
                 */
                String pieceString = ""; 
                if (index % 2 == 1){
                    pieceString = bot.getPiece(); 
                } else {
                    pieceString = player.getPiece(); 
                }
                Board[] boards = new Board[recBoard.numAvailCols];
                for(int i = 0; i < boards.length; i++) {
                    boards[i] = new Board(recBoard); 
                }
                for(int i = 0; i < boards.length; i++) {
                    int col = recBoard.availCols[i]; 
                    boards[i].add(pieceString, col); 
                    avgsArr(boards[i], totals, cases, bot, player, index + 1, orgMove, moves);
                }
                return; 
            }
        }
        
        
   

        /**
         * specific helper method for modifying consAm arr in recursive bot algorithm
         * @param arr pass consAm
         * @param max max value added (either 2 or 1)
         * @param count pass count 
         * @param last pass last 
         * @param nullBefore pass nullBefore
         */
        private void consAmCh(int[] arr, int max, int count, String last, boolean nullBefore) {
            if (count == 3) {
                if(last.equals(bot.getPiece())) {
                    if (nullBefore) {
                        arr[0] += max; 
                    } else {
                        arr[0] += max; 
                    }
                } else {
                    if (nullBefore) {
                        arr[1] += max; 
                    } else {
                        arr[1] += max; 
                    }
                }
            } else if (count == 2)  {
                if (last.equals(bot.getPiece())) {
                    if (nullBefore) {
                        arr[2] += max; 
                    } else {
                        arr[2] += max;
                    }
                } else {
                    if (nullBefore) {
                        arr[3] += max; 
                    } else {
                        arr[3] += max; 
                    }
                }
            } else if (count == 1) {
                if (last.equals(bot.getPiece())) {
                    if (nullBefore) {
                        arr[4] += max; 
                    } else {
                        arr[4] += max; 
                    }
                } else {
                    if (nullBefore) {
                        arr[5] += max; 
                    } else {
                        arr[5] += max - 1; 
                    }
                }
            }
        }
        
        /**
         * determines if the game has been won
         * @return true if the game is won 
         */
        private String gameWon() {
            for(int row = 0; row < 6; row++) {
                if(numR[row] >=  4) {
                    String last = null; 
                    int count = 1; 
                    int largestCount = 1; 
                    for(int i = 0; i < 7; i++ ) {
                        String str = this.board[row][i]; 
                        if (count == 3 && i != 6) {
                            String next = this.board[row][i + 1]; 
                            if (next != null && str != null) {
                                if (str.equals(next)) {
                                    return str; 
                                }
                            }
                        }
                        if (str == null) {
                            last = str; 
                            count = 1; 
                        } else {
                            if (str.equals(last)) {
                                count++; 
                            } else {
                                last = str; 
                                count = 1; 
                            }
                        }
                        if (count > largestCount) { largestCount = count; numR[row] = largestCount; }
                        if (count == 4) { return str; }
                    }
                }
            }
            for(int col = 0; col < 7; col++) {
                if(numC[col] >= 4) {
                    String last = null; 
                    int count = 1; 
                    int largestCount = 1; 
                    for(int i = 0; i < 6; i++ ){
                        String str = this.board[i][col]; 
                        if (str == null) {
                            last = str; 
                            count = 1; 
                        } else {
                            if (str.equals(last)) {
                                count++; 
                            } else {
                                last = str; 
                                count = 1; 
                            }
                        }
                        if (count > largestCount) { largestCount = count; numC[col] = largestCount; }
                        if (count == 4) { return str; }
                    }
                }
            }
            for(int c = -2; c <=3; c++) {
                String last = null; 
                int count = 1; 
                int largestCount = 1; 
                for(int r = 0; r < 6; r++) {
                    if (c + r < 0 || c + r > 6) { continue; }
                    String str = board[r][c + r]; 
                    if (str == null) { 
                        count = 1; 
                        last = null; 
                    } else {
                        if (str.equals(last)) {
                            count++; 
                        } else {
                            last = str; 
                            count = 1; 
                        }
                    }
                    if (count > largestCount) { largestCount = count; }
                    if (count == 4) { return str; }
                }
            }
            for(int c = 8; c >= 3; c--) {
                String last = null; 
                int count = 1; 
                int largestCount = 1; 
                for(int r = 0; r < 6; r++) {
                    if (c - r > 6 || c - r < 0) { last = null; continue; }
                    String str = board[r][c-r]; 
                    if (str == null) {
                        count = 1; 
                        last = null; 
                    } else {
                        if (str.equals(last)) {
                            count++; 
                        } else {
                            last = str; 
                            count = 1;  
                        }
                    }
                    if (count > largestCount) { largestCount = count; }
                    if (count == 4) { return str; }
                }
            }
            return null; 
        }
        
        /**
         * helper function that determines if the board is full
         * @return true if not a single spot on the board is open 
         */
        private boolean isFull() {
            for(String[] row: this.board) {
                for(String str: row) {
                    if (row == null) {
                        return true;
                    }
                }
            }
            return false; 
        }
        
        /**
         * private class that holds both a piece and username
         */
        private static class Player {
            private final String username, piece, color_code;
            private static int playerCount;  
            public Player(String username, String piece) {
                this.username = username; 
                if (playerCount == 0) {
                    this.color_code = "\u001B[31m";
                } else {
                    this.color_code = "\u001B[34m"; 
                }
                this.piece = this.color_code + piece + "\u001B[0m"; 
                playerCount++; 
                }
            public String getPiece() { return this.piece; }
            public String getUsername() { return this.username; }
        }
        
        /**
         * uses getInt() to get an input (between 0 and 6 inclusive) until it can add that input 
         */
        private void addPlayerPiece() {
            int playerPos = getInt(); 
            while (!canAdd(playerPos)) {
                System.out.println("colum " + playerPos + " is already full"); 
                playerPos = getInt(); 
            }
            this.add(player.getPiece(), playerPos); 
        }

        /**
         * adds a specified piece to a specified colum of the board, adds to furthers down open area
         * notes that the colum is no longer available if it fills the row
         * helper method 
         * @param piece the piece to add
         * @param col the colum to add the piece
         */
        private void add(String piece, int col) {
            for(int i = 5; i >= 0; i--) {
                if (board[i][col] == null) {
                    board[i][col] = piece; 
                    numC[col]++; 
                    numR[i]++; 
                    if(i == 0) {
                        this.removeCol(col); 
                    }
                    return;
                }
            }
        }

        /**
         * checks if their is space in a colum
         * @param col the colum to check
         * @return true if col has space
         */
        private boolean canAdd(int col) {
            if(col < 0 || col > 6) { return false; }
            return this.board[0][col] == null; 
        }

        /**
         * prints the board
         */
        private void showBoard() {
            for(String[] row: this.board) {
                for(String str: row) {
                    if(str == null) {
                        System.out.print(str + " "); 
                    } else {
                        System.out.print("  " + str + "  ");
                    }
                }
                System.out.println("");
            }
            System.out.println(""); 
        }

        /**
         * gets an int between 0 and 6 inclusive 
         * can handle non-int inputs and out of range inputs 
         * @return an int between 0 and 6 inclusive
         */
        private int getInt() {

            int lowerBound = 0; 
            int upperBound = 7; 
            int playerInput = -1; 
            while( !(playerInput >= lowerBound && playerInput < upperBound) ) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("please give a number from " + lowerBound + " up to " + upperBound+": "); 
                try {
                    playerInput = scanner.nextInt(); 
                } catch (InputMismatchException e) {
                    System.out.println("input must be an int"); 
                    playerInput = -1; 
                }
            }
            return playerInput; 
        }
        
        /**
         * removes a colum from the list of available colums for the bot to play in 
         * @param col - the colum that we are removing from the list, if not already in the list nothing happens
         */
        private void removeCol(int col) {
            if (numAvailCols <= 0) { return; }
            for(int i = 0; i < numAvailCols; i++) {
                if(availCols[i] == col) {
                    for(int j = i; j < numAvailCols - 1; j++) {
                        availCols[j] = availCols[j + 1];
                    }
                    this.numAvailCols--; 
                    return; 
                }
            }
        }
    }
}