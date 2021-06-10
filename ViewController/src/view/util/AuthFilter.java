package view.util;

import java.io.IOException;

import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import oracle.adf.share.ADFContext;

public class AuthFilter implements Filter {
    
    private static final String WELCOME_PAGE = "/welcome";
    private static final String WELCOME_PARAMETER = "username=";
    private static final String WARNING_PAGE = "/warningNotLoggedPage";
    
    public AuthFilter() {
        super();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //System.out.println("AUTH FILTER INIT!!!");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        //System.out.println("DO FILTER!!!");
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        String path = httpServletRequest.getPathInfo();
        String context = httpServletRequest.getContextPath();
        String servlet = httpServletRequest.getServletPath();
        //StringBuffer url = httpServletRequest.getRequestURL();
        String username = httpServletRequest.getParameter("username");
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        String user = (String) session.get("user");
        
        /*HttpSession session = httpServletRequest.getSession();
        String ulss = (String)session.getAttribute("ulss");
        String tpscr = (String)session.getAttribute("scr");
        String user = (String)session.getAttribute("user");
        */
        if (user == null && username != null && !username.trim().isEmpty()) {
            //session.setAttribute("user", username);
            session.put("user", username);
            user = username;
        }
        
        boolean welcomeOrWarning = path.endsWith(WELCOME_PAGE) || path.endsWith(WARNING_PAGE);
        if ((user == null || ulss == null || tpscr == null) && !welcomeOrWarning) {
            String welcomeString = context + servlet + WELCOME_PAGE;
            if (user != null && ! user.trim().isEmpty()) {
                welcomeString += "?" + WELCOME_PARAMETER + user;
            }
            System.out.println("NOT CORRECTLY LOGGED IN: REDIRECTING TO WELCOME PAGE");
            HttpServletResponse.class.cast(servletResponse).sendRedirect(welcomeString);
        }/* else if ((ulss == null || tpscr == null) && !welcomeOrWarning) {
            HttpServletResponse.class.cast(servletResponse).sendRedirect(context + servlet + WARNING_PAGE);
        }*/ else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        //System.out.println("AUTH FILTER DESTROY!!!");
    }
}
