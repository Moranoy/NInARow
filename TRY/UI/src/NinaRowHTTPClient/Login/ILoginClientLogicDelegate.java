package NinaRowHTTPClient.Login;

import Logic.Models.Player;

public interface ILoginClientLogicDelegate {
    void onLoginSuccess(String player);

    void onLoginError(String errorMessage);
}
