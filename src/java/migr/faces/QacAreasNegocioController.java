package migr.faces;

import comp.emigrados.QacAreasNegocio;
import migr.faces.util.JsfUtil;
import migr.session.QacAreasNegocioFacade;

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

@ManagedBean(name = "qacAreasNegocioController")
@SessionScoped
public class QacAreasNegocioController {

    private QacAreasNegocio current;
    private LazyDataModel items = null;
    @EJB
    private migr.session.QacAreasNegocioFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;

    public QacAreasNegocioController() {
    }

    public QacAreasNegocio getSelected() {
        if (current == null) {
            current = new QacAreasNegocio();
            selectedItemIndex = -1;
        }
        return current;
    }

    private QacAreasNegocioFacade getFacade() {
        return ejbFacade;
    }

    public int getTableWidth() {
        tableWidth = ((getSelected().getClass().getDeclaredFields().length - 6) * 115);
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }


    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (QacAreasNegocio) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new QacAreasNegocio();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
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
        current = (QacAreasNegocio) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
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
        current = (QacAreasNegocio) getItems().getRowData();
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

    @FacesConverter(forClass = QacAreasNegocio.class)
    public static class QacAreasNegocioControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            QacAreasNegocioController controller = (QacAreasNegocioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "qacAreasNegocioController");
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
            if (object instanceof QacAreasNegocio) {
                QacAreasNegocio o = (QacAreasNegocio) object;
                return getStringKey(o.getCdSbu());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + QacAreasNegocioController.class.getName());
            }
        }
    }
}