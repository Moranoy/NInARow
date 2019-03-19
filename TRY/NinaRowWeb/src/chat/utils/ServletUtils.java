package chat.utils;

import ChatLogicEngine.ChatManager;
import ChatLogicEngine.users.PlayerManager;
import Logic.Enums.eTurnType;
import Logic.Models.PlayTurnParameters;
import MultiGamesLogic.GamesManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static chat.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String PLAYER_MANAGER_ATTRIBUTE_NAME = "playerManager";
	private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
    private static final String GAMES_MANAGER_ATTRIBUTE_NAME = "gamesManager";

    private static final String GAME_NAME_PARAM = "gamename";

    /*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained unchronicled for performance POV
	 */
	private static final Object playerManagerLock = new Object();
	private static final Object chatManagerLock = new Object();
	private static final Object gamesManagerLock = new Object();

	public static PlayerManager getPlayerManager(ServletContext servletContext) {

		synchronized (playerManagerLock) {
			if (servletContext.getAttribute(PLAYER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(PLAYER_MANAGER_ATTRIBUTE_NAME, new PlayerManager());
			}
		}
		return (PlayerManager) servletContext.getAttribute(PLAYER_MANAGER_ATTRIBUTE_NAME);
	}

	public static ChatManager getChatManager(ServletContext servletContext) {
		synchronized (chatManagerLock) {
			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
			}
		}
		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}

	public static GamesManager getGamesManager(ServletContext servletContext) {
        synchronized (gamesManagerLock) {
            if (servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(GAMES_MANAGER_ATTRIBUTE_NAME, new GamesManager());
            }
        }

        return (GamesManager) servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME);
    }

    public static String getGameNameFromRequest(HttpServletRequest request) {
	    //TODO: fix this function to return null if game name not found or game name is empty.
        String gameName = request.getParameter(GAME_NAME_PARAM);
        return gameName.replace("+", " ");
    }

	public static boolean getBoolParamFromRadioButtonInput(HttpServletRequest request, String paramKey) {
		//TODO: fix function to handle not having a parameter with the name "paramkey", or having a bad value in it.
	    String value = request.getParameter(paramKey);
        boolean boolValue = false;

		if(value != null && value.equals("on")) {
            boolValue = true;
        }

		return boolValue;
	}

	public static PlayTurnParameters getPlayTurnParamsFromRequest(HttpServletRequest request) {
	    final String TURN_TYPE_PARAM = "turnType";
	    final String SELECTED_COLUMN_PARAM = "column";
	    final String PLAYER_NAME_PARAM = "playerName";

        PlayTurnParameters params = null;


        String turnTypeString = request.getParameter(TURN_TYPE_PARAM);

        if(turnTypeString != null && !turnTypeString.isEmpty()) {
            // Get the rest of the parameters
            eTurnType turnType = eTurnType.valueOf(turnTypeString);
            int selectedColumn = getIntParameter(request, SELECTED_COLUMN_PARAM);
            String playerName = request.getParameter(PLAYER_NAME_PARAM);

            params = new PlayTurnParameters(selectedColumn, turnType);
            params.setmPlayerName(playerName);
        }

		return params;
	}
}
