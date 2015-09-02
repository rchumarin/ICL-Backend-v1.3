package ru.icl.test.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.icl.test.db.Database;

//@WebServlet(name = "WriteMessage", urlPatterns = {"/WriteMessage"})
public class WriteMessage_DRAFT extends HttpServlet {

    public static final String SESSION_MAP = "sessionMap"; 
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
        
        //вводится коллекция для хранения сообщений сторонних клиентов
        /*
        Map<String, List> sessionMap = (Map<String,List>)request.getServletContext().getAttribute(SESSION_MAP);        
        if (sessionMap==null) {
            sessionMap = new HashMap<String, List>();
        }
        */
        
        /* Структура нашего JSON объекта:
        { id_key : {msg_key : [message], 
                       name_Key: name} 
        }
        где: 
            id_key - значение сессии клиента           
            name - имя клиента
            message - сообщения клиента    
        
        */
        JSONObject jsonOb = (JSONObject) request.getServletContext().getAttribute(SESSION_MAP);
        if (jsonOb==null) {
            jsonOb = new JSONObject();
        }        
        response.setContentType("text/html; charset=UTF-8");        
        PrintWriter out = response.getWriter();      
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("<title>Онлайн ЧАТ</title>");
        out.println("<link href=\"css/style_main.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");        
        out.println("<body>");        
        out.println("<div class=\"container\">");
        out.println("<div class=\"logo\">");
        out.println("<a href=\"main.jsp\"><img src=\"images/chat_.png\" width=\"86\" height=\"87\" alt=\"\" name=\"logo\" /></a>");
        out.println("</div>");
        out.println("<div class=\"descr\">");
        out.println("<h3>Онлайн ЧАТ</h3>");
        out.println("</div>");
        out.println("<div class=\"welcome\">");
        
