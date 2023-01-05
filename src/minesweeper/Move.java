package minesweeper;

import java.awt.Point;

public class Move {

  Point cell;
  String command;

  Move(Point cell, String command) {
    this.cell = cell;
    this.command = command;
  }

}
