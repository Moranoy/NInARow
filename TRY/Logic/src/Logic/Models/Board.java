package Logic.Models;

import Logic.Exceptions.InvalidInputException;
import Logic.Exceptions.InvalidUserInputException;

import java.util.*;
import java.util.stream.Collectors;

public class Board{
    private Cell[][] mBoard;
    private int mRows;
    private int mColumns;

    public Board(int numRows, int numCols) {
        this.Init(numRows, numCols);
    }

    public void Init(int numRows, int numCols) {
        this.mBoard = new Cell[numRows][numCols];

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                mBoard[i][j] = new Cell();
                mBoard[i][j].setRowIndex(i);
                mBoard[i][j].setColumnIndex(j);
            }
        }

        this.mRows = numRows;
        this.mColumns = numCols;
    }

    public Cell[][] getCellArray() {
        return mBoard;
    }

    public int getRows() {
        return mRows;
    }

    public int getColumns() {
        return mColumns;
    }


    public Cell UpdateBoard(int column, Player player) throws InvalidInputException {
        // Selected column is full
        if(IsColumnFull(column)) {
            throw new InvalidInputException("Cannot insert to column " + column + " because it is full!");
        }

        Cell chosenCell = null;

        // Insert to first available cell in column (max available row) if there is one.
        // Starts from highest row and makes its way down.
        for (int row = mBoard.length - 1; row >= 0; row--) {
            if (mBoard[row][column].isEmpty()) {
                mBoard[row][column].setPlayer(player); //found empty cell in column
                chosenCell = mBoard[row][column];
                break;
            }
        }

        return chosenCell;
    }

    public Collection<Integer> getAvailablePopoutColumnsForCurrentPlayer(Player mPlayer) {
        List<Integer> popoutAvailableColumnSortedList = new ArrayList<>();
        int lastRowIndex = mBoard.length - 1;

        for(int i = 0; i < this.mColumns; i++) { // Go over columns.
            if(this.mBoard[lastRowIndex][i].getPlayer() != null && this.mBoard[lastRowIndex][i].getPlayer().equals(mPlayer)) { // Check cell in column's last row.
                popoutAvailableColumnSortedList.add(i); // If cell belongs to the current player, player can popout at column - add to list.
            }
        }

        // Since we went from 0..."number of columns", the list will be sorted in an ascending order.
        return popoutAvailableColumnSortedList;
    }

    public boolean CanPlayerPerformPopout(Player player) {
        int lastRowIndex = mBoard.length - 1;

        // Check if there is a disc at the bottom of any of the columns.
        return Arrays.stream(mBoard[lastRowIndex]).anyMatch(
                    (cell) -> cell.getPlayer() != null && cell.getPlayer().equals(player)
                );
    }

    public boolean CanPlayerPerformPopoutForColumn(Player player, int column) {
        int lastRowIndex = mBoard.length - 1;
        // Check the bottom most disc of the column.
        return mBoard[lastRowIndex][column].getPlayer() != null && mBoard[lastRowIndex][column].getPlayer().equals(player);
    }

    public Collection<Cell> PopoutAndGetUpdatedCells(int column) {
        return this.removeCellFromIndexAndGetUpdatedCells(mBoard.length - 1, column); // Remove cell from the bottom most row at the selected column.
    }

    public Collection<Cell> RemoveAllPlayerDiscsFromBoardAndGetUpdatedCells(Player player) {
        Collection<Cell> updatedCellsCollections = new HashSet<>();
        Collection<Cell> updatedCellsInColumn;

        for(int i = mBoard.length - 1; i >= 0; i--) { // Go over rows.
            for(int j = mBoard[i].length - 1; j >= 0; j--) { // Go over columns.
                if(mBoard[i][j].getPlayer() != null && mBoard[i][j].getPlayer().equals(player)) { // Check if the cell was set by the player that quit.
                    updatedCellsInColumn = this.removeCellFromIndexAndGetUpdatedCells(i, j); // Remove cell from board.
                    updatedCellsCollections.addAll(updatedCellsInColumn);
                }
            }
        }

        return updatedCellsCollections;
    }

    public void Clear() {
        for(Cell[] column : mBoard){
            for (Cell cell : column){
                cell.Clear();
            }
        }
    }

    public boolean IsBoardFull() {
        boolean isBoardFull = true;

        // Check if all columns are full
        for(int i = 0; i < this.mColumns; i++) {
            if(!IsColumnFull(i)) {
                isBoardFull = false; // Found a column that is not full.
                break;
            }
        }

        return isBoardFull;
    }

    public boolean IsColumnFull(int columnIndex) {
        return !mBoard[0][columnIndex].isEmpty();
    }

    public Cell getFirstAvailableCellForColumn(int columnIndex) {
        Cell firstAvailableCell = null;

        // Start bottom to top and return the first empty cell.
        for(int i = this.mRows - 1; i >= 0 ; i--) {
            if(mBoard[i][columnIndex].isEmpty()) {
                firstAvailableCell = mBoard[i][columnIndex];
                break;
            }
        }

        return firstAvailableCell;
    }

    private Collection<Cell> removeCellFromIndexAndGetUpdatedCells(int row, int column) {
        Collection<Cell> updatedCellsList = new ArrayList<>();
        Player upperCellPlayer;

        // Go over the discs of the selected column starting from the selected row and update the player in the cell
        // until reaching null or the start of the column.
        for(int i = row; i >= 0; i--) {
            if(i == 0) { // At top most cell.
                updatedCellsList.add(mBoard[i][column]); // Add top most cell to list and break.
                mBoard[i][column].setPlayer(null); // No cell above this one, set player to null.
                break;
            }
            upperCellPlayer = mBoard[i - 1][column].getPlayer();
            mBoard[i][column].setPlayer(upperCellPlayer);
            updatedCellsList.add(mBoard[i][column]);

            if(upperCellPlayer == null) {
                break; // Reached the up most disc in board, break.
            }
        }


        return updatedCellsList;
    }
}
