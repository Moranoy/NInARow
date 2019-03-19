package NinaRowHTTPClient.Login;

import NinaRowHTTPClient.GeneralCommunication.AddressBuilder;
import NinaRowHTTPClient.GeneralCommunication.CommunicationHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// This class is in charge of the login logic. It does not use a designated communications class and tasks because it
// Only has 1 task to fulfill.
public class LoginClientLogic {
    public static final String USERNAME_PARAMETER = "username";
    public static final String USER_TYPE_PARAM = "usertype";
    public static final String LOGIN_URL = "javafx/login";

    private ILoginClientLogicDelegate mDelegate;
    private Gson mGson = new GsonBuilder().create();
    private CommunicationHandler mCommunicationHandler;
    private String mPlayerName;


    public LoginClientLogic(ILoginClientLogicDelegate mDelegate) {
        this.mDelegate = mDelegate;
        this.mCommunicationHandler = new CommunicationHandler();
    }

    public void performLogin(String name, boolean isComputer) {
        this.mPlayerName = name;
        this.mCommunicationHandler.setPath(LOGIN_URL);
        this.mCommunicationHandler.addParameter(USERNAME_PARAMETER, name);

        String userTypeStringValue = isComputer ? "on" : "off"; // Use on/off to copy radio button's values.
        this.mCommunicationHandler.addParameter(USER_TYPE_PARAM, userTypeStringValue);
        this.mCommunicationHandler.doGet(this::handleResponseSuccess, this::handleResponseFailure);
    }

    private void handleResponseSuccess(String responseString) {
        System.out.println(responseString);

        String loggedInPlayerName = this.mGson.fromJson(responseString, String.class);
        this.mDelegate.onLoginSuccess(loggedInPlayerName);
    }

    private void handleResponseFailure(String errorMessage, Integer errorCode) {
        this.mDelegate.onLoginError(errorMessage);
    }
}
