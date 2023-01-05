/*
3 3 free
3 4 mine
 */
package minesweeper;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The game starts with an unexplored minefield that has a user-defined number of mines.
 * <p>
 * The player can:
 * <li>
 * <ul> "mine": Mark unexplored cells as cells that potentially have a mine, and also remove those marks.
 * Any empty cell can be marked, not just the cells that contain a mine. The mark is removed by marking
 * the previously marked cell.
 * <ul> "free": Explore a cell if they think it does not contain a mine.
 * </li>
 * <p>
 *   Example: 3 2 free
 * <p>
 * There are three possibilities after exploring a cell:
 * <li>
 * <ul> If the cell is empty and has no mines around, all cells around it, including the marked ones, can
 * be explored, and it should be done automatically. Also, if next to the explored cell there is another
 * empty one with no mines around, all surrounding cells should be explored as well, and so on, until
 * no more can be explored automatically.
 * <ul> If a cell is empty and has mines around it, only that cell is explored, revealing a number of mines
 * around it.
 * <ul> If the explored cell contains a mine, the game ends and the player loses.
 * </li>
 * <p>
 * There are two possible ways to win:
 * <li>
 * <ul> Marking all the cells that have mines correctly.
 * <ul> Opening all the safe cells so that only those with unexplored mines are left.
 * </li>
 */
public class Main {

  BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  Field field;
  GameState state;
  boolean firstMove;
  public final int SIZE;

  {
    firstMove = true;
    SIZE = 9;
    state = GameState.RUNNING;
  }

  public static void main(String[] args) {
    Main minesweeper = new Main();
    minesweeper.startGame();
  }

  void startGame() {
    initialize();
    while (isRunning()) {
      makeMove();
      checkSolved();
    }
    printResults();
  }

  private void initialize() {
    int mines = getMinesInput();
    field = new Field(SIZE, mines);
  }

  boolean isRunning() {
    return state == GameState.RUNNING;
  }

  int getMinesInput() {
    int mines;
    while (true) {
      System.out.print("How many mines do you want on the field? ");
      String input = getInput();
      try {
        mines = Integer.parseInt(input);
        if (mines > SIZE * SIZE - 1) {
          System.out.println("Mines shouldn't fill the field");
          continue;
        }
        return mines;
      } catch (NumberFormatException e) {
        System.out.println("Error! Enter one integer for the number of mines.");
      }
    }
  }

  private void makeMove() {
    field.print();
    boolean done = false;
    while (!done) {
      Move move = getMoveInput();
      done = switch (move.command) {
        case "free" -> exploreCell(move.cell);
        case "mine" -> markCell(move.cell);
        default -> throw new IllegalArgumentException("Error! Illegal command: " + move.command);
      };
    }
  }

  Move getMoveInput() {
    int x, y;
    String command;
    while (true) {
      System.out.print("Set/unset mines marks or claim a cell as free: ");
      String input = getInput();
      if (input == null || input.length() == 0) {
        System.out.println("Error! Empty input: " + input);
        continue;
      }
      String[] tokens = input.split("\\s+");
      if (tokens.length != 3) {
        System.out.println("Error! Wrong number of arguments: " + input);
        continue;
      }
      try {
        x = Integer.parseInt(tokens[1]) - 1;
        y = Integer.parseInt(tokens[0]) - 1;
        command = tokens[2];
      } catch (NumberFormatException e) {
        System.out.println("Error! Please enter only numbers for coordinates. " + e.getMessage());
        continue;
      }
      if (x > field.SIZE || y > field.SIZE || x < 0 || y < 0) {
        System.out.println("Error! Coordinates outside the field sizes: " + x + " " + y);
        continue;
      }
      if (!"free".equals(command) && !"mine".equals(command)) {
        System.out.println("Error! Command not supported: " + command);
        continue;
      }
      break;
    }
    return new Move(new Point(x, y), command);

  }

  String getInput() {
    String input = "";
    try {
      input = reader.readLine().strip().toLowerCase();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return input;
  }

  /**
   * Removes the mark from the cell, if it was marked.
   * Marks the cell, if it was unmarked and not open.
   *
   * @param cellPoint cell to mark/unmark
   * @return whether the marking operation was successful
   */
  boolean markCell(Point cellPoint) {
    Cell cell = field.getCell(cellPoint.x, cellPoint.y);
    if (cell.isMarked()) {
      cell.setMarked(false);
      return true;
    }
    if (cell.visible && cell.state == CellState.SAFE && cell.getMinesAround() != 0) {
      System.out.println("There is a number here!");
      return false;
    }
    if (cell.visible) {
      System.out.println("This cell is already open!");
      return false;
    }
    cell.setMarked(true);
    return true;
  }

  boolean exploreCell(Point cellPoint) {
    Cell cell = field.getCell(cellPoint.x, cellPoint.y);
    if (cell.visible) {
      System.out.println("This cell is already open!");
      return false;
    }
    if (cell.state == CellState.MINE && firstMove) {
      field.makeCellSafe(cellPoint);
      firstMove = false;
      return exploreCell(cellPoint);
    }
    firstMove = false;
    if (cell.state == CellState.MINE) {
      setGameLost();
      return true;
    }
    if (cell.state == CellState.SAFE && cell.minesAround != 0) {
      cell.visible = true;
      return true;
    }
    System.out.printf("Explore cell: %s, %s%n", cellPoint.x, cellPoint.y);
    field.floodFill(cellPoint);
    return true;
  }

  void setGameLost() {
    state = GameState.LOST;
    field.uncoverMines();
  }

  void checkSolved() {
    if (field.isSolved()) {
      setGameWon();
    }
  }

  private void setGameWon() {
    state = GameState.WON;
    field.print();
    System.out.println("Congratulations! You found all the mines!");
  }

  void printResults() {
    field.print();
    String msg = switch (state) {
      case WON -> "Congratulations! You found all the mines!";
      case LOST -> "You stepped on a mine and failed!";
      default -> throw new IllegalStateException("Error! Illegal game state: finished & " + state);
    };
    System.out.println(msg);
  }

}
