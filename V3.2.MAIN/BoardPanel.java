import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BoardPanel extends Application {

    /** Instance variables. The first four are to initialize various objects that the game needs to run,
     * while the bottom four are variables consistently used and changed throughout the usage of the GUI. */
    private Map map;
    private MetaGame startGame = new MetaGame();
    private Game game = new Game();
    private Pieces pieceLists = new Pieces();
    private HumanPlayerGUI human;
    private Turn turn;
    
    public static boolean midMove = false;
    public static boolean midAtk = false;
    private static int piecesPlaced = 0;
    private String partyDisplay;
    private String enemyDisplay;

    public Label toPlayer = new Label("Welcome!");
    private String submessage = "---------";
    private String message;
    private Button boardButton;
    private Button move;
    private Button attackOrder;
    private Button heal;
    private Button updateState;
    private Button viewParty;
    private Button viewEnemies;
    private GridPane grid = new GridPane();
    
    /** This method sets up the appearance of the GUI itself, while also eventhandling when boardButton,
     * viewParty, viewEnemies, and endTurn buttons are clicked.
     *
     * Note: this method is incomplete in terms of running the game. Currently, the GUI can place AI pieces, allow the player to place pieces, allow the player to move and attack and heal.
     * The only functionality that is missing is checking if the game has been won, and there are sitll various bugs present.
     *
     * Note 2: the GUI version of the game differs fromt the console-based. It uses a different command in MetaGame, different methods in Game, and HumanPlayerGUI (HPG) rather than HumanPlayer.*/
    @Override
    public void start(Stage primaryStage) {
        /*These are the primary layouts used in the GUI.*/
        BorderPane root = new BorderPane();
        VBox topSection = new VBox(0);
        HBox underTop = new HBox();

        /*Setting up the very important toPlayer label, which is set to change the message when most
        buttons are pressed.*/
        toPlayer.setFont(Font.font("Courier New", 18));
        //toPlayer.setTextFill(Color.WHITE);
        toPlayer.setAlignment(Pos.CENTER);

        Button endTurn = new Button("End Turn");
        viewParty = new Button("View party");
        viewEnemies = new Button("View enemies");
        move = new Button("Move");
        attackOrder = new Button("Attack");
        heal = new Button("Heal");
        updateState = new Button("Update state");

        HBox rightButton = new HBox(move, attackOrder, heal, updateState);
        rightButton.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightButton, Priority.ALWAYS);

        underTop.getChildren().addAll(endTurn, viewParty, viewEnemies, rightButton);
        underTop.setPadding(new Insets(2));

        topSection.getChildren().addAll(toPlayer, underTop);

        grid.setVgap(20);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);

        //C O M M U N I C A T I O N
        gamePlaying();
        update();

        topSection.setPadding(new Insets(10, 30, 30, 30));
        topSection.setSpacing(5);

        root.setPadding(new Insets(30,30,30,30));
        root.setCenter(grid);
        root.setTop(topSection);
        //root.setStyle("-fx-background-color: BLACK;");

        Scene scene = new Scene(root, 1600, 600);
        primaryStage.setTitle("Planet Invaders");
        primaryStage.setScene(scene);
        primaryStage.show();


        //E V E N T H A N D L I N G
        /**This updates the appearance of the GUI based on the current information of the game.*/
        updateState.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                update();
            }
        });

        /*This is for the endTurn button. This is what the user presses to end their turn.*/
        endTurn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (game.getGameDone() == 2) {
                    toPlayer.setText("Victory!");
                }
                if (game.getturncounter() != 0) {
                //if(game.getTurnCounter() < game.getTotalTurns() && game.getGameDone() == 1) {
                    System.out.println("total turns: " + map.getTurns());
                    game.oneLessTurn();
                    System.out.println("has won: " +game.hasWon());
                    game.setGameDone(game.hasWon());
                    System.out.println("Gamedone from BP: " + game.getGameDone());
                    if (game.getGameDone() == 1) {

                        //if (game.getturncounter() != map.getTurns()) {
                        //game.setTurnCounter();
                        //game.setturncounter();
                        human.resetTurn();
                        //System.out.println("Turn from BP: " + game.getTurnCounter());
                        System.out.println("Turn from BP: " + game.getturncounter());
                        toPlayer.setText("== AI ATTACK AND SETUP == ");
                        game.play();
                        if (game.getGameDone() == 1) {
                            toPlayer.setText("HUMAN TURN... Click tile with desired piece to execute turn.");
                            //game.setGameDone(game.hasWon());

                        }

                    //}
                    // else {
                    //game.setturncounter();
                    //}
                    }
                }
            }
        });
    }

    /**This method is used to instantiate the objects necessary to start the game so that the game can be run through the same class as the GUI
     * (this serves to start the GUI and the game itself simultaneously)
     * All these commands are meant to set the game up, before the turn loop runs.*/
    public void gamePlaying() {
        this.game = startGame.startgame("one","one");
        this.pieceLists = game.getPieces();
        this.map = game.getMap();
        game.placeAIPieces();
        this.turn = new Turn(getMap(), getPieceLists());
        this.human = new HumanPlayerGUI(this.map, this.pieceLists);
        if (game.getTurnCounter() == 0) {
            toPlayer.setText("Place your pieces! ");
        }
    }

    /**This method is necessary because the row and column numbers of the grid don't correspond to anything.
     * Thus, this method converts the row and column numbers into one integer that increases for each button added.
     *@param rowIndex . This is the row the button is in.
     *@param columnIndex . This is the column the button is in.
     *@param dimensions . This is the dimensions of the map itself.*/
    public int conversion(int rowIndex, int columnIndex, int dimensions) {
        return dimensions - (dimensions - columnIndex - 1) + dimensions*rowIndex;
    }

    /**Overall, this methods updates the appearance of the GUI according to the information of the game (from changes in the map due to movement, etc).*/
    public void update() {
        map.displayMap();
        updateGrid();
        updateDisplay(1);
        updateDisplay(2);
        /* This is for the viewParty button */
        //Events viewPartyEvent = new Events("party", getToPlayer(),getPieceLists());
        Events viewPartyEvent = new Events("party", getToPlayer(), getPartyDisplay());
        viewParty.setOnAction(viewPartyEvent);
        /*This is for the viewEnemies button*/
        //Events viewEnemyEvent = new Events("enemy", getToPlayer(),getPieceLists());
        Events viewEnemyEvent = new Events("enemy", getToPlayer(), getEnemyDisplay());
        viewEnemies.setOnAction(viewEnemyEvent);
    }

    /*This is the initial map creation. The loop is supposed to create buttons in a grid (using GridPane),
    with the rows and columns equal to the map dimensions. This map gets created repeatedly as the state of Map changes.*/
    public void updateGrid() {
        for(int row = 0; row < map.getDimensions(); row++) {
            for(int col = 0; col < map.getDimensions(); col++) {
                grid.setStyle("-fx-background-color: GREEN;");
                int place = conversion(row,col,map.getDimensions()); //This runs the method conversion() in this class. (See below)
                findPieces(place-1); //This runs the method findAIPieces() in this class. (See below)
                setMessage(place + ", " + this.submessage);
                Button boardButton = new Button(message);
                grid.add(boardButton, col, row);
                boardButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //if (game.getTurnCounter() == 0) {
                        if (game.getturncounter() == map.getTurns()) {
                            if (buttonAction(place)) {
                                boardButton.setText(message);
                            }
                        }
                        else {
                            buttonActionAfter(place);
                            if (map.getPiece(place) == 0 && getMidMove() == true) {
                                System.out.println("Midmove success");
                                buttonActionMidMove(place);
                            }
                            else if (getMidAtk()) {
                                System.out.println("MidAtk success");
                                buttonActionMidMove(place);
                            }
                        }
                    }
                });
            }
        }
    }

    /**This goes hand-in-hand with placeAIPieces() in Game. This ensures that the enemy pieces are already placed when the map is created.
     *@param index . This is the converted number (using conversion()) of the location of the button.*/
    public void findPieces(int index) {
        ArrayList<ArrayList<Integer>> proxy = map.getMaparray(); //creates clone of map
        ArrayList<Integer> tileArray = proxy.get(index); //locates information on map on the specified tile (in argument)
        if (tileArray.get(1) != 0 && tileArray.get(1) > pieceLists.getHumanPieces().size()) { //tileArray.get(1) = index of piece in masterList
            submessage = "ENEMY " + tileArray.get(1);
        }
        else if (tileArray.get(1) != 0 && tileArray.get(1) <= pieceLists.getHumanPieces().size()) {
/*            if (tileArray.get(1) == 1) {
                Image combatMech = new Image("combatMech.png");
                boardButton.setGraphic(new ImageView(combatMech));
            }*/
            //else {
                submessage = "PIECE " + tileArray.get(1);
            //}
        }
        else {
            submessage = "---------";
        }
    }

    /**This updates the information in viewParty and viewEnemies based on the information on all the pieces.
     * @param party, this is the type of the piece (player or AI)*/
    public void updateDisplay(int party) {
        if (party == 1) {
            this.partyDisplay = "Party: ";
            for (int count = 0; count < pieceLists.getPlayerParty().size(); count++) {
                this.partyDisplay = this.partyDisplay + pieceLists.getMasterList().get(count).getName() + " (atk: " + pieceLists.getMasterList().get(count).getAtk() + ",hp: " + pieceLists.getMasterList().get(count).getHp() + ",AP: " + pieceLists.getMasterList().get(count).getAP() + ") ";
            }
        }
        if (party == 2) {
            this.enemyDisplay = "Enemies: ";
            for (int count = pieceLists.getAIPieces().size(); count < pieceLists.getMasterList().size(); count++) {
                this.enemyDisplay = this.enemyDisplay + pieceLists.getMasterList().get(count).getName() + " (atk: " + pieceLists.getMasterList().get(count).getAtk() + ",hp: " + pieceLists.getMasterList().get(count).getHp() + ",AP: " + pieceLists.getMasterList().get(count).getAP() + ") ";
            }
        }
    }

    /** This is the implementation of eventhandling for the boardButton at turn 0, since I couldn't get it to work in the GUI.*/
    public boolean buttonAction(int place) {
        boolean check = false;
        //For placing pieces
        if (piecesPlaced < pieceLists.getPlayerParty().size()) { //This ensures that pieces can only be placed before the game begins, and there can't be more pieces placed than permitted.
            if (map.getPiece(place) == 0 && place < map.getDimensions() * map.getDimensions() - map.getDimensions() * 3) { //This ensures that pieces can only be placed on empty spaces and not in the last 3 rows
                piecesPlaced++;
                System.out.println("Place: " + map.getPiece(place));
                map.setState(place, 1, piecesPlaced); //This sets the piece itself
                submessage = "PIECE " + map.getPiece(place);
                message = place + ", " + submessage;
                check = true;
                map.displayMap();
            }
            else {
                toPlayer.setText("Pieces are not permitted to be placed here.");
            }
        }
        else {
            toPlayer.setText("No more pieces are permitted to be placed.");
        }
        return check;
    }

    /** This is the implementation of eventhandling for the boardBUtton after turn 0; allows for movement and attacking.*/
    public void buttonActionAfter(int place) {
        if (map.getPiece(place) == 0) {
                toPlayer.setText("TILE " + place + " selected.");
                System.out.println(getMidMove());
        }
        else {
            if (human.startTurn(place)) {
                toPlayer.setText("Choose to MOVE, ATTACK, OR HEAL.");
                Events moveEvent = new Events(place, "move", getMap(), getGame(), getPieceLists(), getHuman(), getToPlayer(), getMidMove());
                Events attackOrderEvent = new Events(place, "attack", getMap(), getGame(), getPieceLists(), getHuman(), getToPlayer(), getMidAtk());
                Events healEvent = new Events("heal", getMap(), getGame(), getPieceLists(), getHuman(), getToPlayer());
                //Events updateStateEvent = new Events("endPiece", getMap(), getGame(), getPieceLists(), getHuman(), getToPlayer());
                move.setOnAction(moveEvent);
                attackOrder.setOnAction(attackOrderEvent);
                heal.setOnAction(healEvent);
                //updateState.setOnAction(updateStateEvent);
            }
        }
    }

    /** This is the implementation of eventhandling for the boardBUtton after tthe MOVE and ATTACK buttons are pressed; creates a new Events object so the piece can be moved/attack.*/
    public void buttonActionMidMove(int place) {
        Events move2Event = new Events(place, "move", getMap(), getGame(), getPieceLists(), getHuman(), getToPlayer(), getMidMove());
        move.setOnAction(move2Event);
        Events attackOrder2Event = new Events(place, "attack", getMap(), getGame(), getPieceLists(), getHuman(), getToPlayer(), getMidAtk());
        attackOrder.setOnAction(attackOrder2Event);
    }

    public Label getToPlayer() {
        return toPlayer;
    }

    public String getSubmessage() {
        return new String(this.submessage);
    }

    public String getMessage() {
        return new String(this.message);
    }

    public int getPiecesPlaced() {
        return new Integer(this.piecesPlaced);
    }

    public void setSubmessage(String submsg) {
        this.submessage = new String(submsg);
    }

    public void setMessage(String msg) {
        this.message = new String(msg);
    }

    public void setPiecesPlaced(int placed) {
        this.piecesPlaced = new Integer(placed);
    }

    public Map getMap() {
        return this.map;
    }
    public Game getGame() {
        return this.game;
    }
    public Pieces getPieceLists() {
        return this.pieceLists;
    }
    public HumanPlayerGUI getHuman() {
        return this.human;
    }

    public boolean getMidMove() {
        return this.midMove;
    }
    public void setMidMove(boolean bool) {
        this.midMove = bool;
    }
    public boolean getMidAtk() {
        return this.midAtk;
    }
    public void setMidAtk(boolean bool) {
        this.midAtk = bool;
    }

    public String getPartyDisplay() {
        return this.partyDisplay;
    }
    public String getEnemyDisplay() {
        return this.enemyDisplay;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
/*Extra stuff*/
/*METHOD #1: DOESN'T WORK*/
/*                if (game.getTurnCounter() == 0 && (this.piecesPlaced < pieceLists.getPlayerParty().size())) { //This ensures that pieces can only be placed before the game begins, and there can't be more pieces placed than permitted.
                    if (map.getPiece(place) == 0 && place < (map.getDimensions() * map.getDimensions() - map.getDimensions() * 3)) { //This ensures that pieces can only be placed on empty spaces and not in the last 3 rows
                        Events boardButtonEvent = new Events(place, "board", getMap(), getGame(), getPieceLists());
                        boardButton.setOnAction(boardButtonEvent);
                    }
                }*/
/*                  <FOR DEBUGGING THE LOOP>
                    if(game.getTurnCounter() < game.getTotalTurns() && game.getGameDone() == 1) {
                    System.out.println("Total turns: " + game.getTotalTurns());
                    game.setTurnCounter();
                    System.out.println("Turn from BP: " + game.getTurnCounter());
                    game.play();
                    System.out.println("Human goes...");
                }*/