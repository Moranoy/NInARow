package chat.servlets.gameRoom;

import Logic.Exceptions.InvalidInputException;
import Logic.Models.PlayTurnParameters;
import Logic.Models.PlayedTurnData;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/turnhistory"})
public class TurnHistoryServlet extends HttpServlet {
    private static final int GENERAL_ERROR = 499;
    private static final String TURN_NUMBER_PARAM = "turnnumber";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gameName = ServletUtils.getGameNameFromRequest(request);
        int currentTurnNumberParam = ServletUtils.getIntParameter(request, TURN_NUMBER_PARAM);

        if(gameName != null) {
            try {
                GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
                PrintWriter out = response.getWriter();
                List<PlayedTurnData> turnHistory = gamesManager.getTurnHistoryForGame(gameName, currentTurnNumberParam);
                String currentPlayerName = gamesManager.getCurrentPlayerName(gameName);
                Gson gson = new Gson();

                String responseString = gson.toJson(new TurnHistoryResponse(turnHistory, currentPlayerName));
                out.println(responseString);
            } catch (IOException e) {
                response.setStatus(GENERAL_ERROR);
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

    private class TurnHistoryResponse {
        List<PlayedTurnData> mTurnHistoryDelta;
        String mCurrentPlayerName;

        public TurnHistoryResponse(List<PlayedTurnData> mTurnHistoryDelta, String mCurrentPlayerName) {
            this.mTurnHistoryDelta = mTurnHistoryDelta;
            this.mCurrentPlayerName = mCurrentPlayerName;
        }
    }

}
