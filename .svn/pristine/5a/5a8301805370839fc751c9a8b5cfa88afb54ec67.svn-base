package com.recalot.views.tracking;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Matthaeus.schmedding
 */
public class Servlet extends HttpServlet {
    private ControllerHandler handler;
    private Pattern pattern;

    public Servlet(ControllerHandler handler) {
        this.handler = handler;
        this.pattern = Pattern.compile("\"/(Users|users)/(\\w*)/(Items|items)/(\\w*)\"");
    }

    /**
     * TODO: deactivate it later. Just for debugging
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println("Blub");
        out.println("Path:" + req.getServletPath());
        out.println("Path info:" + req.getPathInfo());

        try
        {

            Matcher matcher = pattern.matcher(req.getPathInfo());
            out.println("Group count:" + matcher.groupCount());
            for (int i = 0; i < matcher.groupCount(); i++) {
                out.println("Group " + i + ":" + matcher.group(i));
            }

        }   catch (Exception e){
            out.println("error");
        }



        //check if request is valid
        if (true) {
            // handler.process(req, res);
        } else {
            //
            // render error message
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //check if request is valid
        if (true) {
            //  handler.process(req, res);
        } else {
            // //render error message
        }
    }
}