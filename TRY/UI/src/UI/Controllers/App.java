package UI.Controllers;

import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;
import NinaRowHTTPClient.Game.IGameClientLogicDelegate;
import Logic.Models.Cell;
import Logic.Models.PlayTurnParameters;
import Logic.Models.PlayedTurnData;
import Logic.Models.Player;
import NinaRowHTTPClient.Game.GameClientLogic;
import UI.ChangeListeners.TextChangingListeners;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import UI.UIMisc.FinalSettings;
import UI.Replay.ReplayTurnDataAdapter;
import UI.Theme.Theme;
import UI.UIMisc.GameDescriptionData;
import UI.UIMisc.ImageManager;
import UI.UIMisc.eInvalidMoveType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import UI.Theme.eThemeType;
import javafx.event.ActionEvent;

import java.util.*;
import java.util.stream.Collectors;

import static UI.UIMisc.FinalSettings.*;

public class App implements IBoardControllerDelegate, IGameClientLogicDelegate {

    @FXML private ScrollPane mScrollPane;
    @FXML private AnchorPane mAnchorPane;
    @FXML private VBox mVBoxPlayerDetails;
    @FXML private BorderPane mBorderPane;
    @FXML private GridPane mGridPaneConfig;
    @FXML private Button mBtnQuitGame;
    @FXML private VBox mVBoxGameDetails;
    @FXML private Label mLblTurnNumber;
    @FXML private Label mLblTargetSize;
    @FXML private Label mLblVariant;
    @FXML private ComboBox<eThemeType> mComboBoxTheame;

    // On player quit game
    private Runnable mOnCurrentPlayerLeftGame;

    private String mLoggedInPlayerName;

    public void setOnPlayerLeftGame(Runnable mOnPlayerQuitGame) {
        this.mOnCurrentPlayerLeftGame = mOnPlayerQuitGame;
    }

    private Button mBtnReplay = new Button("Start Replay");
    private Button mBtnBackReplay = new Button("Back");
    private Button mBtnForwardReplay = new Button("Forward");
    private BoardController mBoardController;

    // Game details (left pan)
    private IntegerProperty mCurrentTurnProperty;

    //PlayerDetails (right pane)
    private PlayerDetailsController mPlayerDetailsController;
    private GameClientLogic mGameClientLogic;

    // Replay
    private BooleanProperty mIsReplayInProgressProperty = new SimpleBooleanProperty();
    private ReplayTurnDataAdapter mReplayAdapter = new ReplayTurnDataAdapter();

    //States
    private BooleanProperty mIsGameActiveProperty = new SimpleBooleanProperty();
    private BooleanProperty mIsMyTurnProperty = new SimpleBooleanProperty();

    // Change listeners
    private TextChangingListeners mReplayTextChangingListener;

    // Themes
    private Theme mTheme;

    public App() {
        this.mPlayerDetailsController = new PlayerDetailsController();
        this.mCurrentTurnProperty = new SimpleIntegerProperty();
        this.mTheme = new Theme();
        this.mReplayTextChangingListener = new TextChangingListeners(FinalSettings.REPLAY_STOP_BTN_TEXT,
                FinalSettings.REPLAY_START_BTN_TEXT,
                (str) -> this.mBtnReplay.setText(str)
        );
    }

    // Use this function instead of initialize in order to be able to pass in the gameData param.
    // This param is needed to initialize the UI, and if initialize is used through the loader, passing the param would not be possible.
    public void init(GameDescriptionData gameData, String loggedInPlayerName) {
        this.mLoggedInPlayerName = loggedInPlayerName;
        this.mGameClientLogic = new GameClientLogic(gameData, this.mLoggedInPlayerName, this);
        this.mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.mScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.mBorderPane.setRight(this.mVBoxPlayerDetails);
        this.mPlayerDetailsController.setVBox(mVBoxPlayerDetails);
        this.initTopPane();
        this.initGameDetailsPane();
        this.initBoardPane();
        this.initReplay();
        this.initBinding();
        this.setDefaultDesign();
        this.mIsGameActiveProperty.setValue(false);
    }

