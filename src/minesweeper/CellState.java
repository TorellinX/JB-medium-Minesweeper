package minesweeper;

public enum CellState {
  MINE, SAFE;


  @Override
  public String toString() {
    return switch (this) {
      case MINE -> "X";
      case SAFE -> ".";
    };
  }

}
