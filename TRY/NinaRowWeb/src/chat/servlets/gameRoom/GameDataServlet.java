package chat.servlets.gameRoom;

import Logic.Models.GameDescriptionData;
import MultiGamesLogic.GamesManager;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/gamedata"})

public class GameDataServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get player name from session
        String username = SessionUtils.getUsername(request);

        if(username != null) {
            // Get game name from games manager
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            String gameName = gamesManager.getGameNameForPlayerName(username);

            if(gameName != null) {
                response.setContentType("application/json");
                try (PrintWriter out = response.getWriter()) {
                    Gson gson = new Gson();
                    GameDescriptionData gameDescriptionData = gamesManager.getGameDescriptionForGameName(gameName);
                    PlayerNameAndGameData playerNameAndGameData = new PlayerNameAndGameData(username, gameDescriptionData);
                    String json = gson.toJson(playerNameAndGameData);
                    out.println(json);
                    out.flush();
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private static class PlayerNameAndGameData {
        private String mLoggedInPlayerName;
        private GameDescriptionData mGameDescriptionData;

        public PlayerNameAndGameData(String mLoggedInPlayerName, GameDescriptionData mGameDescriptionData) {
            this.mLoggedInPlayerName = mLoggedInPlayerName;
            this.mGameDescriptionData = mGameDescriptionData;
        }

        public String getmLoggedInPlayerName() {
            return mLoggedInPlayerName;
        }

        public void setmLoggedInPlayerName(String mLoggedInPlayerName) {
            this.mLoggedInPlayerName = mLoggedInPlayerName;
        }

        public GameDescriptionData getmGameDescriptionData() {
            return mGameDescriptionData;
        }

        public void setmGameDescriptionData(GameDescriptionData mGameDescriptionData) {
            this.mGameDescriptionData = mGameDescriptionData;
        }
    }
}
