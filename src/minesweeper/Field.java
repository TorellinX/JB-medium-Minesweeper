package minesweeper;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class Field {
  public final int SIZE;
  public final int mineMum;
  Cell[][] grid;

  Field(int size, int mineNum) {
    this.SIZE = size;
    this.mineMum = mineNum;
    grid = new Cell[SIZE][SIZE];
    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        grid[row][col] = new Cell(CellState.SAFE);
      }
    }
    generateMines();
    countMinesAround();
  }

  public CellState getCellState(int row, int col) {
    return getCell(row, col).state;
  }

  void generateMines() {
    Random random = new Random();
    boolean set;
    int row;
    int cell;
    for (int i = 0; i < mineMum; i++) {
      set = false;
      while (!set) {
        row = random.nextInt(SIZE);
        cell = random.nextInt(SIZE);
        if (grid[row][cell] != null && getCellState(row, cell) != CellState.MINE) {
          grid[row][cell] = new Cell(CellState.MINE);
          set = true;
        }
      }
    }
  }

  void makeCellSafe(Point cellPoint) {
    Random random = new Random();
    int row;
    int cell;
    while (true) {
      row = random.nextInt(SIZE);
      cell = random.nextInt(SIZE);
      if (grid[row][cell] != null && getCellState(row, cell) != CellState.MINE) {
        grid[row][cell] = new Cell(CellState.MINE);
        break;
      }
    }
    grid[cellPoint.x][cellPoint.y] = new Cell(CellState.SAFE);
    countMinesAround();
  }

  void countMinesAround() {
    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        if (getCell(row, col).state != CellState.MINE) {
          getCell(row, col).setMinesAround(countMinesForCell(row, col));
        }
      }
    }
  }

  private int countMinesForCell(int row, int col) {
    int rowStart = row == 0 ? row : row - 1;
    int rowEnd = row == SIZE - 1 ? row : row + 1;
    int colStart = col == 0 ? col : col - 1;
    int colEnd = col == SIZE - 1 ? col : col + 1;
    int sum = 0;
    //System.out.printf("%d %d %d %d%n", rowStart, rowEnd, colStart, colEnd);
    for (int i = rowStart; i <= rowEnd; i++) {
      for (int j = colStart; j <= colEnd; j++) {
        if (i == row && j == col) {
          continue;
        }
        if (getCell(i, j).state == CellState.MINE) {
          sum++;
        }
      }
    }
    return sum;
  }

  Cell getCell(int row, int col) {
    return grid[row][col];
  }




  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(" |123456789|\n-|---------|\n");
    for (int row = 0; row < SIZE; row++) {
      builder.append(row + 1).append("|");
      for (int col = 0; col < SIZE; col++) {
        builder.append(getCell(row, col).toString());
      }
      builder.append("|\n");
    }
    builder.append("-|---------|");
    return builder.toString();
  }

  boolean isSolved() {
    return areAllMinesMarked() || areAllSafeCellsOpened();
  }

  boolean areAllMinesMarked() {
    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        Cell cell = getCell(row, col);
        if (!cell.isMarked() && cell.state == CellState.MINE) {
          return false;
        }
        if (cell.isMarked() && cell.state != CellState.MINE) {
          return false;
        }
      }
    }
    return true;
  }

  boolean areAllSafeCellsOpened() {
    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        Cell cell = getCell(row, col);
        if (!cell.visible && cell.state == CellState.SAFE) {
          return false;
        }
      }
    }
    return true;
  }

  void uncoverMines() {
    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        Cell cell = getCell(row, col);
        if (cell.state == CellState.MINE) {
          cell.visible = true;
          cell.setMarked(false);
        }
      }
    }
  }

  void floodFill(Point cellPoint) {
    // DFS recursive
//    int row = cellPoint.x;
//    int col = cellPoint.y;
//    if (row < 0 || col < 0 || row >= SIZE || col >= SIZE) {
//      return;
//    }
//    Cell cell = getCell(row, col);
//    if (cell.visible || cell.state != CellState.SAFE) {
//      return;
//    }
//    if (cell.minesAround != 0) {
//      cell.visible = true;
//      return;
//    }
//    cell.visible = true;
//    floodFill(row + 1, col);
//    floodFill(row + 1, col + 1);
//    floodFill(row, col + 1);
//    floodFill(row - 1, col + 1);
//    floodFill(row - 1, col);
//    floodFill(row - 1, col - 1);
//    floodFill(row, col - 1);
//    floodFill(row + 1, col - 1);

    // BFS with Queue
    Deque<Point> queue = new ArrayDeque<>();
    queue.add(cellPoint);
    while (!queue.isEmpty()) {
      Point curr = queue.remove();
      int row = curr.x;
      int col = curr.y;
      if (row < 0 || col < 0 || row >= SIZE || col >= SIZE) {
        continue;
      }
      Cell cell = getCell(row, col);
      if (cell.visible || cell.state != CellState.SAFE) {
        continue;
      }
      if (cell.minesAround != 0) {
        cell.visible = true;
        continue;
      }
      cell.visible = true;
      queue.add(new Point(row + 1, col));
      queue.add(new Point(row + 1, col + 1));
      queue.add(new Point(row, col + 1));
      queue.add(new Point(row - 1, col + 1));
      queue.add(new Point(row - 1, col));
      queue.add(new Point(row - 1, col - 1));
      queue.add(new Point(row, col - 1));
      queue.add(new Point(row + 1, col - 1));
    }
  }

  void print() {
    System.out.println(this);
  }

}
