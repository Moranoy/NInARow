package Logic.Models;

import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class PlayedTurnData implements Serializable {
    private Collection<Cell> mUpdatedCellsCollection = new ArrayList<>();
    private Player mPlayer;
    private eGameState mGameStateAfterTurn;
    private eTurnType mTurnType;

    public eTurnType getTurnType() {
        return mTurnType;
    }

    public void setTurnType(eTurnType turnType) {
        this.mTurnType = turnType;
    }

    public Collection<Cell> getUpdatedCellsCollection() {
        return mUpdatedCellsCollection;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public eGameState getGameState() {
        return mGameStateAfterTurn;
    }

    public void setGameState(eGameState gameState) {
        this.mGameStateAfterTurn = gameState;
    }

    public void setUpdatedCellsCollection(Collection<Cell> updatedCell) {
        // Shallow copy the cells so that if they change in game it won't affect this cell collection.
        updatedCell.forEach(
                cell -> this.mUpdatedCellsCollection.add(cell.getShallowCopy())
        );
    }

    public void setPlayerTurn(Player player) {
        this.mPlayer = player;
    }

    public Player getPlayerTurn(){ return this.mPlayer;}

    @Override
    public String toString() {
        return "PlayedTurnData{" +
                "mUpdatedCellsCollection=" + mUpdatedCellsCollection +
                ", mPlayer=" + mPlayer +
                ", mGameStateAfterTurn=" + mGameStateAfterTurn +
                ", mTurnType=" + mTurnType +
                '}';
    }
}
