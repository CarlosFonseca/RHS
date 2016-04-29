package comp.faces;

import comp.entities.EnqFuncaoRequisitos;
import comp.faces.util.JsfUtil;
import comp.session.EnqFuncaoRequisitosFacade;

import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import org.primefaces.model.LazyDataModel;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

@ManagedBean(name = "enqFuncaoRequisitosController")
@SessionScoped
public class EnqFuncaoRequisitosController {

    private EnqFuncaoRequisitos current;
    private LazyDataModel items = null;
    @EJB
    private comp.session.EnqFuncaoRequisitosFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;

    public EnqFuncaoRequisitosController() {
    }
    
    
    public String doSearch(){
        return null;
    }
    

    public EnqFuncaoRequisitos getSelected() {
        if (current == null) {
            current = new EnqFuncaoRequisitos();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EnqFuncaoRequisitosFacade getFacade() {
        return ejbFacade;
    }

    public int getTableWidth() {
        tableWidth = ((getSelected().getClass().getDeclaredFields().length - 6) * 115);
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }

    private void beforeSave() throws Exception {
        getSelected().setLogNre(JsfUtil.getUserPrincipal());
        getSelected().setDtLog(new java.util.Date());
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (EnqFuncaoRequisitos) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new EnqFuncaoRequisitos();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            beforeSave();
            getFacade().create(current);
            recreateModel();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundle").getString("CreateSuccessMessage"));
            return prepareCreate();
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundle").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public String prepareEdit() {
        current = (EnqFuncaoRequisitos) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            beforeSave();
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundle").getString("UpdateSuccessMessage"));
            return "View";
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundle").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public void prepareDestroy() {
        current = (EnqFuncaoRequisitos) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundle").getString("DeleteSuccessMessage"));
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundle").getString("PersistenceErrorOccured"));
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

    public LazyDataModel getItems() {
        if (items == null) {
            items = new LazyDataModel() {

                @Override
                public List load(int fist, int pageSize, String sortField, SortOrder sortOder, Map filters) {
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

    @FacesConverter(forClass = EnqFuncaoRequisitos.class)
    public static class EnqFuncaoRequisitosControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EnqFuncaoRequisitosController controller = (EnqFuncaoRequisitosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "enqFuncaoRequisitosController");
            return controller.ejbFacade.find(getKey(value));
        }

        comp.entities.EnqFuncaoRequisitosPK getKey(String value) {
            comp.entities.EnqFuncaoRequisitosPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new comp.entities.EnqFuncaoRequisitosPK();
            key.setFuncao(Integer.parseInt(values[0]));
            key.setRequisito(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(comp.entities.EnqFuncaoRequisitosPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getFuncao());
            sb.append(SEPARATOR);
            sb.append(value.getRequisito());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof EnqFuncaoRequisitos) {
                EnqFuncaoRequisitos o = (EnqFuncaoRequisitos) object;
                return getStringKey(o.getEnqFuncaoRequisitosPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EnqFuncaoRequisitosController.class.getName());
            }
        }
    }
}