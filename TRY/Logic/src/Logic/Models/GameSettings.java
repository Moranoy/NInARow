package Logic.Models;

import Logic.Enums.eVariant;

import java.io.Serializable;

// This class will contain all of the game related details that were read from the XML file.

public class GameSettings implements Serializable {

    public static final int MAX_BOARD_ROWS = 50;
    public static final int MIN_BOARD_ROWS = 5;
    public static final int MAX_BOARD_COLS = 60;
    public static final int MIN_BOARD_COLS = 6;
    public static final int MIN_TARGET = 2;
    public static final int MIN_NUM_OF_PLAYERS = 2;
    public static final int MAX_NUM_OF_PLAYERS = 6;

    private int mGameNumberOfPlayers;
    private int mTarget;
    private int mRows;
    private int mColumns;
    private eVariant mVariant;
    private String mUploaderName;
    private String mGameName;

    public int getGameNumberOfPlayers() {
        return mGameNumberOfPlayers;
    }

    public void setGameNumberOfPlayers(int gameNumberOfPlayers) {
        this.mGameNumberOfPlayers = gameNumberOfPlayers;
    }

    public int getTarget() {
        return mTarget;
    }

    public int getRows() {
        return mRows;
    }

    public int getColumns() {
        return mColumns;
    }

    public eVariant getVariant() {
        return mVariant;
    }

    public void setTarget(int target) {
        this.mTarget = target;
    }

    public void setRows(int rows) {
        this.mRows = rows;
    }

    public void setColumns(int columns) {
        this.mColumns = columns;
    }

    public void setVariant(eVariant variant) {
        this.mVariant = variant;
    }

    public String getUploaderName() {
        return this.mUploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.mUploaderName = uploaderName;
    }

    public String getmGameName() {
        return mGameName;
    }

    public void setmGameName(String mGameName) {
        this.mGameName = mGameName;
    }
}