        try {
            // считывание параметров
            //String name = request.getParameter("name");
            String msg = request.getParameter("msg");            

            // определение или создание сессии
            HttpSession session = request.getSession(true);
            String id = session.getId();
            //String name = (String) session.getAttribute("name");                        
            String name = (String) request.getServletContext().getAttribute("name");                                                
            out.println("<h5>Добро пожаловать, " + name + "!</h5>");             
            out.println("<h5 class=\"sessia\">Ваш id: "  + id + "</h5>");
            out.println("<h6>");           
            out.println("<a href=\"index.jsp\">Выход</a>");
            out.println("</h6>");
            out.println("</div>");            
            out.println("<div class=\"search_form\">");
            out.println("<form name=\"search_form\" method=\"GET\" action=\"");
            out.println(request.getContextPath());
            //out.println("/WriteMessage\">"); 
            out.println("/simple\">");            
            out.println("<input type=\"text\" name=\"msg\" size=\"135\"/>");
            out.println("<input class=\"search_button\" type=\"submit\" value=\"Отправить\"/>                                   \n");
            out.println("</form>");
            out.println("</div>");            
            out.println("<div class=\"big_column\">");
            out.println("<div>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>N</th>");
            out.println("<th>id</th>");
            out.println("<th>Messages</th>");
            out.println("</tr>");
            
            //вводится коллекция для сообщений текущего клиента
            //ArrayList<String> listMsg;
            JSONArray jsonArr;            

            // для новой сессии создаем новый список
            if (session.isNew()) {
                //listMsg = new ArrayList<>();
                jsonArr = new JSONArray();
            } else { // иначе получаем список из атрибутов сессии
                //listMsg = (ArrayList<String>) session.getAttribute("message");
                jsonArr = (JSONArray)session.getAttribute("jmessage");
                
            }

            // добавление нового сообщения в список и атрибут сессии             
            //listMsg.add(name + ": " + msg);            
            //session.setAttribute("message", listMsg);
            jsonArr.add(msg);            
            session.setAttribute("jmessage", jsonArr);            
            
            // вывод сообщений текущего клиента
            int count=1;                        
            /*
            for (String str1 : listMsg) {                                                         
                out.println("<tr>");
                out.println("<td>" + (count++) + "</td>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + str1 + "</td>");
                out.println("</tr>");
            }
            
            for (Object str1 : jsonArr) {                                                         
                out.println("<tr>");
                out.println("<td>" + (N++) + "</td>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + str1.toString() + "</td>");
                out.println("</tr>");
            }
            */
            
            //вывод сообщений других клиентов
            //sessionMap.put(id, listMsg);
            JSONObject jsonName = new JSONObject();
            jsonName.put("name", name);
            jsonName.put("msg", jsonArr);
            jsonOb.put(id, jsonName);                        

            //RequestDispatcher dispatcher = request.getRequestDispatcher("pages/main.jsp");
            //dispatcher.forward(request, response);
                    
            //проверка
            //System.out.println("1) " + jsonOb);
            
            //getServletContext().setAttribute(SESSION_MAP, sessionMap);
            getServletContext().setAttribute(SESSION_MAP, jsonOb);                        
                                       
            /*
            for (Map.Entry<String, List> entry : sessionMap.entrySet()) {
                String sessionId = entry.getKey();
                List listMessages = entry.getValue();
                
                //сообщения текущего пользователя пропускаются 
                if (sessionId.equals(id)) continue;

                for (Object str2 : listMessages) {
                    out.println("<tr>");                 
                    out.println("<td>" + (N++) + "</td>");
                    out.println("<td style=\"color:red\">" + sessionId + "</td>");
                       
                    out.println("<td>" + str2 + "</td>");
                    out.println("</tr>");
                }
            }
            */            
            //jsonOb.keySet() возвращает набор, состоящий из id
            Iterator interatorKey = jsonOb.keySet().iterator();
            //проверка
            //System.out.println("2) " + jsonOb.keySet());
            
            //jsonOb.keySet() возвращает коллекцию, состоящую из msg и name
            Iterator<JSONObject> iteratorValue = jsonOb.values().iterator();
            
            //проверка
            //System.out.println("3) " + jsonOb.values());

            //Осуществляем перебор 
            while (iteratorValue.hasNext() || interatorKey.hasNext()) {
                String jsonIdValue = (String) interatorKey.next();                                                
                
                //проверка
                //System.out.println("4) " + jsonIdValue); 
                                
                JSONObject jsonMsgAndName = iteratorValue.next();                 
                
                //проверка
                //System.out.println("5) " + jsonMsgAndName);                
                
                JSONArray jsonMsgArrayValue = (JSONArray) jsonMsgAndName.get("msg");                 
                
                //проверка
                //System.out.println("6) " + jsonMsgArrayValue);                
                
                for(Object jsonMsgValue : jsonMsgArrayValue) {                                        
                    out.println("<tr>");                      
                    out.println("<td>" + (count++) + "</td>");
                    out.println("<td>" + jsonIdValue + "</td>");                 
                    out.println("<td>" + jsonMsgValue + "</td>");                                        
                    //проверка
                    //System.out.println("7) " + jsonMsgValue);                                        
                    out.println("</tr>");
                }            
            }                     
            out.println("</table>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");        
            out.println("<div class=\"footer\">© 2015 ICL. Test project</div>");
            
            /*            
            //запись в БД Postgres
            Connection conn = null;
            PreparedStatement pstmt1 = null;
            PreparedStatement pstmt2 = null;
            String sql1 = "insert into users (clientid, fname) values (?, ?)";
            String sql2 = "insert into messages (clientid, message) values (?, ?)";

            try {
                conn = Database.getConnection();                       
                if (session.isNew()) { 
                    pstmt1 = conn.prepareStatement(sql1);
                    pstmt1.setString(1, id);
                    pstmt1.setString(2, name);
                    pstmt1.executeUpdate();
                }

                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, id);
                pstmt2.setString(2, msg);
                pstmt2.executeUpdate();                                            

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {                
                try {                    
                    if (pstmt1!=null) pstmt1.close();
                    if (pstmt2!=null) pstmt2.close();
                    if (conn!=null)conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }               
        */    
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);           
        } finally {
            out.println("</body>");
            out.println("</html>");
            out.close();
        }                               
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
