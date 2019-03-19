package chat.servlets.gameRoom;

import Logic.Models.Cell;
import Logic.Models.Player;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/popoutcolumns"})
public class PopoutColumnsServlet extends HttpServlet {
    private static final int GENERAL_ERROR = 499;
    private static final String USERNAME_PARAM = "username";


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gameName = ServletUtils.getGameNameFromRequest(request);
        String userName = SessionUtils.getUsername(request);

        if(userName == null) {
            // No username from session. Check if there's a username from request params.
            userName = request.getParameter(USERNAME_PARAM);
        }


        if (gameName != null && userName != null) {
            try {
                Player player = ServletUtils.getPlayerManager(getServletContext()).getPlayerForName(userName);
                GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
                PrintWriter out = response.getWriter();
                Collection<Integer> availablePopoutColumns = gamesManager.getAailablePopoutColumnsForPlayer(gameName, player);
                Gson gson = new Gson();

                String responseString = gson.toJson(availablePopoutColumns);
                out.println(responseString);
            } catch (IOException e) {
                response.setStatus(GENERAL_ERROR);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }
}
