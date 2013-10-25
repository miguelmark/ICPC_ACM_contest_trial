/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icpc.acm.contest.trial;

import black.*;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import javax.swing.JOptionPane;

/**
 *
 * @author Miguel
 */
public class UVI_Player implements black.Player {

    private class BoardSpace {
        private Move firstPiece;
        private Move secondPiece;
        public BoardSpace(Move firstMove ) {
            this.firstPiece = firstMove;
            this.secondPiece = Move.NO_MOVE;
        }
        public Move getNextPossibleMove() {
            return this.secondPiece;
        }
        
        public void setSecondPiece() {
            this.secondPiece = UVI_Player.findNextPossibleMove(this.firstPiece);
        }
        
    }
    private class NavObj {
        private int xPos;
        private int yPos;
        private Direction direction;
        public NavObj(int xPos, int yPos, Direction direction) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.direction = direction;
        }

        /**
         * @return the xPos
         */
        public int getxPos() {
            return xPos;
        }

        /**
         * @param xPos the xPos to set
         */
        public void setxPos(int xPos) {
            this.xPos = xPos;
        }

        /**
         * @return the yPos
         */
        public int getyPos() {
            return yPos;
        }

        /**
         * @param yPos the yPos to set
         */
        public void setyPos(int yPos) {
            this.yPos = yPos;
        }

        /**
         * @return the direction
         */
        public Direction getDirection() {
            return direction;
        }

