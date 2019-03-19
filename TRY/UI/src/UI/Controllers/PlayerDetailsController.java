package UI.Controllers;

import Logic.Enums.ePlayerType;
import Logic.Models.Player;
import UI.UIMisc.ImageManager;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import static UI.UIMisc.FinalSettings.HIGH_OPACITY;
import static UI.UIMisc.FinalSettings.PLAYER_MARKED_LABEL;
import static UI.UIMisc.FinalSettings.REMOVE_PLAYER_MARKED_LABEL;

public class PlayerDetailsController {

    private VBox mVBoxPlayerDetails;
    private List<PlayerDetails> mPlayerDetailsList;
    private ListIterator<PlayerDetails> mPlayerDetailsIterator;
    private String mCurrentPlayerName;

    public PlayerDetailsController() {
            this.mVBoxPlayerDetails = new VBox();
            this.mPlayerDetailsList = new ArrayList<>();
            setDesign();
        }

    private void setDesign() {
        this.mVBoxPlayerDetails.setId("mVBoxPlayerDetails");
        this.mVBoxPlayerDetails.setOpacity(HIGH_OPACITY);
    }

    public void setPlayersDetails(Collection<Player> players){
        this.mPlayerDetailsList.clear();
        this.mVBoxPlayerDetails.getChildren().clear();
        players.forEach(
               player -> {
                   PlayerDetails playerDetails = this.createPlayerDetailsWithPlayer(player);
                   //this.mPlayerDetailsList.add(playerDetails); // Add to player details list.
                   this.mVBoxPlayerDetails.getChildren().add(playerDetails.getRoot()); // Add player details root to VBox.
               }
        );

        this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator();
    }

    private PlayerDetails createPlayerDetailsWithPlayer(Player player) {
        PlayerDetails playerDetails = new PlayerDetails(player);

        if(player.getName().equals(this.mCurrentPlayerName)) {
            playerDetails.markPlayerDetails();
        }

        return playerDetails;
    }

    public void updateToNextTurn(){
        PlayerDetails currentPlayerDetails = this.getNextPlayerDetails();

        currentPlayerDetails.incTurn();
        currentPlayerDetails.unMarkPlayerDetails();

        // Remove current player's details from the top of the "queue"
        this.mVBoxPlayerDetails.getChildren().remove(currentPlayerDetails.mVBox);

        // Add current player's details to the bottom of the players details "queue".
        this.mVBoxPlayerDetails.getChildren().add(currentPlayerDetails.mVBox);

        // Next player should automatically be at the top of the queue, no need to perform actions on the queue.
        PlayerDetails nextPlayerDetails = this.getNextPlayerDetailsWithoutMovingIterator();

        nextPlayerDetails.markPlayerDetails();
    }

    public void markCurrentPlayer(){
        try{
            this.getNextPlayerDetailsWithoutMovingIterator().markPlayerDetails();
        }catch (NullPointerException e){
        }
    }

    public void updateToPreviousTurn(){
        this.unmarkCurrentPlayer();

        PlayerDetails currentPlayerDetails = this.getPreviousPlayerDetailsWithoutMovingIterator();
        currentPlayerDetails.decTurn();

        // Get previous players details from the bottom of the queue.
        PlayerDetails previousPlayerDetails = this.getPreviousPlayersDetails();

        //Remove previous player details from the bottom of the queue.
        this.mVBoxPlayerDetails.getChildren().remove(previousPlayerDetails.getRoot());

        // Insert previous player at the top of the queue.
        this.mVBoxPlayerDetails.getChildren().add(0, previousPlayerDetails.getRoot());
        previousPlayerDetails.markPlayerDetails();
    }

    public void playerQuit() {
        PlayerDetails currentPlayerDetails = this.getNextPlayerDetails();

        // Remove current player's details from the top of the "queue"
        this.mVBoxPlayerDetails.getChildren().remove(currentPlayerDetails.mVBox);

        // Remove the last player that was returned by using "next()"
        this.mPlayerDetailsIterator.remove();
    }

    // Helper functions.
    private PlayerDetails getNextPlayerDetails() {
        PlayerDetails nextPlayerDetails;

        if(this.mPlayerDetailsIterator.nextIndex() < this.mPlayerDetailsList.size()) {
            nextPlayerDetails = this.mPlayerDetailsIterator.next(); // Get next player details.
        } else {
            // Set iterator to the start of the collection.
            this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator();
            nextPlayerDetails = this.mPlayerDetailsIterator.next(); // Get first player details.
        }

        return nextPlayerDetails;
    }

    private PlayerDetails getPreviousPlayersDetails() {
        PlayerDetails previousPlayerDetails;

        if(this.mPlayerDetailsIterator.previousIndex() >= 0) {
            previousPlayerDetails = this.mPlayerDetailsIterator.previous(); // Get previous player details.
        } else {
            // Set iterator to the end of the collection.
            this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator(this.mPlayerDetailsList.size());
            previousPlayerDetails = this.mPlayerDetailsIterator.previous(); // Get last player details.
        }

        return previousPlayerDetails;
    }

