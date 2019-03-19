package NinaRowHTTPClient.Game.ResponseModels;

import Logic.Models.Cell;
import Logic.Models.Player;

import java.util.Collection;

// This class contains data regarding a winning player and his winning sequences.
// This class is a workaround to not being able to parse a json map in the client side.
public class PlayerAndWinningSequence {

    private Player mPlayer;
    private Collection<Cell> mWinningSequence;

    public PlayerAndWinningSequence() {
    }

    public Player getmPlayer() {
        return mPlayer;
    }

    public Collection<Cell> getmWinningSequence() {
        return mWinningSequence;
    }

    @Override
    public String toString() {
        return "PlayerAndWinningSequence{" +
                "mPlayer=" + mPlayer +
                ", mWinningSequence=" + mWinningSequence +
                '}';
    }
}

