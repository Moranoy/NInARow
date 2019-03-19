package chat.servlets.javafxClient;

import ChatLogicEngine.users.PlayerManager;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static chat.constants.Constants.USERNAME;

@WebServlet(urlPatterns = {"/javafx/logout"})
public class javaFXLogoutServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // The javafx client passes the username through the http params.
        String usernameFromSession = request.getParameter(USERNAME);
        PlayerManager playerManager = ServletUtils.getPlayerManager(getServletContext());

        if (usernameFromSession != null) {
            System.out.println("Clearing session for " + usernameFromSession);
            playerManager.removePlayerWithName(usernameFromSession);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
