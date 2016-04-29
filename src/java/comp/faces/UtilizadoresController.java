package comp.faces;

import comp.entities.Utilizadores;
import comp.faces.util.JsfUtil;
import comp.session.UtilizadoresFacade;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean(name = "utilizadoresController")
@SessionScoped
public class UtilizadoresController {

    private Utilizadores current;
    private LazyDataModel items = null;
    @EJB
    private comp.session.UtilizadoresFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;
    private String confirmaPass = "";
    private Map<String, String> autoCompleteFilter = new HashMap<String, String>();
    
    public UtilizadoresController() {
    }

    public Utilizadores getSelected() {
        if (current == null) {
            current = new Utilizadores();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UtilizadoresFacade getFacade() {
        return ejbFacade;
    }

    public int getTableWidth() {
        tableWidth = ((getSelected().getClass().getDeclaredFields().length - 6) * 115);
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }

    public String getConfirmaPass() {
        return confirmaPass;
    }

    public void setConfirmaPass(String confirmaPass) {
        this.confirmaPass = confirmaPass;
    }
    
    private void beforeSave() throws Exception {
       getSelected().setPalavraChave(SHA2(getSelected().getPalavraChave()));
        getSelected().setLogNre(JsfUtil.getUserPrincipal());
        getSelected().setDtLog(new java.util.Date());
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Utilizadores) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Utilizadores();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            beforeSave();
            getFacade().create(current);
            recreateModel();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CreateSuccessMessage"));
            return prepareCreate();
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public String prepareEdit() {
        current = (Utilizadores) getItems().getRowData();
        current.setPalavraChave("");
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }
    
    public void prepareEditIndividual(ComponentSystemEvent  event){
        current = getFacade().find(JsfUtil.getUserPrincipal());
        current.setPalavraChave("");
    }

  
    public String update() {
        if (!this.confirmaPass.isEmpty() && !this.confirmaPass.equals(getSelected().getPalavraChave())){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("resources/Bundle").getString("UtilizadoresErro_palavraChave"));
            return null;
        }
        
        try {
            beforeSave();
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UpdateSuccessMessage"));
            return "View";
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public void prepareDestroy() {
        current = (Utilizadores) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
    }

    public String destroy() {
        performDestroy();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DeleteSuccessMessage"));
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

   private String capturaPerfis(){
        
        FacesContext fc = FacesContext.getCurrentInstance();
	String subURL = fc.getExternalContext().getRequestServletPath();   
        
        if (subURL.contains("/emp/")){
            return "emp";
        } else {
            return "";
        }
    }
    
    public LazyDataModel getItems() {
        if (items == null) {
            items = new LazyDataModel() {

                @Override
                public List load(int fist, int pageSize, String sortField, SortOrder sortOder, Map filters) {
                    if (capturaPerfis().equals("emp")){
                        filters.put("nre",JsfUtil.getUserPrincipal());                                                       
                    }                    
                    List lazyCad = getFacade().findRange(new int[]{fist, fist + pageSize}, sortField, sortOder, filters);
                    return lazyCad;
                }
            };

            items.setPageSize(10);
            items.setRowCount(getFacade().count());
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public List<Utilizadores> autoComplete(String dadosFiltro){
        
        autoCompleteFilter.clear();
        autoCompleteFilter.put("globalFilter",dadosFiltro);
        autoCompleteFilter.put("nre",dadosFiltro);
        autoCompleteFilter.put("nomeComp",dadosFiltro);
        
        return getFacade().findRange( new int[]{0, 10}, null, null, autoCompleteFilter);
    }
    
    public String SHA2(String in){

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
    
    @FacesConverter(forClass = Utilizadores.class, value="Utilizadores")
    public static class UtilizadoresControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UtilizadoresController controller = (UtilizadoresController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "utilizadoresController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Utilizadores) {
                Utilizadores o = (Utilizadores) object;
                return getStringKey(o.getNre());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UtilizadoresController.class.getName());
            }
        }
    }
}