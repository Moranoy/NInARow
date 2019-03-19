package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

import java.io.Serializable;


public class Cell implements Serializable {

    private Player mPlayer;
    private int mColumnIndex;
    private int mRowIndex;

    public boolean isEmpty() {
        return mPlayer == null;
    }

    public void Clear(){
        mPlayer = null;
    }

    public int getColumnIndex() {
        return mColumnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.mColumnIndex = columnIndex;
    }

    public int getRowIndex() {
        return mRowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.mRowIndex = rowIndex;
    }

    public void setPlayer(Player player) {
        this.mPlayer = player;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Cell getShallowCopy() {
        Cell copyCell = new Cell();

        copyCell.setRowIndex(this.mRowIndex);
        copyCell.setColumnIndex(this.mColumnIndex);
        copyCell.setPlayer(this.mPlayer);

        return copyCell;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "mPlayer=" + mPlayer +
                ", mColumnIndex=" + mColumnIndex +
                ", mRowIndex=" + mRowIndex +
                '}';
    }
}