        /**
         * @param direction the direction to set
         */
        public void setDirection(Direction direction) {
            this.direction = direction;
        }
        
    }
    public static Move findNextPossibleMove(Move move) {
            switch(move) {
                case TOP_LEFT_CURVE:
                    return Move.BOTTOM_RIGHT_CURVE;
                case TOP_RIGHT_CURVE:
                    return Move.BOTTOM_LEFT_CURVE;
                case BOTTOM_LEFT_CURVE:
                    return Move.TOP_RIGHT_CURVE;
                case BOTTOM_RIGHT_CURVE:
                    return Move.TOP_LEFT_CURVE;
                case HORIZONTAL_LINE:
                    return Move.VERTICAL_LINE;
                case VERTICAL_LINE:
                    return Move.HORIZONTAL_LINE;
                case RED_SQUARE:
                    return Move.NO_MOVE;
                case TIME_BONUS:
                    return Move.ANY;
                default:
                    return Move.ANY;
            }
        }
    private enum Move {
        TOP_LEFT_CURVE, BOTTOM_RIGHT_CURVE,
        HORIZONTAL_LINE, VERTICAL_LINE,
        TOP_RIGHT_CURVE, BOTTOM_LEFT_CURVE,
        RED_SQUARE, TIME_BONUS, NO_MOVE, ANY
    }
    private enum Direction {

        UP, DOWN, LEFT, RIGHT
    }
    private int[][] gameBoard;
    private BoardSpace[][] virtualBoard;
    private int xPosition;
    private int yPosition;
    private Direction currentDirection;
    private int lastMove;
    private static JTextArea displayArea;
    private static JFrame debugPanel;

    static {
        debugPanel = new JFrame();
        displayArea = new JTextArea();
        debugPanel.getContentPane().add(displayArea);
        debugPanel.pack();
        debugPanel.setVisible(true);
        debugPanel.setSize(480, 320);
    }

    public UVI_Player(int[][] gameboard) {
        this.gameBoard = gameboard;
        this.virtualBoard = new BoardSpace[gameboard.length][gameboard.length];
        this.xPosition = 0;
        this.yPosition = 0;
        this.lastMove = 0;
    }

    @Override
    public int play(int lastPlayedCard) {
        if(lastPlayedCard == 0) {
            this.establishBoardLocation(lastPlayedCard);
            return 2;
        }else {
            this.establishBoardLocation(lastPlayedCard);
            displayArea.setText("UVI player was at [" + this.xPosition + "," + this.yPosition + "]");
            int result = level2Algorithm(); // run desired algo
            displayArea.append("\nUVI player moving to [" + this.xPosition + "," + this.yPosition + "]");
            displayArea.append("\nGameBoard length " + this.gameBoard.length);
            displayArea.append("\nOld Direction " + this.currentDirection.toString());
            displayArea.append("\nChosen card " + result);
            displayArea.append("\nNew direction = " + this.currentDirection.toString());
            return result;
        }
    }
    
    private void crawlBoard(int xPos, int yPos, Direction currentDirection) {
        // Determine current board location
        System.out.println("Starting board traversal at [" + xPos + "," + yPos + "]");
        System.out.println("Direction: " + currentDirection.toString());
        BoardSpace currentSpace = this.virtualBoard[xPos][yPos];
        if(currentSpace == null) {
            // empty space
            // base case
            // we have landed on this  space
            System.out.println("Landed on blank space at [" + xPos + "," + yPos + "]");
            System.out.println("Direction: " + currentDirection.toString());
            this.xPosition = xPos;
            this.yPosition = yPos;
            this.currentDirection = currentDirection;
        }else {
            // we might be set off course
            // from the original intended path
            currentSpace.setSecondPiece();
            switch(currentSpace.getNextPossibleMove()) {
                case VERTICAL_LINE:
                    if(currentDirection == Direction.DOWN) {
                        crawlBoard(xPos, yPos + 1, Direction.DOWN);
                    }else if(currentDirection == Direction.UP) {
                        crawlBoard(xPos, yPos - 1, Direction.UP);
                    }
                    break;
                case HORIZONTAL_LINE:
                    if(currentDirection == Direction.LEFT) {
                        crawlBoard(xPos - 1, yPos, Direction.LEFT);
                    }else if(currentDirection == Direction.RIGHT) {
                        crawlBoard(xPos + 1, yPos, Direction.RIGHT);
                    }
                    break;
                case TOP_LEFT_CURVE:
                    if(currentDirection == Direction.RIGHT) {
                        crawlBoard(xPos, yPos - 1, Direction.UP);
                    }else if(currentDirection == Direction.DOWN) {
                        crawlBoard(xPos - 1, yPos, Direction.LEFT);
                    }
                    break;
                case TOP_RIGHT_CURVE:
                    if(currentDirection == Direction.LEFT) {
                        crawlBoard(xPos, yPos - 1, Direction.UP);
                    }else if(currentDirection == Direction.DOWN) {
                        crawlBoard(xPos + 1, yPos, Direction.RIGHT);
                    }
                    break;
                case BOTTOM_LEFT_CURVE:
                    if(currentDirection == Direction.RIGHT) {
                        crawlBoard(xPos, yPos + 1, Direction.DOWN);
                    }else if(currentDirection == Direction.UP) {
                        crawlBoard(xPos - 1, yPos, Direction.LEFT);
                    }
                    break;
                case BOTTOM_RIGHT_CURVE:
                    if(currentDirection == Direction.LEFT) {
                        crawlBoard(xPos, yPos + 1, Direction.DOWN);
                    }else if(currentDirection == Direction.UP) {
                        crawlBoard(xPos + 1, yPos, Direction.RIGHT);
                    }
                    break;
            }
        }
    } 
    
    private NavObj foresight(int xPos, int yPos, Direction currentDirection) {
        // Determine current board location
        // System.out.println("FORESIGHT: Investigating [" + xPos + "," + yPos + "]");
        // System.out.println("currently moving " + currentDirection.toString());
        // Check if have reached out of bounds
        if(xPos > this.gameBoard.length - 1  || xPos < 0 || yPos > this.gameBoard.length - 1 || yPos < 0) {
            // reached out of bounds return unsafe indicator
            System.out.println("\nOoops! Reached out of bounds!\n");
            return new NavObj(-1, -1, Direction.DOWN);
        }
        BoardSpace currentSpace = this.virtualBoard[xPos][yPos];
        if(currentSpace == null) {
            // empty space
            // base case
            // we have landed on this  space
            return new NavObj(xPos, yPos, currentDirection);
        }else {
            // we might be set off course
            // from the original intended path
            currentSpace.setSecondPiece();
            switch(currentSpace.getNextPossibleMove()) {
                case VERTICAL_LINE:
                    if(currentDirection == Direction.DOWN) {
                        return foresight(xPos, yPos + 1, Direction.DOWN);
                    }else if(currentDirection == Direction.UP) {
                        return foresight(xPos, yPos - 1, Direction.UP);
                    }
                    break;
                case HORIZONTAL_LINE:
                    if(currentDirection == Direction.LEFT) {
                        return foresight(xPos - 1, yPos, Direction.LEFT);
                    }else if(currentDirection == Direction.RIGHT) {
                        return foresight(xPos + 1, yPos, Direction.RIGHT);
                    }
                    break;
                case TOP_LEFT_CURVE:
                    if(currentDirection == Direction.RIGHT) {
                        return foresight(xPos, yPos - 1, Direction.UP);
                    }else if(currentDirection == Direction.DOWN) {
                        return foresight(xPos - 1, yPos, Direction.LEFT);
                    }
                    break;
                case TOP_RIGHT_CURVE:
                    if(currentDirection == Direction.LEFT) {
                        return foresight(xPos, yPos - 1, Direction.UP);
                    }else if(currentDirection == Direction.DOWN) {
                        return foresight(xPos + 1, yPos, Direction.RIGHT);
                    }
                    break;
                case BOTTOM_LEFT_CURVE:
                    if(currentDirection == Direction.RIGHT) {
                        return foresight(xPos, yPos + 1, Direction.DOWN);
                    }else if(currentDirection == Direction.UP) {
                        return foresight(xPos - 1, yPos, Direction.LEFT);
                    }
                    break;
                case BOTTOM_RIGHT_CURVE:
                    if(currentDirection == Direction.LEFT) {
                        return foresight(xPos, yPos + 1, Direction.DOWN);
                    }else if(currentDirection == Direction.UP) {
                        return foresight(xPos + 1, yPos, Direction.RIGHT);
                    }
                    break;
                 default:
                     return null;
                     
            }
        }
        
        return null;
    }
    
    private void establishBoardLocation(int lastPlayedCard) {
        System.out.println("\nEstablishing board position: lastPlayed card is " + lastPlayedCard);
        if (lastPlayedCard == 0) {
            // first play of the game
            // We start at [0,0]
            // so the first card will be a cross
            // That will move us to [0,1]
            // Therefore we already know the position 
            // for our opponnet
            System.out.println("\n" + this.getName() + " makes the first move!");
            this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
            this.xPosition = 0;
            this.yPosition = 1;
            // set current direction to down
            this.currentDirection = Direction.DOWN;
            // save the move we just made
            // this.lastMove = 2;
            
        } else {
            // calculate board position based on previous card
            // played by our opponent
            // use last known position
            if (lastPlayedCard == 1) {
                // our positioning also depends on our last move
                if (this.currentDirection == Direction.UP) {
                    // move to the right one space
                    // Place card in virtual card space
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_RIGHT_CURVE);
                    // Determine actual positioning based on current board condition
                    crawlBoard(xPosition + 1, yPosition, Direction.RIGHT);                                        
                } else if (this.currentDirection == Direction.DOWN) {
                    // move to the left by one space
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_LEFT_CURVE);
                    crawlBoard(xPosition - 1, yPosition, Direction.LEFT);
                } else if (this.currentDirection == Direction.LEFT) {
                    // move one space down
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_RIGHT_CURVE);
                    crawlBoard(xPosition, yPosition + 1, Direction.DOWN);
                    
                } else {
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_LEFT_CURVE);
                    crawlBoard(xPosition, yPosition - 1, Direction.UP);                   
                }
            } else if (lastPlayedCard == 2) {
                // our positioning also depends on our last move
                if (this.currentDirection == Direction.UP) {
                    // move to the up one space
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                    crawlBoard(xPosition, yPosition - 1, Direction.UP);
                                      
                } else if (this.currentDirection == Direction.DOWN) {
                    // move to the down by one space
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                    crawlBoard(xPosition, yPosition + 1, Direction.DOWN);
                    
                } else if (this.currentDirection == Direction.LEFT) {
                    // move one to the left
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.HORIZONTAL_LINE);
                    crawlBoard(xPosition - 1, yPosition, Direction.LEFT);
                    
                } else {
                    // move one space to the right
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.HORIZONTAL_LINE);
                    crawlBoard(xPosition + 1, yPosition, Direction.RIGHT);                   
                }
            } else {
                // our positioning also depends on our last move
                // third card
                if (this.currentDirection == Direction.UP) {
                    // move to the left one space
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_LEFT_CURVE);
                    crawlBoard(xPosition - 1, yPosition, Direction.LEFT);
                    
                } else if (this.currentDirection == Direction.DOWN) {
                    // move to the right by one space
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_RIGHT_CURVE);
                    crawlBoard(xPosition + 1, yPosition, Direction.RIGHT);
                                      
                } else if (this.currentDirection == Direction.LEFT) {
                    // move one space up
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_RIGHT_CURVE);
                    crawlBoard(xPosition, yPosition - 1, Direction.UP);
      
                } else {
                    // move one space down
                    this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_LEFT_CURVE);
                    crawlBoard(xPosition, yPosition + 1, Direction.DOWN);
                    
                }
            }
            
        }
    }
    
    public int trivialAlgorithm() {
        // given our current position determine a safe move to perform
        // This does NOT provide the most optimal move
        // but rather a move that is legal
        // if no moves are legal or safe
        // the player will choose the first move by default
        if (this.currentDirection == Direction.UP) {
            if ((this.yPosition - 1) > 0) {
                // we can continue to move up
                // return cross card
                this.yPosition -= 1;
                this.currentDirection = Direction.UP;
                return 2;
            } else if ((this.xPosition - 1) > 0) {
                // we can go to the left one
                this.xPosition -= 1;
                this.currentDirection = Direction.LEFT;
                return 3;
            } else if ((this.xPosition + 1) < this.gameBoard.length - 1) {
                // we can go the right
                this.xPosition += 1;
                this.currentDirection = Direction.RIGHT;
                return 1;
            } else {
                // we lose
                return 1;
            }
        } else if (this.currentDirection == Direction.DOWN) {
            if ((this.yPosition + 1) < this.gameBoard.length - 1) {
                // we can continue to move down
                // return cross card
                this.yPosition += 1;
                this.currentDirection = Direction.DOWN;
                return 2;
            } else if ((this.xPosition - 1) > 0) {
                // we can go to the left one
                this.xPosition -= 1;
                this.currentDirection = Direction.LEFT;
                return 1;
            } else if ((this.xPosition + 1) < this.gameBoard.length - 1) {
                // we can go the right one
                this.xPosition += 1;
                this.currentDirection = Direction.RIGHT;
                return 3;
            } else {
                // we lose
                return 1;
            }
        } else if (this.currentDirection == Direction.LEFT) {
            if ((this.xPosition - 1) > 0) {
                // we can continue to move left
                // return cross card 
                this.xPosition -= 1;
                this.currentDirection = Direction.LEFT;
                return 2;
            } else if ((this.yPosition - 1) > 0) {
                // we can go up one
                this.yPosition -= 1;
                this.currentDirection = Direction.UP;
                return 3;
            } else if ((this.yPosition + 1) < this.gameBoard.length - 1) {
                // we can down one
                this.yPosition += 1;
                this.currentDirection = Direction.DOWN;
                return 1;
            } else {
                // we lose
                return 1;
            }
        } else {
            // this.currentDirection == Direction.RIGHT;
            if ((this.xPosition + 1) < this.gameBoard.length - 1) {
                // we can continue to go to right
                // return cross card
                this.xPosition += 1;
                this.currentDirection = Direction.RIGHT;
                return 2;
            } else if ((this.yPosition - 1) > 0) {
                // we can go to the left one
                this.yPosition -= 1;
                this.currentDirection = Direction.UP;
                return 1;
            } else if ((this.yPosition + 1) < this.gameBoard.length - 1) {
                // we can go down one
                // System.out.println("this.yPosition + 1 > 0 : " + (this.yPosition + 1));
                // System.out.println("this.xPosition + 1 < this.gameBoard.length - 1 " + (this.xPosition + 1));
                this.yPosition += 1;
                this.currentDirection = Direction.DOWN;
                return 3;
            } else {
                // we lose
                return 1;
            }
        }
    }
    
    public int level1Algorithm() {
        // given our current position determine a safe move to perform
        // This does NOT provide the most optimal move
        // but rather a move that is legal
        // if no moves are legal or safe
        // the player will choose the first move by default
        // Level 1 attempts to avoid red squares
        // if no safe moves are possible the second card
        // is chosen by default
        int card = 2;
        int option = 0;
        boolean cardIsChosen = false;
        do {
            if (this.currentDirection == Direction.UP) {
                
                switch (option) {
                    case 0:
                        if ((this.yPosition - 1) > 0) {
                            // we can continue to move up
                            // return cross card
                            if (isSafePosition(xPosition, yPosition - 1)) {
                                this.yPosition -= 1;
                                this.currentDirection = Direction.UP;
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.xPosition - 1) > 0) {
                            // we can go to the left one
                            if (isSafePosition(xPosition - 1, yPosition )) {
                                this.xPosition -= 1;
                                this.currentDirection = Direction.LEFT;
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.xPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            if (isSafePosition(xPosition + 1, yPosition)) {
                                this.xPosition += 1;
                                this.currentDirection = Direction.RIGHT;
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
                //
            } else if (this.currentDirection == Direction.DOWN) {
                switch (option) {
                    case 0:
                        if ((this.yPosition + 1) <= this.gameBoard.length - 1) {
                            // we can continue to move up
                            // return cross card
                            if (isSafePosition(xPosition, yPosition + 1)) {
                                this.yPosition += 1;
                                this.currentDirection = Direction.DOWN;
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.xPosition - 1) > 0) {
                            // we can go to the left one
                            if (isSafePosition(xPosition - 1, yPosition )) {
                                this.xPosition -= 1;
                                this.currentDirection = Direction.LEFT;
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.xPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            if (isSafePosition(xPosition + 1, yPosition)) {
                                this.xPosition += 1;
                                this.currentDirection = Direction.RIGHT;
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
                
                
            } else if (this.currentDirection == Direction.LEFT) {                
                ///////////////
                switch (option) {
                    case 0:
                        if ((this.xPosition - 1) > 0) {
                            // we can continue to move up
                            // return cross card
                            if (isSafePosition(xPosition - 1, yPosition)) {
                                this.xPosition -= 1;
                                this.currentDirection = Direction.LEFT;
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.yPosition - 1) > 0) {
                            // we can go to the left one
                            if (isSafePosition(xPosition, yPosition - 1 )) {
                                this.yPosition -= 1;
                                this.currentDirection = Direction.UP;
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.yPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            if (isSafePosition(xPosition, yPosition + 1)) {
                                this.yPosition += 1;
                                this.currentDirection = Direction.DOWN;
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
            } else {
                // this.currentDirection == Direction.RIGHT;
                switch (option) {
                    case 0:
                        if ((this.xPosition + 1) <= this.gameBoard.length -1) {
                            // we can continue to move up
                            // return cross card
                            if (isSafePosition(xPosition + 1, yPosition)) {
                                this.xPosition += 1;
                                this.currentDirection = Direction.RIGHT;
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.yPosition - 1) > 0) {
                            // we can go to the left one
                            if (isSafePosition(xPosition, yPosition - 1 )) {
                                this.yPosition -= 1;
                                this.currentDirection = Direction.UP;
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.yPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            if (isSafePosition(xPosition, yPosition + 1)) {
                                this.yPosition += 1;
                                this.currentDirection = Direction.DOWN;
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
            }
        } while (cardIsChosen != true);


        return card;
    }

    public int level2Algorithm() {
        // given our current position determine a safe move to perform
        // This does NOT provide the most optimal move
        // but rather a move that is legal
        // if no moves are legal or safe
        // the player will choose the first move by default
        // Level 1 attempts to avoid red squares
        // if no safe moves are possible the second card
        // is chosen by default
        System.out.println("\nUtilizing Level 2 algorithm with foresight ability....\n");
        int card = 2;
        int option = 0;
        boolean cardIsChosen = false;
        NavObj nextLocation;
        do {
            if (this.currentDirection == Direction.UP) {
                
                switch (option) {
                    case 0:
                        if ((this.yPosition - 1) > 0) {
                            // we can continue to move up
                            // return cross card
                            
                            // determine abosulte ending position on board
                            nextLocation = foresight(xPosition, yPosition - 1, Direction.UP);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.xPosition - 1) > 0) {
                            // we can go to the left one
                            nextLocation = foresight(xPosition - 1, yPosition, Direction.LEFT);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos() )) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_LEFT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.xPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            nextLocation = foresight(xPosition + 1, yPosition, Direction.RIGHT);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_RIGHT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                        System.out.println("We lost at this point [" + xPosition + "," + yPosition +  "] :(");
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
                //
            } else if (this.currentDirection == Direction.DOWN) {
                switch (option) {
                    case 0:
                        if ((this.yPosition + 1) <= this.gameBoard.length - 1) {
                            // we can continue to move up
                            // return cross card
                            nextLocation = foresight(xPosition, yPosition + 1, Direction.DOWN);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.xPosition - 1) > 0) {
                            // we can go to the left one
                            nextLocation = foresight(xPosition - 1, yPosition, Direction.LEFT);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos() )) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_LEFT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.xPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            nextLocation = foresight(xPosition + 1, yPosition, Direction.RIGHT);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_RIGHT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                        System.out.println("We lost at this point [" + xPosition + "," + yPosition +  "] :(");
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
                
                
            } else if (this.currentDirection == Direction.LEFT) {                
                ///////////////
                switch (option) {
                    case 0:
                        if ((this.xPosition - 1) > 0) {
                            // we can continue to move up
                            // return cross card
                            nextLocation = foresight(xPosition - 1, yPosition, Direction.LEFT);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.HORIZONTAL_LINE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.yPosition - 1) > 0) {
                            // we can go to the left one
                            nextLocation = foresight(xPosition, yPosition - 1, Direction.UP);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos() )) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_RIGHT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.yPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            nextLocation = foresight(xPosition, yPosition + 1, Direction.DOWN);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_RIGHT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.HORIZONTAL_LINE);
                        System.out.println("We lost at this point [" + xPosition + "," + yPosition +  "] :(");
                        card = 2; 
                        cardIsChosen = true;
                        break;
                }
            } else {
                // this.currentDirection == Direction.RIGHT;
                switch (option) {
                    case 0:
                        if ((this.xPosition + 1) <= this.gameBoard.length -1) {
                            // we can continue to move up
                            // return cross card
                            nextLocation = foresight(xPosition + 1, yPosition, Direction.RIGHT);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.HORIZONTAL_LINE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 2;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        } else {
                            option += 1;
                        }
                        break;
                    case 1:
                        if ((this.yPosition - 1) > 0) {
                            // we can go to the left one
                            nextLocation = foresight(xPosition, yPosition - 1, Direction.UP);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.TOP_LEFT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 1;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    case 2:
                        if ((this.yPosition + 1) <= this.gameBoard.length - 1) {
                            // we can go the right
                            nextLocation = foresight(xPosition, yPosition + 1, Direction.DOWN);
                            if (isSafePosition(nextLocation.getxPos(), nextLocation.getyPos())) {
                                this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.BOTTOM_LEFT_CURVE);
                                this.xPosition = nextLocation.getxPos();
                                this.yPosition = nextLocation.getyPos();
                                this.currentDirection = nextLocation.getDirection();
                                card = 3;
                                cardIsChosen = true;
                            } else {
                                option += 1;
                            }
                        }else {
                            option += 1;
                        }
                        break;
                    default:
                        // lost
                        // ran out of options
                        // reset option counter
                        option = 0;
                        // select default card to return
                        this.virtualBoard[xPosition][yPosition] = new BoardSpace(Move.HORIZONTAL_LINE);
                        System.out.println("We lost at this point [" + xPosition + "," + yPosition +  "] :(");
                        card = 2;
                        cardIsChosen = true;
                        break;
                }
            }
        } while (cardIsChosen != true);


        return card;
    }
    
    private boolean isSafePosition(int x, int y) {
        boolean isSafe = false;
        if(x < 0 || y < 0) {
            isSafe = false;
        } else if (x > this.gameBoard.length - 1 || y > this.gameBoard.length - 1) {
            isSafe = false;
        } else if(this.gameBoard[x][y] >= 0) {
            isSafe = true;
        } else {
            isSafe =  false;
        }
        System.out.println("[" + x + "," + y + "] " + (isSafe == true ? " is a safe position" : "is not a safe position"));
        return isSafe;
    }

    @Override
    public String getName() {
        return "UVI Virtual Player";
    }

    public void setTextArea(JTextArea displayArea) {
        this.displayArea = displayArea;
    }
}
