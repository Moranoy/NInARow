package UI.Controllers;

import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static UI.UIMisc.FinalSettings.*;

public class CellController {

    private StackPane mPane;
    private ImageView mIVSign;
    private Image mEmptyCellImg;
    private ICellControllerDelegate mDelegate;
    private int mColumn;
    private int mRow;
    private Border mDefaultBorder;
    private Border mMarkedBorder;

    public CellController(int column, int row, ICellControllerDelegate delegate){
        this.mColumn = column;
        this.mRow = row;
        this.mDelegate = delegate;
        this.mEmptyCellImg = new Image("/UI/Images/EmptyCell.JPG");
        this.init();
    }

    private void init() {
        this.mIVSign = new ImageView(this.mEmptyCellImg);
        this.mDefaultBorder = new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        this.mMarkedBorder = new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

        this.mIVSign.setFitHeight(CELL_SIZE);
        this.mIVSign.setFitWidth(CELL_SIZE);

        Rectangle clip = new Rectangle(
                this.mIVSign.getFitWidth(), this.mIVSign.getFitHeight()
        );
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        this.mIVSign.setClip(clip);

        setOnAction();

        this.mPane = new StackPane();
        this.mPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.mPane.getChildren().add(this.mIVSign);
        this.mPane.setPrefSize(CELL_SIZE, CELL_SIZE);
    }

    private void setOnAction() {
        this.mIVSign.setOnMouseClicked(
                e -> this.mDelegate.CellClicked(this.mRow, this.mColumn)
        );

        this.mIVSign.setOnMouseEntered(
                e -> this.mPane.setBorder(this.mMarkedBorder)
        );

        this.mIVSign.setOnMouseExited(
                e -> this.mPane.setBorder(this.mDefaultBorder)
        );
    }

    public void setImage(Image image) {
        this.mIVSign.setImage(image);
    }

    public Pane getPane() {
        return this.mPane;
    }

    public int getColumn() {
        return this.mColumn;
    }

    public int getRow() {
        return this.mRow;
    }

    public void setDefaultStyle() {
        this.mPane.setStyle(CELL_BORDER_DEFAULT);
        this.mPane.setBorder(this.mDefaultBorder);
    }

    public void setErrorStyle() {
        this.mPane.setStyle(CELL_BORDER_ERROR);
        this.mPane.setBorder(this.mMarkedBorder);
    }

    public void setWinningStyle() {
        this.mPane.setStyle(CELL_BORDER_WINNING);
        this.mPane.setBorder(this.mMarkedBorder);

    }
}
