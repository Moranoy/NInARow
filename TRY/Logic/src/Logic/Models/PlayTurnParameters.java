package Logic.Models;

import Logic.Enums.eTurnType;

public class PlayTurnParameters {

    private int mSelectedColumn;
    private eTurnType mTurnType;

    public String getmPlayerName() {
        return mPlayerName;
    }

    public void setmPlayerName(String mPlayerName) {
        this.mPlayerName = mPlayerName;
    }

    private String mPlayerName;

    public PlayTurnParameters(eTurnType turnType) {
        this.mTurnType = turnType;
    }

    public PlayTurnParameters(int selectedColumn, eTurnType turnType) {
        this.mSelectedColumn = selectedColumn;
        this.mTurnType = turnType;
    }

    public int getmSelectedColumn() {
        return mSelectedColumn;
    }

    public eTurnType getmTurnType() {
        return this.mTurnType;
    }
}
