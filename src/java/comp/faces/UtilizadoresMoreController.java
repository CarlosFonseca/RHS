/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.faces;

import comp.entities.Utilizadores;
import comp.faces.util.JsfUtil;
import comp.session.UtilizadoresFacade;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author bnf02107
 */
@ManagedBean
@SessionScoped
public class UtilizadoresMoreController {
    
    private Utilizadores current;
    private int tabIndex = 0 ;
    private String tabId = "menu00"; 
    
    private String user = "";
    private String pwd =  "";
    
    
    @EJB
    private comp.session.UtilizadoresFacade ejbFacade;
        
//    @EJB
//    private migr.session.CadastrosFacade ejbCadastrosFacade;
//    private List<Cadastros> listCadastros;
    

    /** Creates a new instance of UtilizadoresMoreController */
    public UtilizadoresMoreController() {
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getTabId() {
        return tabId;
    }
    
    public void onChange(TabChangeEvent event) {
        Tab activeTab = event.getTab();

        try {
            tabId = activeTab.getId();
            tabIndex = Integer.parseInt(activeTab.getId().substring(4, activeTab.getId().length()));
        } catch (NumberFormatException numberFormatException) {
        }

}

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    public String doLog(){
        FacesContext context = FacesContext.getCurrentInstance();
       HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
       request.getUserPrincipal();
       String home = request.getContextPath();
       
       
       if(ejbFacade.find(user)==null){
            try {
                context.getExternalContext().redirect(home+"/rro/loginError.faces");
            } catch (IOException ex) {
                Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
            }
       }else if(ejbFacade.find(user).getPalavraChave() == null ? SHA2(pwd) != null : ejbFacade.find(user).getPalavraChave().equals(SHA2(pwd))){
            try {
                context.getExternalContext().redirect(home+"/net/emp/j_security_check?j_username="+ user + "&j_password=" +pwd);
            } catch (IOException ex) {
                Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
            }           
       }else{
            try {
                context.getExternalContext().redirect(home+"/rro/loginError.faces");
            } catch (IOException ex) {
                Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
            }       
       }

        return null;
    }
    
    
   public String dopreLog(ActionListener actionListener ){
        FacesContext context = FacesContext.getCurrentInstance();
       HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
       request.getUserPrincipal();
       String home = request.getContextPath();
       
       if (!user.equals("") && !pwd.equals("")){
       
           if(ejbFacade.find(user)==null){
                try {
                    context.getExternalContext().redirect(home+"/rro/loginError.faces");
                } catch (IOException ex) {
                    Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
                }
           }else if(ejbFacade.find(user).getPalavraChave() == null ? SHA2(pwd) != null : ejbFacade.find(user).getPalavraChave().equals(SHA2(pwd))){
                try {
                    context.getExternalContext().redirect(home+"/net/emp/j_security_check?j_username="+ user + "&j_password=" +pwd);
                } catch (IOException ex) {
                    Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
                }           
           }
      }
        return null;
    } 
    
    
    
    
    private UtilizadoresFacade getFacade() {
        return ejbFacade;
    }

//    public List<Cadastros> getListCadastros() {
//        if(listCadastros == null){
//            listCadastros = getEjbCadastrosFacade().findAll();
//        }
//        
//        return listCadastros;
//    }
//
//    public CadastrosFacade getEjbCadastrosFacade() {
//        return ejbCadastrosFacade;
//    }
     
    public String sessionUserName(HttpSession session){
           if(session.getAttribute("sessionUserName") == null || session.getAttribute("sessionUserName").equals(""));
                session.setAttribute("sessionUser", JsfUtil.getUserPrincipal());
                session.setAttribute("sessionUserName", getFacade().find(JsfUtil.getUserPrincipal()).getNomeAbrv());
           
           return (String) session.getAttribute("sessionUserName");     
    }
    
   public void logout() throws ServletException {
       user = "";
       pwd = "";
       FacesContext context = FacesContext.getCurrentInstance();
       HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
       request.getUserPrincipal();
       String home = request.getContextPath();
       try {
            request.logout();
        } catch (ServletException ex) {
           JsfUtil.addErrorMessage(ex.getMessage()) ;
        }
       
        try {
            context.getExternalContext().redirect(home);
        } catch (IOException ex) {
            Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

     public void appCange() throws ServletException { 
         
         
       String userTemp = this.user;
       String pwdTemp = this.pwd;
       this.user = "";
       this.pwd = "";
         
       FacesContext context = FacesContext.getCurrentInstance();
       HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
       request.getUserPrincipal();
       String home = request.getContextPath();
       try {
            request.logout();
        } catch (ServletException ex) {
           JsfUtil.addErrorMessage(ex.getMessage()) ;
        }
       
        try {
            context.getExternalContext().redirect(home.replace("RHS", "rh")+"/login.faces?j_username="+ userTemp + "&j_password=" +pwdTemp);
        } catch (IOException ex) {
            Logger.getLogger(UtilizadoresMoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
   
        
//    public void actualizaUtilizadores(){
//        
//        String msg = "";
//        ImportarDados importarDados = new ImportarDados();
//        
//        msg = importarDados.doImport();
//        
//        if(msg.equals(""))
//            JsfUtil.addSuccessMessage("Foram actualizados os Utilizadores");
//        else
//            JsfUtil.addErrorMessage(msg);
//    }
    
        
    private String SHA2(String in){

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ex.getMessage());
        }
        
        md.update(in.getBytes());    
        
        byte byteData[] = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        return sb.toString();

    }
    
    
}
