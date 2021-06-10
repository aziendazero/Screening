package view.backing;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutBean {
    

    public String logoutAction() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse)ectx.getResponse();
        HttpSession session = (HttpSession)ectx.getSession(false);
        try{
            session.invalidate();
            System.out.println("Invalidata sessione " + session.getId());
            ServletContext servletContext =
                (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String logoutUrl = servletContext.getInitParameter("casServerLogoutUrl");
            ectx.redirect(logoutUrl);
            //ectx.redirect("https://cas.azero.veneto.it/logout");
            System.out.println("Redirect >>>> ");
            fc.responseComplete();
        } catch (Exception exp) {
            exp.printStackTrace();
         }
        return null;
    }
}
