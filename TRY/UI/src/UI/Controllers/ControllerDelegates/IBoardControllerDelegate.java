package UI.Controllers.ControllerDelegates;

public interface IBoardControllerDelegate {
    void PopoutBtnClicked(int btnIndex);

    void ColumnClicked(int columnIndex);

    boolean isPopoutAllowed();
}