    private void initTopPane() {
        this.mComboBoxTheame.getItems().addAll(eThemeType.Default, eThemeType.Aviad, eThemeType.Binsk);
        this.mBtnQuitGame.disableProperty().bind(Bindings.and(this.mIsMyTurnProperty.not(), this.mIsGameActiveProperty));
        this.mBtnQuitGame.setOnMouseClicked(this::onPlayerQuit);
    }

    private void initGameDetailsPane() {
        this.mLblTargetSize.setText("Target: " + Integer.toString(this.mGameClientLogic.getmGameData().getmTarget()));
        this.mLblVariant.setText("Variant: " + this.mGameClientLogic.getmGameData().getmVariant());
        this.mCurrentTurnProperty.setValue(0);
        this.mLblTurnNumber.textProperty().bind(
                Bindings.concat(
                        "Turn number: ",
                        this.mCurrentTurnProperty
                )
        );
    }

    private void initBoardPane() {
        this.mBoardController = new BoardController(this.mGameClientLogic.getmGameData().getmRows(), this.mGameClientLogic.getmGameData().getmColumns(), this);
        this.mBoardController.InitBoard();
        this.mBorderPane.setCenter(this.mBoardController.getBoardPane());
    }


    private void setDefaultDesign() {
        this.mVBoxGameDetails.setId("mVBoxGameDetails");
        changeOpacity(LOW_OPACITY);
        this.mTheme.setDefaultTheme();
        setTheme(eThemeType.Default);
        changeTheme();
    }

    private void initBinding() {
        //disabled binding
        this.mBtnReplay.disableProperty().bind(this.mIsGameActiveProperty.not());
        this.mBtnBackReplay.disableProperty().bind(
                Bindings.or(this.mIsReplayInProgressProperty.not(), this.mReplayAdapter.getCurrentTurnNumberInReplayProperty().isEqualTo(0)));

        // Text listeners
        this.mIsReplayInProgressProperty.addListener(this.mReplayTextChangingListener);
    }

    private void onPlayerQuit(MouseEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Exit Game Button pressed");
        alert.setContentText("Are you sure you want to exit game?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            this.performPlayerQuit();
        }
    }

    private void performPlayerQuit() {
        if(this.mGameClientLogic.isGameActive()) {
            // Game is active, player chose to quit during his turn.
            PlayTurnParameters playTurnParameters = new PlayTurnParameters(eTurnType.PlayerQuit);
            this.playTurn(playTurnParameters);
        } else {
            // Game is not active yet, player wishes to quit before it starts.
            this.mGameClientLogic.quitBeforeGameStarts();
        }

        this.mOnCurrentPlayerLeftGame.run();
    }


    private void initImageManagerWithPlayerImages() {
        List<String> playerIDs = new ArrayList<>();
        ImageManager.Clear(); // Reset existing images if there are any.

        this.mGameClientLogic
                .getPlayers()
                .forEach(
                        player -> playerIDs.add(player.getName())
                );

        ImageManager.SetImagesForPlayerIDs(playerIDs);
    }

    // Replay

    private void initReplay() {
        // On clicks
        this.mBtnReplay.setOnMouseClicked(this::onReplayButtonClick);
        this.mBtnBackReplay.setOnMouseClicked(this::onBackReplayButtonClick);
        this.mBtnForwardReplay.setOnMouseClicked(this::onForwardReplayButtonClick);

        this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        this.mGridPaneConfig.add(this.mBtnBackReplay, 4, 0);
        this.mGridPaneConfig.add(this.mBtnReplay, 5, 0);
        this.mGridPaneConfig.add(this.mBtnForwardReplay, 6, 0);
    }

    private void onReplayButtonClick(MouseEvent mouseEvent) {
        if(this.mIsReplayInProgressProperty.getValue()) {
            // Replay is in progress - User wants to end replay.
            this.mReplayAdapter.getAllNextTurnsCollection().forEach(this::handleUIAfterPlayedTurns);
            this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        } else {
            // Replay is not in progress - User wants to start replaying.
            this.mReplayAdapter.start(this.mGameClientLogic.GetTurnHistory());
            this.mBoardController.disableAllPopoutButtons();
            // Replay just begun, we start at the final turn - can't go forward in replay.
            this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        }

        // Toggle boolean property
        this.mIsReplayInProgressProperty.setValue(!this.mIsReplayInProgressProperty.getValue());
    }

