package de.rjo.game;

import de.rjo.hex.Hexagon;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    final int sizeOfHexagons = 50;

    private final static int widthOfMainPane = 850;
    private final static int heightOfMainPane = 850;

    private Hexagon[][] board;
    private Game game;

    private MouseEvent mousePosition;

    @Override
    public void start(Stage stage) {

	Hexagon.size = sizeOfHexagons;
	Hexagon.X_OFFSET = 100;
	Hexagon.Y_OFFSET = 130;

	Pane pane = new Pane();
	initGame(pane);
	initBoard();

	for (int row = 0; row < GameConstants.nbrRows; row++) {
	    for (int col = 0; col < GameConstants.nbrCols; col++) {
		game.getState()[row][col].getLabel().setLayoutX(board[row][col].getPointAtNorthWest().x + 10);
		game.getState()[row][col].getLabel().setLayoutY(board[row][col].getPointAtNorthWest().y);

		pane.getChildren().addAll(board[row][col], game.getState()[row][col].getLabel());
	    }
	}

	pane.getChildren().add(game.getLineToNeighbour());
	// this tracks the mouse position
	pane.addEventFilter(MouseEvent.MOUSE_MOVED, event -> this.mousePosition = event);

	final ScrollPane sp = new ScrollPane();
	sp.setHbarPolicy(ScrollBarPolicy.NEVER);
	sp.setVbarPolicy(ScrollBarPolicy.NEVER);
	sp.setContent(pane);
	sp.setPannable(false); // set true for panning

	Scene scene = new Scene(sp, widthOfMainPane, heightOfMainPane);
	scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> game.processKey(event.getCode()));

//	    System.out.println("Pressed: " + event.getCode() + ",    " + "(x: " + mousePosition.getX() + ", y: "
//		    + mousePosition.getY() + ") -- " + "(sceneX: " + mousePosition.getSceneX() + ", sceneY: "
//		    + mousePosition.getSceneY() + ") -- (screenX: " + mousePosition.getScreenX() + ", screenY: "
//		    + mousePosition.getScreenY() + ") --- + scrollpane: " + sp.getHmin() + "," + sp.getHmax() + ","
//		    + sp.getLayoutY() + "   " + sp.getHvalue() + "," + sp.getVvalue() + ")");

	stage.setScene(scene);
	stage.setTitle("hex-hex");

	stage.show();
    }

    private void initGame(Pane pane) {
	game = new Game(GameConstants.nbrRows, GameConstants.nbrCols, pane);
    }

    private void initBoard() {
	board = new Hexagon[GameConstants.nbrRows][GameConstants.nbrCols];

	for (int row = 0; row < GameConstants.nbrRows; row++) {
	    for (int col = 0; col < GameConstants.nbrCols; col++) {
		board[row][col] = new Hexagon(row, col, game.getState()[row][col].getTeam().getColor());

		board[row][col].setOnMouseClicked(mevt -> {
		    game.onMouseClicked((Hexagon) mevt.getSource());
//		    for (var neighbour : hex.getNeighbours(GameConstants.nbrRows, GameConstants.nbrCols)) {
//			board[neighbour.getRow()][neighbour.getColumn()].setFill(Color.LIGHTPINK);
//		    }
		    mevt.consume(); // btw prevents panning
		});
//		board[row][col].setOnMouseReleased(mevt -> {
//		    var hex = (Hexagon) mevt.getSource();
//		    for (var neighbour : hex.getNeighbours(GameConstants.nbrRows, GameConstants.nbrCols)) {
//			board[neighbour.getRow()][neighbour.getColumn()].resetColour();
//		    }
//		 mevt.consume(); // btw prevents panning
//		});
		board[row][col].setOnMouseEntered(mevt -> {
		    game.onMouseEntered((Hexagon) mevt.getSource());
		    mevt.consume();
		});
		board[row][col].setOnMouseExited(mevt -> {
		    game.onMouseExited((Hexagon) mevt.getSource());
		    mevt.consume();
		});
	    }
	}
    }

    public static void main(String[] args) {
	Application.launch(args);
    }

}