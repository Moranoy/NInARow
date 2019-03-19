package chat.servlets.gameRoom;

import Logic.Models.Cell;
import Logic.Models.PlayedTurnData;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/winningsequence"})
public class WinningSequenceServlet extends HttpServlet {
    private static final int GENERAL_ERROR = 499;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gameName = ServletUtils.getGameNameFromRequest(request);

        if(gameName != null) {
            try {
                GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
                PrintWriter out = response.getWriter();
                Map<Player, Collection<Cell>> playerToWinningSequenceMap = gamesManager.getPlayerToWinningSequenceMap(gameName);
                List<PlayerAndWinningSequence> playerAndWinningSequenceCollection = getPlayerAndWinningSequenceCollectionFromMap(playerToWinningSequenceMap);
                Gson gson = new Gson();

                String responseString = gson.toJson(playerAndWinningSequenceCollection);
                out.println(responseString);
            } catch (IOException e) {
                response.setStatus(GENERAL_ERROR);
            }
        }
    }

    private List<PlayerAndWinningSequence> getPlayerAndWinningSequenceCollectionFromMap(Map<Player, Collection<Cell>> playerToWinningSequenceMap ) {
        List<PlayerAndWinningSequence> playerAndWinningSequenceList = new ArrayList<>();

        for(Map.Entry<Player, Collection<Cell>> playerAndWinningSequenceEntry: playerToWinningSequenceMap.entrySet()) {
            playerAndWinningSequenceList.add(new PlayerAndWinningSequence(playerAndWinningSequenceEntry.getKey(), playerAndWinningSequenceEntry.getValue()));
        }

        return playerAndWinningSequenceList;
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

    // This class contains data regarding a winning player and his winning sequences.
    // This class is a workaround to not being able to parse a json map in the client side.
    private class PlayerAndWinningSequence {
        private Player mPlayer;
        private Collection<Cell> mWinningSequence;

        public PlayerAndWinningSequence(Player mPlayer, Collection<Cell> mWinningSequence) {
            this.mPlayer = mPlayer;
            this.mWinningSequence = mWinningSequence;
        }
    }
}
