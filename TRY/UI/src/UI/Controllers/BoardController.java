package UI.Controllers;

import Logic.Models.Cell;
import Logic.Models.Player;
import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import UI.UIMisc.ImageManager;
import UI.UIMisc.eInvalidMoveType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static UI.UIMisc.FinalSettings.POPOUT_BTN_SIZE;
import static java.lang.Thread.sleep;

public class BoardController implements ICellControllerDelegate {

    private List<CellController> mCellControllerList;
    private List<Button> mPopOutButtonList;
    private GridPane mBoardPane;
    private int mNumColumns;
    private int mNumOfDiscRows;
    private int mPopoutRowIndex;
    private IBoardControllerDelegate mDelegate;
    private boolean mIsPopoutEnabled;
    private Label mMSGToUser;

    public BoardController(int numRows, int numCols, IBoardControllerDelegate delegate) {
        this.mBoardPane = new GridPane();
        this.mBoardPane.setDisable(true);
        this.mPopOutButtonList = new ArrayList<>();
        this.mNumColumns = numCols;
        this.mNumOfDiscRows = numRows;
        this.mCellControllerList = new ArrayList<>();
        this.mDelegate = delegate;
        this.mPopoutRowIndex = this.mNumOfDiscRows;
        this.mIsPopoutEnabled = this.mDelegate.isPopoutAllowed();
        this.mMSGToUser = new Label();
        this.mMSGToUser.setVisible(false);
        this.mBoardPane.setPadding(new Insets(10));
    }

    public GridPane getBoardPane() {
        return mBoardPane;
    }

    public void InitBoard() {
        int i;

        for (i = 0; i < this.mNumColumns; i++) { //build board
            for (int j = 0; j < this.mNumOfDiscRows; j++) {
                CellController cell = new CellController(i, j, this);
                this.mCellControllerList.add(cell);
                this.mBoardPane.add(cell.getPane(), i, j);
            }
        }

        if(mIsPopoutEnabled) {
            initPopoutButtons();
        }

        this.mBoardPane.setVisible(true);
    }

    private void initPopoutButtons() {
        for (int i = 0; i < this.mNumColumns; i++) { //set popout list
            Button popoutBtn = new Button();
            Image img = new Image(getClass().getResourceAsStream("/UI/Images/popoutArrow.JPG"), POPOUT_BTN_SIZE, POPOUT_BTN_SIZE, true, true);
            popoutBtn.setGraphic(new ImageView(img));
            popoutBtn.setPrefSize(POPOUT_BTN_SIZE, POPOUT_BTN_SIZE);
            popoutBtn.setPadding(new Insets(1));
            popoutBtn.setDisable(true);
            popoutBtn.setPadding(new Insets(5));

            this.mPopOutButtonList.add(popoutBtn);
            this.mBoardPane.add(popoutBtn, i, this.mPopoutRowIndex);
            setPopoutActions(popoutBtn);
        }
    }

    public void UpdateDiscs(Collection<Cell> updatedColumnCellsCollection) {

        for (Cell updatedCellInColumn : updatedColumnCellsCollection) {
            CellController selectedCell = this.mCellControllerList
                    .stream()
                    .filter(
                            cell -> cell.getRow() == updatedCellInColumn.getRowIndex() && cell.getColumn() == updatedCellInColumn.getColumnIndex()
                    ).findFirst()
                    .get();

            if (updatedCellInColumn.isEmpty()){
                selectedCell.setImage(ImageManager.getEmptyDiscSlotImage());
            }
            else{
                selectedCell.setImage(ImageManager.getImageForPlayerID(updatedCellInColumn.getPlayer().getName()));
            }
        }

    }
    
    private void setPopoutActions(Button popoutBtn) {
        DropShadow shadow = new DropShadow();

        popoutBtn.setOnAction(
                e -> {
                    int btnColumnInd = this.getColumnIndexForPopoutBtn(popoutBtn);
                    mDelegate.PopoutBtnClicked(btnColumnInd);
                }
        );
        popoutBtn.setOnMouseEntered(
                e -> popoutBtn.setEffect(shadow)
        );
        popoutBtn.setOnMouseExited(
                e -> popoutBtn.setEffect(null)
        );
    }

    private int getColumnIndexForPopoutBtn(Button btn) {
        return mPopOutButtonList.indexOf(btn);
    }

    @Override
    public void CellClicked(int row, int column) {
        this.mDelegate.ColumnClicked(column);
    }

    public void DisplayWinningSequences(Map<Player, Collection<Cell>> playerToWinningSequenceMap) {

        playerToWinningSequenceMap.forEach(
                (player, winningCells) -> {
            if (winningCells != null) {
                setWinningStyle(winningCells);
            }
        });
    }

    private void setWinningStyle(Collection<Cell> winningCells) {

        Collection<CellController> winningCellsController = new ArrayList<>();

        for (Cell cell : winningCells){
            for (CellController cellController : this.mCellControllerList){
                if (cell.getRowIndex() == cellController.getRow() && cell.getColumnIndex() == cellController.getColumn() ){
                    winningCellsController.add(cellController);
                    break;
                }
            }
        }
        for (CellController cellController : winningCellsController){
            cellController.setWinningStyle();
        }
    }

    public void enablePopoutButtonsForColumns(Collection<Integer> columnsToEnableSortedList) {
        // Enable buttons whose indexes are in the list.
        columnsToEnableSortedList.forEach(
                index -> mPopOutButtonList.get(index).setDisable(false)
        );
    }

    public void ResetBoard() {
        this.mCellControllerList.stream().forEach(
                cellController -> {
                    cellController.setImage(ImageManager.getEmptyDiscSlotImage());
                    cellController.setDefaultStyle();
                }
        );

        this.mPopOutButtonList.stream().forEach(
                button -> button.setDisable(true)
        );
    }

    public void setTheame(String style){
        this.mBoardPane.setStyle(style);
    }

    public void handelInvalidAction(int column, eInvalidMoveType invalidMove) throws InterruptedException {
        List<CellController> invalidColumn = getCellsListInColumn(column);

        this.mMSGToUser.setText("Error: " + invalidMove.name());
        this.mMSGToUser.setVisible(true);

        for (CellController cell : invalidColumn) {
            cell.setErrorStyle();
        }
        sleep(1000);

        for (CellController cell : invalidColumn) {
            cell.setDefaultStyle();
        }

        this.mMSGToUser.setVisible(false);
    }

    private List<CellController> getCellsListInColumn(int column) {
        List<CellController> wantedCells = new ArrayList<>();

        for (CellController cell : this.mCellControllerList){
            if (cell.getColumn() == column){
                wantedCells.add(cell);
            }
        }

        return wantedCells;
    }

    public void disableAllPopoutButtons() {
        if(this.mDelegate.isPopoutAllowed()) {
            this.mPopOutButtonList.forEach(
                    btn -> btn.setDisable(true)
            );
        }
    }
}

