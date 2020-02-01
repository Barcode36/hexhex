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

    private static final String GAMEPANE_STYLE_CLASS = "gamepane";

    final int sizeOfHexagons = GameProperties.instance().getPropertyInt(Hexagon.HEXAGON_SIZE);

    private final static int widthOfMainPane = GameProperties.instance().getPropertyInt(GameProperties.MAIN_PANE_WIDTH);
    private final static int heightOfMainPane = GameProperties.instance()
	    .getPropertyInt(GameProperties.MAIN_PANE_HEIGHT);

    private Hexagon[][] board;
    private Game game;

    private MouseEvent mousePosition;

    @Override
    public void start(Stage stage) {

	Hexagon.size = sizeOfHexagons;
	Hexagon.X_OFFSET = GameProperties.instance().getPropertyInt(Hexagon.HEXAGON_X_OFFSET);
	Hexagon.Y_OFFSET = GameProperties.instance().getPropertyInt(Hexagon.HEXAGON_Y_OFFSET);

	Pane pane = new Pane();
	initGame(pane);
	initBoard();
	game.setBoard(board);

	for (int row = 0; row < GameProperties.NBR_ROWS; row++) {
	    for (int col = 0; col < GameProperties.NBR_COLS; col++) {
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
	sp.getStyleClass().clear();
	sp.getStyleClass().add(GAMEPANE_STYLE_CLASS);

	Scene scene = new Scene(sp, widthOfMainPane, heightOfMainPane);
	scene.getStylesheets().add("/de/rjo/game/hexhex.css");
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
	game = new Game(pane);
    }

    private void initBoard() {
	board = new Hexagon[GameProperties.NBR_ROWS][GameProperties.NBR_COLS];

	for (int row = 0; row < GameProperties.NBR_ROWS; row++) {
	    for (int col = 0; col < GameProperties.NBR_COLS; col++) {
		board[row][col] = new Hexagon(row, col, game.getState()[row][col].getTeam().getStyleClass());

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