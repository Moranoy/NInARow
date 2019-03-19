package chat.servlets.lobby;

import ChatLogicEngine.users.PlayerManager;
import Logic.Models.Player;
import MultiGamesLogic.GamesManager;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/joingame"})
public class JoinGameServlet extends HttpServlet {

    private static final String GAME_ROOM_URL = "/pages/game_room/game_room.html";

    private static final int GENERAL_ERROR = 499;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        System.out.println("Entered joingame servlet.");

        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession != null) {
            String gameName = request.getParameter(Constants.GAME_NAME_PARAM);
            this.handleJoinGame(usernameFromSession, gameName, response, request.getContextPath());
        } else {
            // Unidentified user. Send back to login screen.
            response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }

    private void handleJoinGame(String userName, String gameName, HttpServletResponse response, String contextPath) {
        String errorMessage = null;
        PrintWriter out = null;

        if(gameName != null) {
            gameName = gameName.replace("+", " ");
            PlayerManager playerManager = ServletUtils.getPlayerManager(getServletContext());
            Player player = playerManager.getPlayerForName(userName);
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            //TODO: fix error handling.
            try {
                out = response.getWriter();
                gamesManager.addUserToGame(gameName, player);
                response.addHeader("gamename", gameName);
                response.sendRedirect(contextPath + GAME_ROOM_URL);
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
