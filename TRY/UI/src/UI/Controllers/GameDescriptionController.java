package UI.Controllers;

import Logic.Enums.eGameState;
import UI.UIMisc.GameDescriptionData;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.function.Consumer;

public class GameDescriptionController {

    private HBox mRootHbox = new HBox();
    private Label mGameStateLabel = new Label();
    private Label mGameTypeLabel = new Label();
    private Label mBoardDimentionsLabel = new Label();
    private Label mParticipantsLabel = new Label();
    private Label mTargetLabel = new Label();
    private Label mGameNameLabel = new Label();
    private Label mUploaderNameLabel = new Label();
    private Button mJoinGameBtn = new Button("Join Game");

    private eGameState mGameState;
    private GameDescriptionData mGameDescriptionData;

    public GameDescriptionController(GameDescriptionData data, Consumer<GameDescriptionData> onJoinGame) {
        this.setData(data);
        this.handleGameState();
        this.addChildren();
        this.setUI();
        this.mJoinGameBtn.setOnMouseClicked(
                (e) -> onJoinGame.accept(this.mGameDescriptionData)
        );
    }

    public Node getRoot() {
        return this.mRootHbox;
    }

    private void setData(GameDescriptionData data) {
        this.mGameDescriptionData = data;
        this.mGameState = data.getmGameState();
        this.mGameStateLabel.setText(this.mGameState.name());
        this.mGameTypeLabel.setText(data.getmVariant().name());
        this.mBoardDimentionsLabel.setText(Integer.toString(data.getmRows()) + "x" + Integer.toString(data.getmColumns()));
        this.mParticipantsLabel.setText(Integer.toString(data.getmNumberOfPlayers()) + "/" + Integer.toString(data.getmMaxPlayers()));
        this.mTargetLabel.setText(Integer.toString(data.getmTarget()));
        this.mGameNameLabel.setText(data.getmGameName());
        this.mUploaderNameLabel.setText(data.getmUploaderName());
    }

    private void addChildren() {
        this.mRootHbox.getChildren().add(this.mGameStateLabel);
        this.mRootHbox.getChildren().add(this.mGameTypeLabel);
        this.mRootHbox.getChildren().add(this.mBoardDimentionsLabel);
        this.mRootHbox.getChildren().add(this.mParticipantsLabel);
        this.mRootHbox.getChildren().add(this.mTargetLabel);
        this.mRootHbox.getChildren().add(this.mGameNameLabel);
        this.mRootHbox.getChildren().add(this.mUploaderNameLabel);
        this.mRootHbox.getChildren().add(this.mJoinGameBtn);
    }

    private void handleGameState() {
        if(!this.mGameState.equals(eGameState.Inactive)) {
            // When game is not inactive, other players can't join.
            this.mJoinGameBtn.setVisible(false);
        }
    }

    private void setUI() {
        this.mRootHbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}
