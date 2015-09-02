package ru.icl.test.filters;

import java.io.IOException;
import static java.rmi.server.LogStream.log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.icl.test.db.Database;

public class myFilter implements Filter {         
    
    private static final boolean debug = true;

    private FilterConfig filterConfig = null;
    
    int count = 1;
    
    public myFilter() {
    }    
    
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain)
            throws IOException, ServletException {                
        /* 
        Структура нашего JSON объекта:
        { id_key : {msg_key : [message], 
                       name_Key: name} 
        }
        где: 
            id_key - сессия клиента           
            name - имя клиента
            message - сообщения клиента (массив)                    
        */        
        HttpServletRequest request = (HttpServletRequest) req;        
        JSONObject jsonOb = (JSONObject) request.getServletContext().getAttribute("sessionMap");
        if (jsonOb==null) {
            jsonOb = new JSONObject();
        }  
        
        HttpSession session = request.getSession(true);        
        String name = (String) session.getAttribute("name");                        
        //String name = (String) request.getServletContext().getAttribute("name");                                                                       
        System.out.println(name);
        String msg = request.getParameter("msg");    
        String id = session.getId();

        session.setAttribute("name", name);
        JSONArray jsonArr;            
        if (session.isNew()) {                
            jsonArr = new JSONArray();
        } else { 
            jsonArr = (JSONArray)session.getAttribute("jmessage");                
        }

        jsonArr.add(msg); 
        session.setAttribute("jmessage", jsonArr);            
        JSONObject jsonName = new JSONObject();
        jsonName.put("name", name);
        jsonName.put("msg", jsonArr);
        jsonOb.put(id, jsonName);                        
        request.getServletContext().setAttribute("sessionMap", jsonOb);                 
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
        }*/         
        chain.doFilter(req, resp);        
    }                                                 

    public void destroy() {        
    }

    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("myFilter:Initializing filter");
            }
        }
    } 
}
