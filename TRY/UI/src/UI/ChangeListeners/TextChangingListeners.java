package UI.ChangeListeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.Consumer;

public class TextChangingListeners implements ChangeListener<Boolean> {

    private String mStrWhenTrue;
    private String mStrWhenFalse;
    private Consumer<String> mOnValueChange;

    public TextChangingListeners(String strWhenTrue, String strWhenFalse, Consumer<String> onValueChange) {
        this.mStrWhenTrue = strWhenTrue;
        this.mStrWhenFalse = strWhenFalse;
        this.mOnValueChange = onValueChange;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        String selectedString = newValue ? this.mStrWhenTrue : this.mStrWhenFalse;

        this.mOnValueChange.accept(selectedString);
    }
}
