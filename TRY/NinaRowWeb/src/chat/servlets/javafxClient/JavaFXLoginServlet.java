package chat.servlets.javafxClient;

import ChatLogicEngine.users.PlayerManager;
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
import java.io.Writer;

import static chat.constants.Constants.USERNAME;

@WebServlet(urlPatterns = {"/javafx/login"})
public class JavaFXLoginServlet  extends HttpServlet {
    private static final int NO_USER_NAME_ERROR = 497;
    private static final int USER_NAME_ALREADY_EXISTS = 498;

    private final String USER_TYPE_PARAM = "usertype";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("Entered login servlet.");

        String usernameFromSession = SessionUtils.getUsername(request);
        PlayerManager playerManager = ServletUtils.getPlayerManager(getServletContext());

        PrintWriter out = response.getWriter();

        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null) {
                response.setStatus(NO_USER_NAME_ERROR);
                out.println("No username.");
                System.out.println("Username is null.");
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    if (playerManager.isUserExists(usernameFromParameter)) {
                        response.setStatus(USER_NAME_ALREADY_EXISTS);
                        out.println("Username already exists.");
                        System.out.println("Username already exists" + usernameFromParameter);
                    } else {
                        boolean isComputer = ServletUtils.getBoolParamFromRadioButtonInput(request, USER_TYPE_PARAM);
                        playerManager.addPlayer(usernameFromParameter, isComputer);

                        // start session with user.
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        out.println(usernameFromParameter);
                    }
                }
            }
        } else {
            //user is already logged in
            //response.sendRedirect(LOBBY_URL);
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
