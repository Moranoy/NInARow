package chat.servlets.lobby;

import Logic.Exceptions.InvalidFileInputException;
import MultiGamesLogic.GamesManager;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Collection;
import java.util.Scanner;


@WebServlet(urlPatterns = {"/upload"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadGameFileServlet extends HttpServlet {

    private static final int GENERAL_ERROR = 499;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = SessionUtils.getUsername(request);

        if(userName != null) {
            response.setContentType("text/html");

            Collection<Part> parts = request.getParts();

            StringBuilder fileContent = new StringBuilder();

            for (Part part : parts) {
                //to write the content of the file to a string
                fileContent.append(readFromInputStream(part.getInputStream()));
            }

            InputStream fileContentStream = new ByteArrayInputStream(fileContent.toString().getBytes("UTF-8"));

            this.initNewGameFromFileContent(fileContentStream, userName, response);
        } else {
            response.setStatus(GENERAL_ERROR);
            PrintWriter out = response.getWriter();
            out.append("Unidentified user. Please log in.");
        }
    }

    private void initNewGameFromFileContent(InputStream fileContentStream, String userName, HttpServletResponse response) {
        PrintWriter out = null;
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

        try {
            out = response.getWriter();
            gamesManager.addGame(fileContentStream, userName);
        } catch(Exception e) {
            if(out != null) {
                out.append(e.getMessage());
            }

            response.setStatus(GENERAL_ERROR);
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