    private PlayerDetails getNextPlayerDetailsWithoutMovingIterator() {
        int nextPlayerDetailsIndex;

        if(this.mPlayerDetailsIterator.nextIndex() < this.mPlayerDetailsList.size()) {
            nextPlayerDetailsIndex = this.mPlayerDetailsIterator.nextIndex(); // Get next player details index.
        } else {
            // Get first player details.
            nextPlayerDetailsIndex = 0;
        }

        return this.mPlayerDetailsList.get(nextPlayerDetailsIndex);
    }

    private PlayerDetails getPreviousPlayerDetailsWithoutMovingIterator() {
        int previousPlayerDetailsIndex;

        if(this.mPlayerDetailsIterator.previousIndex() >= 0) {
            previousPlayerDetailsIndex = this.mPlayerDetailsIterator.previousIndex(); // Get previous player details index.
        } else {
            // Get last player details.
            previousPlayerDetailsIndex = this.mPlayerDetailsList.size() - 1;
        }

        return this.mPlayerDetailsList.get(previousPlayerDetailsIndex);
    }

    public void reset() {
        this.unmarkCurrentPlayer();

        // Reset order/details of player details inside the root node.
        this.mPlayerDetailsList.forEach(
                playerDetails -> {
                    playerDetails.mTurnNumber = 0; // Reset turn.
                    playerDetails.mTurnNumberLabel.setText("Turn number: " + Integer.toString(playerDetails.mTurnNumber));
                    this.mVBoxPlayerDetails.getChildren().remove(playerDetails.getRoot()); // Remove from vbox.
                    this.mVBoxPlayerDetails.getChildren().add(playerDetails.getRoot()); // Add to the end of the vbox.
                }
        );

        // Mark first player.
        this.mPlayerDetailsList.get(0).markPlayerDetails();
    }

    private void unmarkCurrentPlayer() {
        PlayerDetails nextPlayerDetails = this.getNextPlayerDetailsWithoutMovingIterator();
        nextPlayerDetails.unMarkPlayerDetails();
    }

    public void setTheme(String style) {
        this.mVBoxPlayerDetails.setStyle(style);
    }

    public void setOpacity(double opacity){
        this.mVBoxPlayerDetails.setOpacity(opacity);
    }

    public void setLabelsStyle(String newStyle) {
        this.mPlayerDetailsList.stream().forEach(
                playerDetails -> playerDetails.setLabelsStyle(newStyle)
        );
    }

    public void clear() {
        this.mVBoxPlayerDetails.getChildren().clear();
        this.mPlayerDetailsList.clear();
    }

    public void setVBox(VBox mVBoxPlayerDetails) {
        this.mVBoxPlayerDetails = mVBoxPlayerDetails;
    }

    public void setCurrentPlayerName(String currentPlayerName) {
        this.mCurrentPlayerName = currentPlayerName;
    }

    private class PlayerDetails {
        private VBox mVBox;
        private Label mName;
        private Label mType;
        private Label mTurnNumberLabel;
        private ImageView mDiscImg;
        private Separator mSeparator;
        private int mTurnNumber;

        private PlayerDetails(Player player) {
            this.mVBox = new VBox();
            this.mName = new Label(player.getName());
            this.mType = new Label(player.getType().equals(ePlayerType.Human) ? "Human" : "Computer");
            this.mDiscImg = new ImageView(ImageManager.getImageForPlayerID(player.getName()));
            this.mSeparator = new Separator();
            this.mTurnNumber = player.getTurnCounter();
            this.mTurnNumberLabel = new Label("Turn number: " + Integer.toString(this.mTurnNumber));

            this.initUI();
        }

        private void initUI() {
            this.mVBox.setSpacing(2);
            this.initImage();
            this.mVBox.getChildren().add(this.mName);
            this.mVBox.getChildren().add(this.mType);
            this.mVBox.getChildren().add(this.mTurnNumberLabel);
            this.mVBox.getChildren().add(this.mDiscImg);
            this.mVBox.getChildren().add(this.mSeparator);
            paddingLabels();
        }

        public void setLabelsStyle(String style){
            this.mName.setStyle(style);
            this.mType.setStyle(style);
            this.mTurnNumberLabel.setStyle(style);
        }

        private void paddingLabels() {
            this.mName.setPadding(new Insets(5));
            this.mType.setPadding(new Insets(5));
            this.mTurnNumberLabel.setPadding(new Insets(5));
        }

        private void initImage() {
            this.mDiscImg.setFitHeight(20);
            this.mDiscImg.setFitWidth(20);

            Rectangle clip = new Rectangle(
                    this.mDiscImg.getFitWidth(), this.mDiscImg.getFitHeight()
            );
            clip.setArcWidth(40);
            clip.setArcHeight(40);
            this.mDiscImg.setClip(clip);
        }

        private Node getRoot() {
            return this.mVBox;
        }

        private void incTurn() {
            this.mTurnNumber++;
            this.mTurnNumberLabel.setText("Turn number: " + Integer.toString(this.mTurnNumber));
        }

        private void decTurn() {
            this.mTurnNumber--;
            this.mTurnNumberLabel.setText("Turn number: " + Integer.toString(this.mTurnNumber));
        }

        private void markPlayerDetails() {
            this.mVBox.setStyle(PLAYER_MARKED_LABEL );
        }

        private void unMarkPlayerDetails() {
            this.mVBox.setStyle(REMOVE_PLAYER_MARKED_LABEL);
        }
    }
}
