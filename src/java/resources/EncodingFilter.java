/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author bnf02107
 */
public class EncodingFilter implements Filter {
    
    
     public void init(FilterConfig filterConfig) throws ServletException {
     }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        request.setCharacterEncoding("UTF-8");
         chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
