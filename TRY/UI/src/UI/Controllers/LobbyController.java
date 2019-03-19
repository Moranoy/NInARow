package UI.Controllers;

import NinaRowHTTPClient.Lobby.ILobbyClientLogicDelegate;
import NinaRowHTTPClient.Lobby.LobbyClientLogic;
import UI.UIMisc.GameDescriptionData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class LobbyController implements ILobbyClientLogicDelegate {

    private String mOnlineUserName;
    private Consumer<GameDescriptionData> mOnEnteringGame;
    private Runnable mOnLogout;
    private LobbyClientLogic mLobbyClientLogic;

    @FXML
    private ScrollPane muiScrollPane;

    @FXML
    private BorderPane muiBorderPane;

    @FXML
    private HBox muiGameOptionsTopTab;

    @FXML
    private Button muiLogoutBtn;

    @FXML
    private VBox muiOnlinePlayers;

    @FXML
    private Text muiOnlinePlayerTitle;

    @FXML
    private FlowPane muiGameDetailsFlowPane;

    @FXML
    private void initialize() {
        this.muiLogoutBtn.setOnMouseClicked(
                (e) -> this.mLobbyClientLogic.logout(this.mOnlineUserName)
        );

        this.setUI();
        this.mLobbyClientLogic = new LobbyClientLogic(this);
        this.initFetchingPlayers();
        this.initFetchingGames();
    }

    private void setUI() {
        this.muiScrollPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void initFetchingPlayers() {
        this.mLobbyClientLogic.observeOnlinePlayerNames();
    }

    public void setmOnlineUserName(String mOnlineUserName) {
        this.mOnlineUserName = mOnlineUserName;
    }


    private void initFetchingGames() {
        this.mLobbyClientLogic.observeGames();
    }

    private void onJoinGameClick(GameDescriptionData gameDescriptionData) {
        this.mLobbyClientLogic.joinGameWithData(this.mOnlineUserName, gameDescriptionData);
    }

    public void setmOnEnteringGame(Consumer<GameDescriptionData> mOnEnteringGame) {
        this.mOnEnteringGame = mOnEnteringGame;
    }

    public void setmOnLogout(Runnable mOnLogout) {
        this.mOnLogout = mOnLogout;
    }

    @Override
    public void onPlayerNamesUpdate(List<String> playerNames) {
        Platform.runLater(
                () -> {
                    this.muiOnlinePlayers.getChildren().clear();
                    this.muiOnlinePlayers.getChildren().add(this.muiOnlinePlayerTitle);
                    playerNames.forEach(
                            (name) -> {
                                Label nameLabel = new Label(name);
                                this.muiOnlinePlayers.getChildren().add(nameLabel);
                            }
                    );
                }
        );
    }

    @Override
    public void onErrorUpdatingPlayerNames(String errorMessage) {
        //TODO
    }

    @Override
    public void onGamesUpdate(List<GameDescriptionData> updatedGamesList) {
        Platform.runLater(
                () -> {
                    this.muiGameDetailsFlowPane.getChildren().clear();
                    GameDescriptionController gameController;

                    for(int i = 0; i < updatedGamesList.size(); i++) {
                        gameController = new GameDescriptionController(updatedGamesList.get(i), this::onJoinGameClick);
                        this.setGameDataUIInParentView(gameController, i);
                        this.muiGameDetailsFlowPane.getChildren().add(gameController.getRoot());
                    }
                }
        );
    }

    @Override
    public void onErrorUpdatingGames(String errorMessage) {
        //TODO:
    }

    @Override
    public void onLogoutFinish() {
        Platform.runLater(this.mOnLogout);
    }

    @Override
    public void onJoinGameSuccess(GameDescriptionData gameDescriptionData) {
        Platform.runLater(
                () -> this.mOnEnteringGame.accept(gameDescriptionData)
        );
    }

    @Override
    public void onJoinGameFailure(String errorMessage) {
        //TODO: notify user.
    }

    // UI

    private void setGameDataUIInParentView(GameDescriptionController gameController, int index) {
        FlowPane.setMargin(gameController.getRoot(), new Insets(5, 3, 5, 3));
        ((HBox)gameController.getRoot()).setSpacing(10);
        Color bgColor = index % 2 == 0 ? Color.LIGHTBLUE : Color.MEDIUMPURPLE;
        ((HBox)gameController.getRoot()).setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
