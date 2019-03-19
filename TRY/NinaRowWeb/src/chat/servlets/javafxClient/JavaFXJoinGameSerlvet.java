package chat.servlets.javafxClient;

import ChatLogicEngine.users.PlayerManager;
import Logic.Models.GameDescriptionData;
import Logic.Models.Player;
import MultiGamesLogic.GamesManager;
import chat.utils.ServletUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static chat.constants.Constants.GAME_NAME_PARAM;
import static chat.constants.Constants.USERNAME;

@WebServlet(urlPatterns = {"/javafx/joingame"})
public class JavaFXJoinGameSerlvet extends HttpServlet {
    private static final int GENERAL_ERROR = 499;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // The javafx client passes the game name and username through the http params.
        String userNmae = request.getParameter(USERNAME);
        String gameName = request.getParameter(GAME_NAME_PARAM);

        if (userNmae != null) {
            this.handleJoinGame(userNmae, gameName, response);
        }
    }

    private void handleJoinGame(String userName, String gameName, HttpServletResponse response) {
        String errorMessage = null;
        PrintWriter out = null;

        if(gameName != null) {
            PlayerManager playerManager = ServletUtils.getPlayerManager(getServletContext());
            Player player = playerManager.getPlayerForName(userName);
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

            //TODO: fix error handling.
            try {
                out = response.getWriter();
                gamesManager.addUserToGame(gameName, player);
                GameDescriptionData gameDescriptionData = gamesManager.getGameDescriptionForGameName(gameName);
                Gson gson = new Gson();
                out.append(gson.toJson(gameDescriptionData));

            } catch(IOException e) {
                response.setStatus(GENERAL_ERROR);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                response.setStatus(GENERAL_ERROR);
            } finally {
                if(errorMessage != null) {
                    out.append(errorMessage);
                }
            }
        } else {
            errorMessage = "Unknown game";
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
