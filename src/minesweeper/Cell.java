package minesweeper;

/**
 * Symbols to represent each cell's state:
 * <li>
 * <ul> "." as unexplored cells;
 * <ul> "/" as explored free cells without mines around it;
 * <ul> Numbers from 1 to 8 as explored free cells with 1 to 8 mines around them, respectively;
 * <ul> "X" as mines
 * <ul> "*" as unexplored marked cells
 * </li>
 */
public class Cell {
  CellState state;
  boolean visible;

  private boolean marked;

  int minesAround;

  Cell(CellState state) {
    this.state = state;
    this.visible = false;
  }

  void setMinesAround(int minesAround) {
    this.minesAround = minesAround;
  }

  int getMinesAround() {
    return minesAround;
  }

  public String toString() {
    if (marked && !visible) {
      return "*";
    }
    if (!visible) {
      return ".";
    }
    if (state == CellState.MINE) {
      return "X";
    }
    if (state == CellState.SAFE && minesAround == 0) {
      return "/";
    }
    return minesAround + "";
  }

  boolean isMarked() {
    return marked;
  }

  void setMarked(boolean marked) {
    this.marked = marked;
  }
}