    private void onBackReplayButtonClick(MouseEvent mouseEvent) {
        this.mBtnForwardReplay.setDisable(false); // Manually set forward button's disableness
        if(this.mReplayAdapter.hasPrevious()) {
            PlayedTurnData previousTurnData = this.mReplayAdapter.getPreviousTurnData();
            this.handleUIAfterPlayedTurns(previousTurnData, true);
        }
    }

    private void onForwardReplayButtonClick(MouseEvent mouseEvent) {
        PlayedTurnData nextTurnData = this.mReplayAdapter.getNextTurnData();
        this.handleUIAfterPlayedTurns(nextTurnData);

        if(!this.mReplayAdapter.hasNext()) {
            // Button will be disabled after this click.
            this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        }
    }

    // IHTTPClientDelegate implementation

    @Override
    public void myTurnStarted() {
        Platform.runLater(
                () -> this.mIsMyTurnProperty.setValue(true)
        );
    }

    @Override
    public void myTurnFinished() {
        Platform.runLater(
                () -> {
                    this.mBoardController.disableAllPopoutButtons();
                    this.mIsMyTurnProperty.setValue(false);
                }
        );
    }

    @Override
    public void gameReset() {
        this.clear();
    }

    @Override
    public void gameStarted() {
        setLabelsStyle(LABEL_STYLE_DEFAULT);
        Platform.runLater(
                () -> {
                    this.showAlert("The game has begun!", "Best of luck.");
                    this.mGameClientLogic.resetCurrentTurnCounter();
                    this.mBoardController.getBoardPane().setDisable(false);
                    this.mBoardController.ResetBoard();
                    this.initImageManagerWithPlayerImages(); // Do this when game starts because that's when all players are present.
                    this.mCurrentTurnProperty.setValue(0);
                    this.mIsGameActiveProperty.setValue(true);
                }
        );
    }

    @Override
    public void gameEnded(eGameState gameEndedState) {
        Platform.runLater(
                () -> {
                    this.mIsGameActiveProperty.set(false);
                    this.handleGameStateEvents(gameEndedState);
                }

        );
    }

    @Override
    public void updatePlayers(List<Player> playerList) {
        Platform.runLater(
                () -> this.mPlayerDetailsController.setPlayersDetails(playerList)
        );
    }

    @Override
    public void onTurnPlayed(PlayedTurnData playedTurnData, String currentPlayerName) {
        Platform.runLater(
                () -> {
                    this.mPlayerDetailsController.setCurrentPlayerName(currentPlayerName);
                    this.handleUIAfterPlayedTurns(playedTurnData);
                }
        );
    }

    @Override
    public void discAddedToFullColumn(int column) {
        Platform.runLater(
                () -> {
                    try {
                        this.mBoardController.handelInvalidAction(column, eInvalidMoveType.ColumnFull); //notify user
                    } catch (InterruptedException e) {
                        System.out.println("SleepError. " + e.getMessage());
                    }
                }
        );
    }

    @Override
    public void currentPlayerCannotPopoutAtColumn(int column) {
        Platform.runLater(
                () -> {
                    try {
                        this.mBoardController.handelInvalidAction(column, eInvalidMoveType.InvalidPopout);
                    } catch (InterruptedException e) {
                        System.out.println("SleepError. " + e.getMessage());
                    }
                }
        );
    }

    @Override
    public void onPlayerToWinningSequenceUpdate(Map<Player, Collection<Cell>> playerToWinningSequenceMap) {
        Platform.runLater(
                () -> {
                    if(playerToWinningSequenceMap != null) {
                        this.mBoardController.DisplayWinningSequences(playerToWinningSequenceMap);
                        gameWonMsg(playerToWinningSequenceMap.keySet());
                    } else {
                        showAlert("Game was won!", "Please wait for a new game to start or exit.");
                    }
                }
        );
    }

    @Override
    public void onAvailablePopoutColumnsUpdate(Collection<Integer> availablePopoutColumns) {
        Platform.runLater(
                () -> handlePopoutUIIfNeeded(availablePopoutColumns)
        );
    }

