package UI.Controllers;

import Logic.Models.Player;
import NinaRowHTTPClient.Login.ILoginClientLogicDelegate;
import NinaRowHTTPClient.Login.LoginClientLogic;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import javafx.scene.input.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.function.Consumer;

public class LoginController implements ILoginClientLogicDelegate {
    private Consumer<String> mOnFinishedLogin;
    private LoginClientLogic mLoginClientLogic;

    @FXML
    private TextField muiNameTextField;

    @FXML
    private Button muiLoginButton;

    @FXML
    private Label muiErrorLabel;

    @FXML
    private RadioButton mPlayerTypeRadioButton;

    @FXML
    private void initialize() {
        this.mLoginClientLogic = new LoginClientLogic(this);
        this.muiLoginButton.setOnMouseClicked(this::onLoginClick);
        this.muiErrorLabel.setText(""); // Set error message to be invisible at the start.
    }

    public void setmOnFinishedLogin(Consumer<String> mOnFinishedLogin) {
        this.mOnFinishedLogin = mOnFinishedLogin;
    }

    private void onLoginClick(MouseEvent e) {
        String name = this.muiNameTextField.getText();
        this.mLoginClientLogic.performLogin(name, this.mPlayerTypeRadioButton.isSelected());
    }

    public void onLoginError(String errorMessage) {
        Platform.runLater(
                () -> this.muiErrorLabel.setText(errorMessage)
        );
    }

    public void onLoginSuccess(String playerName) {
        Platform.runLater(
                () -> this.mOnFinishedLogin.accept(playerName)
        );
    }
}
