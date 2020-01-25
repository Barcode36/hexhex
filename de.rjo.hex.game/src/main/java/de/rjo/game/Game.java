package de.rjo.game;

import java.util.Random;

import de.rjo.hex.Arrow;
import de.rjo.hex.GridCoordinate;
import de.rjo.hex.Hexagon;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Game {

    private static final String HOVERING_STYLE_CLASS = "entered"; // for when the mouse hovers over a hex
    private static final String SELECTED_STYLE_CLASS = "selected"; // a selected hex
    private static final String NEIGHBOUR_STYLE_CLASS = "neighbour"; // mouse hovers over a neighbour of a selected hex

    private GameState[][] state;

    private int roundNbr;
    private Label roundNbrLabel;
    private Label scoreLabel;
    private Label energyLabel[];
    private int[] scores;
    private Energy[] energy;

    private Rectangle playerToMoveRectangle;
    private Team playerToMove;
    private Hexagon currentlySelected; // hex that has been clicked
    private Hexagon currentlyHoveredNeighbour; // which neighbour is currently being hovered over
    private GridCoordinate[] neighbours;
    private Arrow lineToNeighbour;

    public Game(int nbrRows, int nbrCols, Pane pane) {
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
	    while (hexesChosen != GameProperties.instance().getPropertyInt(GameProperties.NBR_START_HEXES)) {
		var chosenHex = rnd.nextInt(maxHexes);
		var x = chosenHex / nbrCols;
		var y = chosenHex % nbrCols;
//		System.out.println(chosenHex + " " + x + "   " + y);
		if (state[x][y].isEmpty()) {
		    state[x][y].setUnits(team,
			    rnd.nextInt(GameProperties.instance().getPropertyInt(GameProperties.MAX_UNITS_IN_HEX)) + 1);
		    hexesChosen++;
		}
	    }
	}

	currentlySelected = null;

	lineToNeighbour = new Arrow();

	playerToMove = Team.BLUE;

	playerToMoveRectangle = new Rectangle(80, 20);
	setPlayerToMoveStyle(playerToMove);

	var playerToMoveLabel = new Label("player to move");
	Group playerToMoveGroup = new Group(playerToMoveRectangle, playerToMoveLabel);

	var endOfGoButton = new Button("end of go");
	endOfGoButton.setOnMouseClicked(evt -> doEndOfGo());

	roundNbr = 1;
	roundNbrLabel = new Label("Round: " + roundNbr);

	scores = new int[2];
	scoreLabel = new Label(scores[0] + " - " + scores[1]);

	energy = new Energy[2];
	var energyGridPane = new GridPane();
	energyGridPane.getStyleClass().add("energyBox");
	energyLabel = new Label[2];
	for (Team t : new Team[] { Team.BLUE, Team.RED }) {
	    energy[t.ordinal()] = new Energy(GameProperties.instance().getPropertyInt(GameProperties.INITIAL_ENERGY));
	    energyLabel[t.ordinal()] = new Label(t.getName() + ":\t\t" + energy[t.ordinal()].getEnergy());
	}
	energyGridPane.add(energyLabel[Team.BLUE.ordinal()], 0, 0);
	energyGridPane.add(energyLabel[Team.RED.ordinal()], 0, 1);

	var playerGridPane = new GridPane();
	playerGridPane.setLayoutX(20);
	playerGridPane.setLayoutY(20);
	playerGridPane.setHgap(5);

	VBox roundinfoBox = new VBox(roundNbrLabel, playerToMoveGroup, scoreLabel);
	roundinfoBox.getStyleClass().add("infoBox");

	playerGridPane.add(roundinfoBox, 0, 0);
	playerGridPane.add(energyGridPane, 1, 0);
	playerGridPane.add(endOfGoButton, 2, 0);

	// adding lineToNeighbour here (instead of via Main) means the arrow/line does
	// not get displayed!?
	pane.getChildren().addAll(/* lineToNeighbour, */ playerGridPane);
    }

    private void setPlayerToMoveStyle(Team playerToMove) {
	playerToMoveRectangle.getStyleClass().clear();
	playerToMoveRectangle.getStyleClass().add(playerToMove.getStyleClass());
    }

    private void doEndOfGo() {
	if (currentlySelected != null) {
	    onMouseClicked(currentlySelected);// deselect the current hex
	}
	changePlayerToMove();
	updateRound();
    }

    public GameState[][] getState() {
	return state;
    }

    private void changePlayerToMove() {
	if (playerToMove == Team.BLUE) {
	    playerToMove = Team.RED;
	} else {
	    playerToMove = Team.BLUE;
	}
	setPlayerToMoveStyle(playerToMove);
    }

    public Arrow getLineToNeighbour() {
	return lineToNeighbour;
    }

    private void updateRound() {
	if (playerToMove == Team.BLUE) {
	    roundNbr++;
	    roundNbrLabel.setText("Round: " + roundNbr);
	}
    }

    public void setCurrentlySelected(Hexagon currentlySelected) {
	this.currentlySelected = currentlySelected;
	this.neighbours = currentlySelected.getNeighbours(
		GameProperties.instance().getPropertyInt(GameProperties.NBR_ROWS),
		GameProperties.instance().getPropertyInt(GameProperties.NBR_COLS));
    }

    public Hexagon getCurrentlySelected() {
	return this.currentlySelected;
    }

    public void processKey(KeyCode keyCode) {
//	Direction posn = null;
	int nbr = 0;
	switch (keyCode) {
//	case Q:
//	    posn = Direction.NORTHWEST;
//	    break;
//	case W:
//	    posn = Direction.NORTHEAST;
//	    break;
//	case A:
//	    posn = Direction.WEST;
//	    break;
//	case S:
//	    posn = Direction.EAST;
//	    break;
//	case Y:
//	    posn = Direction.SOUTHWEST;
//	    break;
//	case X:
//	    posn = Direction.SOUTHWEST;
//	    break;
	case DIGIT1:
	    nbr = 1;
	    break;
	case DIGIT2:
	    nbr = 2;
	    break;
	case DIGIT3:
	    nbr = 3;
	    break;
	default:
	    break;
	}

	if ((nbr != 0) && (currentlyHoveredNeighbour != null)) {
	    moveToNeighbour(currentlySelected, currentlyHoveredNeighbour, nbr);
	}
//	if (posn != null) {
//	    move(posn);
//	}
    }

    // moves the given number from originHex to the targetHex (neighbour)
    private void moveToNeighbour(Hexagon originHex, Hexagon targetHex, int nbrUnits) {
	var originState = state[originHex.getGridCoordinates().getRow()][originHex.getGridCoordinates().getColumn()];
	var targetState = state[targetHex.getGridCoordinates().getRow()][targetHex.getGridCoordinates().getColumn()];
	if (originState.canMoveFrom(nbrUnits) && targetState.canMoveTo(playerToMove)) {
	    originState.decrement(originHex, nbrUnits);

	    if (targetState.isEmpty()) {
		targetState.setUnits(playerToMove, nbrUnits);
		targetHex.setStyleClass(playerToMove.getStyleClass());
	    } else {
		targetState.increment(nbrUnits);
	    }

	    deselectHex(originHex);
	}
    }

