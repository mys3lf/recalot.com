package com.example.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Matthaeus.schmedding
 */
public class Servlet extends HttpServlet {
    private Controller controller;

    public Servlet(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println("Blub");
        out.println("Path:" + req.getServletPath());
        out.println("Path info:" + req.getPathInfo());

        out.println("Result");
        if (req.getPathInfo() == null  || req.getPathInfo().equals("/")) {
            out.println(controller.get(null));
        } else {
            String[] split = req.getPathInfo().split("/");
            if (split.length > 0) {
                out.println(controller.get(split[1]));
            }
        }
    }
}