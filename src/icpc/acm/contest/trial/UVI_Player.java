/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icpc.acm.contest.trial;

import black.*;
import javax.swing.JFrame;
import javax.swing.JTextArea;

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
            this.secondPiece = UVI_Player.findNextPossibleMove(firstMove);
        }
        public Move getNextPossibleMove() {
            return this.secondPiece;
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
        debugPanel.setSize(640, 320);
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
        if (lastPlayedCard == 0) {
            // first play of the game
            // We start at [0,0]
            // so the first card will be a cross
            // That will move us to [0,1]
            // Therefore we already know the position 
            // for our opponnet
            this.xPosition = 0;
            this.yPosition = 1;
            // set current direction to down
            this.currentDirection = Direction.DOWN;
            // save the move we just made
            this.lastMove = 2;
            this.virtualBoard[0][1] = new BoardSpace(Move.VERTICAL_LINE);
            return 2;
        } else {
            // calculate board position based on previous card
            // played by our opponent
            // use last known position
            if (lastPlayedCard == 1) {
                // our positioning also depends on our last move
                if (this.currentDirection == Direction.UP) {
                    // move to the right one space
                    this.virtualBoard[this.xPosition][this.yPosition] = new BoardSpace(Move.VERTICAL_LINE);
                    this.xPosition += 1;                    
                    this.currentDirection = Direction.RIGHT;
                } else if (this.currentDirection == Direction.DOWN) {
                    // move to the left by one space
                    this.xPosition -= 1;
                    this.currentDirection = Direction.LEFT;
                } else if (this.currentDirection == Direction.LEFT) {
                    // move one space down
                    this.yPosition += 1;
                    this.currentDirection = Direction.DOWN;
                } else {
                    this.yPosition -= 1;
                    this.currentDirection = Direction.UP;
                }
            } else if (lastPlayedCard == 2) {
                // our positioning also depends on our last move
                if (this.currentDirection == Direction.UP) {
                    // move to the up one space
                    this.yPosition -= 1;
                    this.currentDirection = Direction.UP;
                } else if (this.currentDirection == Direction.DOWN) {
                    // move to the down by one space
                    this.yPosition += 1;
                    this.currentDirection = Direction.DOWN;
                } else if (this.currentDirection == Direction.LEFT) {
                    // move one to the left
                    this.xPosition -= 1;
                    this.currentDirection = Direction.LEFT;
                } else {
                    // move one space to the right
                    this.xPosition += 1;
                    this.currentDirection = Direction.RIGHT;
                }
            } else {
                // our positioning also depends on our last move
                // third card
                if (this.currentDirection == Direction.UP) {
                    // move to the left one space
                    this.xPosition -= 1;
                    this.currentDirection = Direction.LEFT;
                } else if (this.currentDirection == Direction.DOWN) {
                    // move to the right by one space
                    this.xPosition += 1;
                    this.currentDirection = Direction.RIGHT;
                } else if (this.currentDirection == Direction.LEFT) {
                    // move one space up
                    this.yPosition -= 1;
                    this.currentDirection = Direction.UP;
                } else {
                    // move one space down
                    this.yPosition += 1;
                    this.currentDirection = Direction.DOWN;
                }
            }
            displayArea.setText("UVI player was at [" + this.xPosition + "," + this.yPosition + "]");
            int result = level1Algorithm(); // run desired algo
            displayArea.append("\nUVI player moving to [" + this.xPosition + "," + this.yPosition + "]");
            displayArea.append("\nGameBoard length " + this.gameBoard.length);
            displayArea.append("\nOld Direction " + this.currentDirection.toString());
            displayArea.append("\nChosen card " + result);
            displayArea.append("\nNew direction = " + this.currentDirection.toString());
            return result;
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

    public boolean isSafePosition(int x, int y) {
        boolean isSafe = false;
        if (this.gameBoard[x][y] >= 0) {
            isSafe = true;
        } else if (x > this.gameBoard.length - 1 || y > this.gameBoard.length - 1) {
            isSafe = false;
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