    // IBoardControllerDelegate implementation

    @Override
    public void PopoutBtnClicked(int btnIndex) {
        PlayTurnParameters playTurnParameters = new PlayTurnParameters(btnIndex, eTurnType.Popout);
        this.playTurn(playTurnParameters);
    }

    @Override
    public void ColumnClicked(int columnIndex) {
        PlayTurnParameters playTurnParameters = new PlayTurnParameters(columnIndex, eTurnType.AddDisc);
        this.playTurn(playTurnParameters);
    }

    @Override
    public boolean isPopoutAllowed() {
        return this.mGameClientLogic.isPopoutAllowed();
    }

    private void playTurn(PlayTurnParameters playTurnParameters)  {
        playTurnParameters.setmPlayerName(this.mLoggedInPlayerName);

        if(this.mGameClientLogic.isGameActive()) { // Check if game is active.
            if(this.mIsMyTurnProperty.get()) { // Check if its the user's turn.
                if(!this.mIsReplayInProgressProperty.getValue()) { // Check if a replay is in progress.
                    this.mGameClientLogic.playTurnAsync(playTurnParameters); // Play turn.
                } else {
                    // Handle replay in progress.
                    this.showAlert("Oh no you didn't", "Cannot play turn while replay is in progress.");
                }
            } else {
                // Handle not the user's turn.
                this.showAlert("Follow the damn rules!", "Cannot play turn while another precious soul is playing.");
            }
        } else {
            // Handle game is no longer active.
            this.showAlert("Cannot play turn after the game has ended.", "Either restart or move on with your life.");
        }
    }

    private void handleUIAfterPlayedTurns(PlayedTurnData turnData) {
        this.handleUIAfterPlayedTurns(turnData, false);
    }

    private void handleUIAfterPlayedTurns(PlayedTurnData turnData, boolean isReverseTurn) {
        this.handleBoardControllerUIAfterTurn(turnData);
        //this.handlePlayerDetailsControllerUIAfterTurn(turnData, isReverseTurn); no need to use this in Ex03. Instead, current player is updated every time players are fetched.
        this.handleGameDetailsUIAfterTurn(isReverseTurn);
    }

    private void handleGameStateEvents(eGameState gameState) {
        if (gameState.equals(eGameState.Draw)) {
            this.showAlert("Draw!", "The game has ended in a draw. Please wait for a new game to start or exit.");
        }
    }

    private void handleGameDetailsUIAfterTurn(boolean isReverseTurn) {
        if(isReverseTurn) {
            this.mCurrentTurnProperty.set(this.mCurrentTurnProperty.getValue() - 1);
        } else {
            this.mCurrentTurnProperty.set(this.mCurrentTurnProperty.getValue() + 1);
        }
    }

    private void handlePlayerDetailsControllerUIAfterTurn(PlayedTurnData turnData, boolean isReverseTurn) {
        if(turnData.getTurnType().equals(eTurnType.PlayerQuit)) {
            this.mPlayerDetailsController.playerQuit();
        } else {
            if (isReverseTurn) {
                this.mPlayerDetailsController.updateToPreviousTurn();
            } else {
                this.mPlayerDetailsController.updateToNextTurn();
            }
        }
    }

    private void handleBoardControllerUIAfterTurn(PlayedTurnData turnData) {
        if(turnData.getUpdatedCellsCollection() != null) {
            // Update board if needed.
            this.mBoardController.UpdateDiscs(turnData.getUpdatedCellsCollection());
            this.mBoardController.UpdateDiscs(turnData.getUpdatedCellsCollection());
        }
    }

    private void handlePopoutUIIfNeeded(Collection<Integer> availablePopoutColumns) {
        boolean shouldEnablePopoutButtons = !this.mIsReplayInProgressProperty.getValue();

        if(shouldEnablePopoutButtons) {
            // Enable popout buttons that the user is allowed to click.
            this.mBoardController.enablePopoutButtonsForColumns(availablePopoutColumns); // Response through delegate.
        }
    }


