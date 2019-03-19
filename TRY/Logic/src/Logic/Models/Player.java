package Logic.Models;

import Logic.Enums.ePlayerType;

import java.io.Serializable;

public class Player implements Serializable {

    private int mTurnCounter;
    private String mName;
    private ePlayerType mType;

    public void init(String name, ePlayerType type) {
        setTurnCounter(0);
        setName(name);
        setType(type);
    }

    @Override
    public boolean equals(Object otherPlayer) {
        // Check if other player is not null and IDs match.
        return otherPlayer != null && mName.contentEquals(((Player)otherPlayer).mName);
    }

    public void FinishedTurn() {
        mTurnCounter++;
    }

    public String getName() {
        return mName;
    }

    public ePlayerType getType() {
        return mType;
    }

    public int getTurnCounter(){return mTurnCounter;}

    public void setTurnCounter(int turnCounter) {
        this.mTurnCounter = turnCounter;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setType(ePlayerType mType) {
        this.mType = mType;
    }

    @Override
    public String toString() {
        return "Player{" +
                "mTurnCounter=" + mTurnCounter +
                ", mName='" + mName + '\'' +
                ", mType=" + mType +
                '}';
    }
}
