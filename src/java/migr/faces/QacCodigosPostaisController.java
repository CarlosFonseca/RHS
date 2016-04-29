package migr.faces;

import comp.emigrados.QacCodigosPostais;
import java.util.HashMap;
import migr.faces.util.JsfUtil;
import migr.session.QacCodigosPostaisFacade;

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

@ManagedBean(name = "qacCodigosPostaisController")
@SessionScoped
public class QacCodigosPostaisController {

    private QacCodigosPostais current;
    private LazyDataModel items = null;
    @EJB
    private migr.session.QacCodigosPostaisFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;
    private Map<String, String> autoCompleteFilter = new HashMap<String, String>();

    public QacCodigosPostaisController() {
    }

    public QacCodigosPostais getSelected() {
        if (current == null) {
            current = new QacCodigosPostais();
            selectedItemIndex = -1;
        }
        return current;
    }

    private QacCodigosPostaisFacade getFacade() {
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
        current = (QacCodigosPostais) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new QacCodigosPostais();
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
        current = (QacCodigosPostais) getItems().getRowData();
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
        current = (QacCodigosPostais) getItems().getRowData();
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
    
        public List<QacCodigosPostais> autoComplete(String dadosFiltro){
        
        autoCompleteFilter.clear();
        autoCompleteFilter.put("globalFilter",dadosFiltro);
        autoCompleteFilter.put("nre",dadosFiltro);
        autoCompleteFilter.put("nomeComp",dadosFiltro);
        
        return getFacade().findRange( new int[]{0, 10}, null, null, autoCompleteFilter);
    }


    @FacesConverter(forClass = QacCodigosPostais.class, value="QacCodigosPostais" )
    public static class QacCodigosPostaisControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            QacCodigosPostaisController controller = (QacCodigosPostaisController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "qacCodigosPostaisController");
            return controller.ejbFacade.find(getKey(value));
        }

        comp.emigrados.QacCodigosPostaisPK getKey(String value) {
            comp.emigrados.QacCodigosPostaisPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new comp.emigrados.QacCodigosPostaisPK();
            key.setNrOrdem(Integer.parseInt(values[0]));
            key.setCdPostal(values[1]);
            key.setCdPais(values[2]);
            return key;
        }

        String getStringKey(comp.emigrados.QacCodigosPostaisPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getNrOrdem());
            sb.append(SEPARATOR);
            sb.append(value.getCdPostal());
            sb.append(SEPARATOR);
            sb.append(value.getCdPais());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof QacCodigosPostais) {
                QacCodigosPostais o = (QacCodigosPostais) object;
                return getStringKey(o.getQacCodigosPostaisPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + QacCodigosPostaisController.class.getName());
            }
        }
    }
}