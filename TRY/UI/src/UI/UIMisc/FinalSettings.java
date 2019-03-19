package UI.UIMisc;

import java.io.Serializable;

public class FinalSettings implements Serializable {

    public static final int CELL_SIZE = 45;
    public static final int POPOUT_BTN_SIZE = CELL_SIZE - 5;
    public static final int EXIT_BTN_SIZE = 45;

    //App window size
    public static final int APP_WINDOW_WIDTH = 670;
    public static final int APP_WINDOW_HEIGHT = 575;

    // Button text
    public static final String START_BTN_TEXT = "Start Game";
    public static final String RESTART_BTN_TEXT = "Restart Game";
    public static final String REPLAY_START_BTN_TEXT = "Start Replay";
    public static final String REPLAY_STOP_BTN_TEXT = "Stop Replay";

    //css
    public static final String BACKGROUND_SETTINGS = " -fx-background-repeat: stretch; \n" +
        "    -fx-background-size: cover;\n" +
        "    -fx-background-position: center;";
    public static final String AVIAD_THEME_IMAGE_BACKGROUND = " -fx-background-image: url(\"UI/Images/AviadTheameBackground.jpg\");";
    public static final String Guy_THEME_IMAGE_BACKGROUND = " -fx-background-image: url(\"UI/Images/GuyTheameBackground.png\");";
    public static final String DEFAULT_THEME_IMAGE_BACKGROUND = " -fx-background-image: url(\"UI/Images/DefaultTheameBackground.jpg\");";
    public static final double LOW_OPACITY = 0.5;
    public static final double HIGH_OPACITY = 0.8;
    public static final String CELL_BORDER_ERROR = " -fx-background-color: red";
    public static final String CELL_BORDER_WINNING = " -fx-background-color: red";
    public static final String CELL_BORDER_DEFAULT = " -fx-background-color: none";
    public static final String BUTTON_GRADIENT_AVIAD = " -fx-background-color: linear-gradient(#008307, #9ce7a0);";
    public static final String BUTTON_GRADIENT_Guy = " -fx-background-color: linear-gradient(#2A5058, #61a2b1);";
    public static final String BUTTON_GRADIENT_DEFAULT = " -fx-background-color: linear-gradient(white, grey);";
    public static final String LABEL_STYLE_AVIAD = " -fx-font-size: 14px;";
    public static final String LABEL_STYLE_Guy = " -fx-font-size: 12px;";
    public static final String LABEL_STYLE_DEFAULT = " -fx-font-size: 13px;";
    public static final String VBOX_STYLE_AVIAD = " -fx-background-color: #44b65f;";
    public static final String VBOX_STYLE_Guy = " -fx-background-color: #568AB6;";
    public static final String VBOX_STYLE_DEFAULT = " -fx-background-color: white;";
    public static final String PLAYERS_PANE_STYLE_AVIAD = " -fx-background-color: #44b65f;";
    public static final String PLAYERS_PANE_STYLE_Guy = " -fx-background-color: #568AB6;";
    public static final String PLAYERS_PANE_STYLE_DEFAULT = " -fx-background-color: white;";
    public static final String FONT_AVIAD = " -fx-font-family: \"Comic Sans MS\";"  + " -fx-font-style: normal";
    public static final String FONT_Guy = " -fx-font-family: \"Arial\";" + " -fx-font-style: italic";
    public static final String FONT_DEFAULT = " -fx-font-family: \"Tahoma\";" + " -fx-font-style: normal";
    public static final String PLAYER_MARKED_LABEL = " -fx-border-color: red";
    public static final String REMOVE_PLAYER_MARKED_LABEL = " -fx-border-color: none";

}
