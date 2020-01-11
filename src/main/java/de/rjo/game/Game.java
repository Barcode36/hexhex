package de.rjo.game;

import java.util.Random;

import de.rjo.hex.Arrow;
import de.rjo.hex.Direction;
import de.rjo.hex.GridCoordinate;
import de.rjo.hex.Hexagon;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Game {

    private GameState[][] state;

    private Hexagon currentlySelected;
    private GridCoordinate[] neighbours;
    private Arrow lineToNeighbour;

    public Game(int nbrRows, int nbrCols) {
	state = new GameState[nbrRows][nbrCols];
	for (int row = 0; row < nbrRows; row++) {
	    for (int col = 0; col < nbrCols; col++) {
		state[row][col] = new GameState();
	    }
	}
	// choose a number of different hexs for each team with varying number of team
	// members in each hex
	Random rnd = new Random();
	var maxHexes = nbrRows * nbrCols;

	for (Team team : new Team[] { Team.BLUE, Team.RED }) {
	    var hexesChosen = 0;
	    while (hexesChosen != GameConstants.nbrStartHexes) {
		var chosenHex = rnd.nextInt(maxHexes);
		var x = chosenHex / nbrCols;
		var y = chosenHex % nbrCols;
//		System.out.println(chosenHex + " " + x + "   " + y);
		if (state[x][y].isEmpty()) {
		    state[x][y].setPlayers(team, rnd.nextInt(GameConstants.maxPlayersInHex) + 1);
		    hexesChosen++;
		}
	    }
	}

	currentlySelected = null;

	lineToNeighbour = new Arrow();
    }

    public GameState[][] getState() {
	return state;
    }

    public void setCurrentlySelected(Hexagon currentlySelected) {
	this.currentlySelected = currentlySelected;
	this.neighbours = currentlySelected.getNeighbours(GameConstants.nbrRows, GameConstants.nbrCols);
    }

    public Hexagon getCurrentlySelected() {
	return this.currentlySelected;
    }

    public void processKey(KeyCode keyCode) {
	Direction posn = null;
	switch (keyCode) {
	case Q:
	    posn = Direction.NORTHWEST;
	    break;
	case W:
	    posn = Direction.NORTHEAST;
	    break;
	case A:
	    posn = Direction.WEST;
	    break;
	case S:
	    posn = Direction.EAST;
	    break;
	case Y:
	    posn = Direction.SOUTHWEST;
	    break;
	case X:
	    posn = Direction.SOUTHWEST;
	    break;
	default:
	    break;
	}

	if (posn != null) {
	    move(posn);
	}
    }

    // called when user has indicated which direction he wants to move in
    public void move(Direction posn) {
	if (currentlySelected == null) {
	    return;
	}
	var x = currentlySelected.getGridCoordinates().getRow();
	var y = currentlySelected.getGridCoordinates().getColumn();

	state[x][y].decrement(currentlySelected);
    }

    public void onMouseEntered(Hexagon hex) {
	// if no hex as yet selected, colour it differently
	if (currentlySelected == null) {
	    hex.setFill(Color.HOTPINK);
	} else {
	    // if a neighbour, colour differently and draw an arrow; otherwise no action
	    for (var neighbour : neighbours) {
		if (hex.getGridCoordinates().equals(neighbour)) {
		    hex.setFill(Color.LIGHTPINK);
		    lineToNeighbour.setStartX(currentlySelected.getCentreX());
		    lineToNeighbour.setStartY(currentlySelected.getCentreY());
		    lineToNeighbour.setEndX(hex.getCentreX());
		    lineToNeighbour.setEndY(hex.getCentreY());
		    lineToNeighbour.setVisible(true);
		    break;
		}
	    }
	}
    }

    public void onMouseExited(Hexagon hex) {
	if (hex != currentlySelected) {
	    hex.resetColour();
	}
	lineToNeighbour.setVisible(false);
    }

    public void onMouseClicked(Hexagon hex) {
	if (hex == currentlySelected) {
	    hex.resetColour();
	} else {
	    if (currentlySelected != null) {
		currentlySelected.resetColour();
	    }
	    setCurrentlySelected(hex);
	    hex.setFill(Color.DARKGREEN);
	}
	lineToNeighbour.setVisible(false);
    }

    public Node getLineToNeighbour() {
	return lineToNeighbour;
    }
}
