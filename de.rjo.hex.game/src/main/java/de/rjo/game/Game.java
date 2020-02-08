package de.rjo.game;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import de.rjo.hex.Arrow;
import de.rjo.hex.GridCoordinate;
import de.rjo.hex.Hexagon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Game {

    private static final String HOVERING_STYLE_CLASS = "entered"; // for when the mouse hovers over a hex
    private static final String SELECTED_STYLE_CLASS = "selected"; // a selected hex
    private static final String NEIGHBOUR_STYLE_CLASS = "neighbour"; // mouse hovers over a neighbour of a selected hex

    // some messages for the info label
    private static final String INFO_NOT_ENOUGH_UNITS = "not enough units";
    private static final String INFO_WRONG_TEAM = "wrong team, cannot move there";
    private static final String INFO_NOT_ENOUGH_ENERGY = "not enough energy";
    private static final String INFO_NO_MOVES_LEFT = "no moves left";

    private GameState[][] state;
    private Hexagon[][] board;

    private IntegerProperty roundNbr;
    private IntegerProperty[] scores;
    private Energy[] energy;
    private StringProperty infoText;

    private IntegerProperty nbrMovesAvaliableInRound; // number of moves that a player can still make in this round
    private ObjectProperty<Team> playerToMove; // which player is currently moving
    private Hexagon currentlySelected; // hex that has been clicked
    private Hexagon currentlyHoveredNeighbour; // which neighbour is currently being hovered over
    private GridCoordinate[] neighbours;
    private Arrow lineToNeighbour;

    private static interface Rules {
	int ENERGY_TO_MOVE_ONE_UNIT = 5; // how much energy is required to move one unit one hex
	int ENERGY_GAINED_PER_OCCUPIED_HEX = 6; // how much energy is gained per occupied hex (at end of round)
	int BONUS_FRIENDLY_NEIGHBOUR = 2; // bonus for each friendly neighbour
	int PENALTY_ENEMY_NEIGHBOUR = 2; // penalty for each enemy neighbour
	int NBR_FRIENDLY_NEIGHBOURS_FOR_BONUS = 2; // extra units with (at least) this number of friendly neighbours
    }

    public Game(Pane pane) {
	state = new GameState[GameProperties.NBR_ROWS][GameProperties.NBR_COLS];
	for (int row = 0; row < GameProperties.NBR_ROWS; row++) {
	    for (int col = 0; col < GameProperties.NBR_COLS; col++) {
		state[row][col] = new GameState(row, col);
	    }
	}
	// choose a number of different hexs for each team with varying number of team
	// members in each hex
	Random rnd = new Random();
	var maxHexes = GameProperties.NBR_ROWS * GameProperties.NBR_COLS;

	for (Team team : new Team[] { Team.BLUE, Team.RED }) {
	    var hexesChosen = 0;
	    while (hexesChosen != GameProperties.instance().getPropertyInt(GameProperties.NBR_START_HEXES)) {
		var chosenHex = rnd.nextInt(maxHexes);
		var x = chosenHex / GameProperties.NBR_COLS;
		var y = chosenHex % GameProperties.NBR_COLS;
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

	playerToMove = new SimpleObjectProperty<Team>(Team.BLUE);
	nbrMovesAvaliableInRound = new SimpleIntegerProperty(
		GameProperties.instance().getPropertyInt(GameProperties.NBR_MOVES_PER_ROUND));

	var endOfGoButton = new Button("end of go");
	endOfGoButton.setOnMouseClicked(evt -> doEndOfGo());

	roundNbr = new SimpleIntegerProperty(1);
	var roundNbrLabel = new Label();
	roundNbrLabel.textProperty().bind(Bindings.format("%s%s", "Round: ", roundNbr.asString()));

	scores = new IntegerProperty[2];
	scores[0] = new SimpleIntegerProperty(0);
	scores[1] = new SimpleIntegerProperty(0);
	var scoreLabel = new Label();
	scoreLabel.textProperty().bind(Bindings.format("%s-%s", scores[0].asString(), scores[1].asString()));

	energy = new Energy[2];
	var energyGridPane = new GridPane();
	energyGridPane.getStyleClass().add("energyBox");
	var energyLabel = new Label[2];
	for (Team t : new Team[] { Team.BLUE, Team.RED }) {
	    energy[t.ordinal()] = new Energy(GameProperties.instance().getPropertyInt(GameProperties.INITIAL_ENERGY));
	    energyLabel[t.ordinal()] = new Label();
	    energyLabel[t.ordinal()].getStyleClass().add("energyLabel");
	    energyLabel[t.ordinal()].textProperty()
		    .bind(Bindings.format("%s:\t\t%3s", t.getName(), energy[t.ordinal()].getEnergy().asString("%3s")));

	}
	energyGridPane.add(energyLabel[Team.BLUE.ordinal()], 0, 0);
	energyGridPane.add(energyLabel[Team.RED.ordinal()], 0, 1);

	var playerGridPane = new GridPane();
	playerGridPane.setLayoutX(20);
	playerGridPane.setLayoutY(20);
	playerGridPane.setHgap(5);

	VBox roundinfoBox = new VBox(roundNbrLabel, scoreLabel);
	roundinfoBox.getStyleClass().add("infoBox");

	infoText = new SimpleStringProperty("");
	var infoLabel = new Label();
	infoLabel.textProperty().bind(infoText);

	var nbrMovesIndicator = new MoveProgressBar(playerToMove, nbrMovesAvaliableInRound,
		GameProperties.instance().getPropertyInt(GameProperties.NBR_MOVES_PER_ROUND));

	playerGridPane.add(roundinfoBox, 0, 0);
	playerGridPane.add(energyGridPane, 1, 0);
	playerGridPane.add(nbrMovesIndicator, 2, 0);
	playerGridPane.add(endOfGoButton, 3, 0);
	playerGridPane.add(infoLabel, 4, 0);

	// adding lineToNeighbour here (instead of via Main) means the arrow/line does
	// not get displayed!?
	pane.getChildren().addAll(/* lineToNeighbour, */ playerGridPane);
    }

    private void doEndOfGo() {
	if (currentlySelected != null) {
	    onMouseClicked(currentlySelected);// deselect the current hex
	}
	infoText.set("");
	changePlayerToMove();
	if (playerToMove.get() == Team.BLUE) {
	    endOfRound();
	}
    }

    public GameState[][] getState() {
	return state;
    }

    private void changePlayerToMove() {
	if (playerToMove.get() == Team.BLUE) {
	    playerToMove.set(Team.RED);
	} else {
	    playerToMove.set(Team.BLUE);
	}
	nbrMovesAvaliableInRound.set(GameProperties.instance().getPropertyInt(GameProperties.NBR_MOVES_PER_ROUND));
    }

    public Arrow getLineToNeighbour() {
	return lineToNeighbour;
    }

    private void endOfRound() {
	roundNbr.set(roundNbr.get() + 1); // add(1) doesn't work as expected
	boostEnergyLevels();
	addToArmies();
    }

    private long countNeighbours(GameState gamestate, Team requiredTeam) {
	return gamestate.streamNeighbours(board, GameProperties.NBR_ROWS, GameProperties.NBR_COLS)
		.filter(neighbour -> state[neighbour.getRow()][neighbour.getColumn()].getTeam() == requiredTeam)
		.count();
    }

    // hex's with at least two neighbours get a bonus of 1
    private void addToArmies() {
	for (Team team : new Team[] { Team.BLUE, Team.RED }) {
	    streamGameState().filter(hex -> hex.getTeam() == team).forEach(gamestate -> {
		long friendlyNeighbours = countNeighbours(gamestate, team);
		gamestate.increment((int) (friendlyNeighbours / Rules.NBR_FRIENDLY_NEIGHBOURS_FOR_BONUS));
	    });
	}
    }

    // new energy levels are calulated at the end of each round
    private void boostEnergyLevels() {
	String info = "";
	for (Team team : new Team[] { Team.BLUE, Team.RED }) {
	    AtomicInteger totalEnergyGained = new AtomicInteger(0);
	    streamGameState().filter(hex -> hex.getTeam() == team).forEach(gamestate -> {
		long friendlyNeighbours = countNeighbours(gamestate, team);
		long enemyNeighbours = countNeighbours(gamestate, team.getOpponent());

		long energyGained = Rules.ENERGY_GAINED_PER_OCCUPIED_HEX
			+ friendlyNeighbours * Rules.BONUS_FRIENDLY_NEIGHBOUR
			- enemyNeighbours * Rules.PENALTY_ENEMY_NEIGHBOUR;
		System.out.println(
			"team " + team + " energyGained: " + energyGained + " for " + gamestate.getCoordinates()
				+ " got friendly: " + friendlyNeighbours + " enemy: " + enemyNeighbours);
		totalEnergyGained.addAndGet((int) energyGained);
	    });

	    energy[team.ordinal()].increase(totalEnergyGained.get());
	    info += " +" + totalEnergyGained.get() + " for team " + team.getName() + ";";
	}
	infoText.set(info);
    }

    private Stream<GameState> streamGameState() {
	return Arrays.stream(state).flatMap(Arrays::stream);
    }

    public void setCurrentlySelected(Hexagon currentlySelected) {
	this.currentlySelected = currentlySelected;
	this.neighbours = currentlySelected.getNeighbours(GameProperties.NBR_ROWS, GameProperties.NBR_COLS);
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
    }

    // moves the given number from originHex to the targetHex (neighbour)
    private void moveToNeighbour(Hexagon originHex, Hexagon targetHex, int nbrUnits) {
	var originState = state[originHex.getGridCoordinates().getRow()][originHex.getGridCoordinates().getColumn()];
	var targetState = state[targetHex.getGridCoordinates().getRow()][targetHex.getGridCoordinates().getColumn()];

	// check if move allowed
	if (!originState.canMoveFrom(nbrUnits)) {
	    infoText.set(INFO_NOT_ENOUGH_UNITS);
	} else if (!targetState.canMoveTo(playerToMove.get())) {
	    infoText.set(INFO_WRONG_TEAM);
	} else if (nbrMovesAvaliableInRound.get() == 0) {
	    infoText.set(INFO_NO_MOVES_LEFT);
	} else {
	    var energyRequired = Rules.ENERGY_TO_MOVE_ONE_UNIT * nbrUnits;
	    if (energyRequired > energy[playerToMove.get().ordinal()].getEnergy().get()) {
		infoText.set(INFO_NOT_ENOUGH_ENERGY);
	    } else {
		originState.decrement(originHex, nbrUnits);

		if (targetState.isEmpty()) {
		    targetState.setUnits(playerToMove.get(), nbrUnits);
		    targetHex.setStyleClass(playerToMove.get().getStyleClass());
		} else {
		    targetState.increment(nbrUnits);
		}

		deselectHex(originHex);
		infoText.set("");
		energy[playerToMove.get().ordinal()].reduce(energyRequired);
		nbrMovesAvaliableInRound.set(nbrMovesAvaliableInRound.get() - 1);
	    }
	}
    }

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
	    if (state[hex.getGridCoordinates().getRow()][hex.getGridCoordinates().getColumn()].getTeam() == playerToMove
		    .get()) {
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

    public void setBoard(Hexagon[][] board) {
	this.board = board;
    }

}
