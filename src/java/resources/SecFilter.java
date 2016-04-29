/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

/**
 *
 * @author bnf02107
 */
import java.io.IOException;
import java.util.logging.LogRecord;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
public class SecFilter implements Filter {
 //Pagina de login
 
 private static final String SIGNON_PAGE_URI = "login.faces";
 private String UserPrincipal = "";
 private String sessionUser ="";
 
 
 
 public void init(FilterConfig filterConfig) throws ServletException {
 }
 
 public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
 HttpServletResponse response = (HttpServletResponse) res;
 HttpServletRequest request = (HttpServletRequest) req;
 if (!this.authorize((HttpServletRequest) req)) {
 request.getRequestDispatcher(SIGNON_PAGE_URI).forward(req, res);
 } else {
 response.setHeader("Cache-Control", "no-store");
 response.setHeader("Pragma", "no-cache");
 response.setDateHeader("Expires", 0);
 chain.doFilter(req, res);
 }
 }
 
 public void destroy() {
 }
 //Metodo que verifica o bean em sessao se esta logado.
 
 private boolean authorize(HttpServletRequest req) {
 boolean retorno = false;
 HttpSession session = req.getSession(false);
 if (session != null) {
     try {
        sessionUser = session.getAttribute("sessionUser").toString();              
     } catch (Exception e) {
     }
    


//    if (!sessionUser.equals("off") || sessionUser != null ) {
        retorno = true;
//    }
 }
 return retorno;
 }
 
 public boolean isLoggable(LogRecord record) {
 throw new UnsupportedOperationException("Not supported yet.");
 }
}