//    // called when user has indicated which direction he wants to move in
//    public void move(Direction posn) {
//	if (currentlySelected == null) {
//	    return;
//	}
//	var x = currentlySelected.getGridCoordinates().getRow();
//	var y = currentlySelected.getGridCoordinates().getColumn();
//
//	state[x][y].decrement(currentlySelected);
//    }

    public void onMouseEntered(Hexagon hex) {
	// if no hex as yet selected, colour it differently
	if (currentlySelected == null) {
	    hex.changeStyleClass(HOVERING_STYLE_CLASS);
	} else {
	    // if a neighbour, colour differently and draw an arrow; otherwise no action
	    for (var neighbour : neighbours) {
		if (hex.getGridCoordinates().equals(neighbour)) {
		    if (hex.equals(currentlyHoveredNeighbour)) {
			// no action, we were hovering here already
		    } else {
			hex.changeStyleClass(NEIGHBOUR_STYLE_CLASS);
			lineToNeighbour.setStartX(currentlySelected.getCentreX());
			lineToNeighbour.setStartY(currentlySelected.getCentreY());
			lineToNeighbour.setEndX(hex.getCentreX());
			lineToNeighbour.setEndY(hex.getCentreY());

			lineToNeighbour.setVisible(true);
			currentlyHoveredNeighbour = hex;
		    }
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
	currentlyHoveredNeighbour = null;
    }

    /*
     * sets (or deselects) the currently selected hex.
     */
    public void onMouseClicked(Hexagon hex) {
	if (hex == currentlySelected) {
	    deselectHex(hex);
	} else {
	    // can only select a hex belonging to the current player.
	    if (state[hex.getGridCoordinates().getRow()][hex.getGridCoordinates().getColumn()]
		    .getTeam() == playerToMove) {
		if (currentlySelected != null) {
		    currentlySelected.resetColour();
		}
		setCurrentlySelected(hex);
		hex.changeStyleClass(SELECTED_STYLE_CLASS);
	    }
	    lineToNeighbour.setVisible(false);
	}
	currentlyHoveredNeighbour = null;
    }

    private void deselectHex(Hexagon hex) {
	hex.resetColour();
	currentlySelected = null;
	currentlyHoveredNeighbour = null;
	lineToNeighbour.setVisible(false);
    }

}