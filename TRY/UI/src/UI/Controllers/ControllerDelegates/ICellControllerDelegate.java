package UI.Controllers.ControllerDelegates;

@FunctionalInterface
public interface ICellControllerDelegate {
    void CellClicked(int column, int row);
}