    private void gameWonMsg(Set<Player> winnersSet) {
        Set<String> winnerNamesSet = winnersSet.stream().map(Player::getName).collect(Collectors.toSet());
        final String namesSeporator = ", ";
        String winnersNames = String.join(namesSeporator, winnerNamesSet);
        String bodyString = "The winners are " + winnersNames + "." + System.lineSeparator() + "Please start a new game or exit.";
        this.showAlert("The game was won!", bodyString);
    }

    private void showAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(body);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ //user chose ok
            alert.close();
        }
    }

    private void clear() {
        Platform.runLater(
                () -> {
                    this.mIsGameActiveProperty.setValue(false);
                    ImageManager.Clear();
                    this.mPlayerDetailsController.clear();
                    this.mCurrentTurnProperty.set(0);
                    this.mBoardController.ResetBoard();
                    this.mTheme.setAviadTheme();
                }
        );
    }


    // Theme

    public void changeTheme(){
        this.mBorderPane.setStyle(this.mTheme.getCurrentThemeBackground());
    }

    public void onComboBoxItemChange(ActionEvent actionEvent) {
        if (this.mComboBoxTheame.getSelectionModel().getSelectedItem().equals(eThemeType.Aviad)){
            this.mTheme.setAviadTheme();
            setTheme(eThemeType.Aviad);
        }
        else if (this.mComboBoxTheame.getSelectionModel().getSelectedItem().equals(eThemeType.Binsk)){
            this.mTheme.setGuyTheme();
            setTheme(eThemeType.Binsk);
        } else{
            this.mTheme.setDefaultTheme();
            setTheme(eThemeType.Default);
        }
        changeTheme();
        if (this.mPlayerDetailsController != null){
            this.mPlayerDetailsController.markCurrentPlayer();
        }

    }

    public void changeOpacity(double opacity){
        this.mVBoxGameDetails.setOpacity(opacity);
        this.mPlayerDetailsController.setOpacity(opacity);
    }

    private void setTheme(eThemeType themeType){
        if (themeType.equals(eThemeType.Aviad)){
            this.mVBoxGameDetails.setStyle(VBOX_STYLE_AVIAD);
            this.mPlayerDetailsController.setTheme(PLAYERS_PANE_STYLE_AVIAD + FONT_AVIAD);
            setButtonsFill(BUTTON_GRADIENT_AVIAD + FONT_AVIAD);
            setLabelsStyle(LABEL_STYLE_AVIAD + FONT_AVIAD);
        }else if (themeType.equals(eThemeType.Binsk)){
            this.mVBoxGameDetails.setStyle(VBOX_STYLE_Guy);
            this.mPlayerDetailsController.setTheme(PLAYERS_PANE_STYLE_Guy + FONT_Guy);
            setButtonsFill(BUTTON_GRADIENT_Guy + FONT_Guy);
            setLabelsStyle(LABEL_STYLE_Guy + FONT_Guy);
        }else{
            this.mVBoxGameDetails.setStyle(VBOX_STYLE_DEFAULT);
            this.mPlayerDetailsController.setTheme(PLAYERS_PANE_STYLE_DEFAULT + FONT_DEFAULT);
            setButtonsFill(BUTTON_GRADIENT_DEFAULT + FONT_DEFAULT);
            setLabelsStyle(LABEL_STYLE_DEFAULT + FONT_DEFAULT);
        }
    }

    private void setButtonsFill(String style) {
        this.mComboBoxTheame.setStyle(style);
        this.mBtnReplay.setStyle(style);
        this.mBtnForwardReplay.setStyle(style);
        this.mBtnBackReplay.setStyle(style);
    }

    private void setLabelsStyle(String style){
        this.mLblTargetSize.setStyle(style);
        this.mLblTurnNumber.setStyle(style);
        this.mLblVariant.setStyle(style);
        this.mPlayerDetailsController.setLabelsStyle(style);
    }

    public void resizeHeight(ReadOnlyDoubleProperty newSize){
        this.mBorderPane.prefHeightProperty().bind(newSize);
    }

    public void resizeWidth(ReadOnlyDoubleProperty newSize){
        this.mBorderPane.prefWidthProperty().bind(newSize);
    }

    public void QuitBtnClicked(MouseEvent mouseEvent) {

    }
}
