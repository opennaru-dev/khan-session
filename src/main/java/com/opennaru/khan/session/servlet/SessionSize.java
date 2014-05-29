/*
 * Opennaru, Inc. http://www.opennaru.com/
 *
 *  Copyright (C) 2014 Opennaru, Inc. and/or its affiliates.
 *  All rights reserved by Opennaru, Inc.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.opennaru.khan.session.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * SessionSize : Example... for just testing...
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
//@WebServlet(name = "SessionSize", urlPatterns = "/SessionSize")
public class SessionSize extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    }

    public void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h2>Session Size Servlet Example</h2>");

        HttpSession session = req.getSession();
        Enumeration<String> sessionNames = session.getAttributeNames();
        String name;
        Object elt = new Object();
        int size = 0;

        while (sessionNames.hasMoreElements()) {
            name = (String) sessionNames.nextElement();
            elt = session.getAttribute(name);
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(elt);

                out.println("name=" + name);
                out.println("size=" + baos.size());
                size += baos.size();
            } catch (Exception IOE) {
                out.println("Error !=" + name + " not serializable");
            } finally {
                oos.close();
                baos.close();
            }

            out.println("class=" + elt.getClass().getCanonicalName());
            out.println("class string=" + elt.toString());
        }

        out.println("total size=" + size);

        out.close();

    }

    @Override
    public void destroy() {

    }
}
