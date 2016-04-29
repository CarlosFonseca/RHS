package migr.faces;

import java.text.DateFormat;
import java.text.ParseException;
import comp.emigrados.InfoEmpresas;
import migr.faces.util.JsfUtil;
import migr.session.InfoEmpresasFacade;

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

@ManagedBean(name = "pginfoEmpresasController")
@SessionScoped
public class InfoEmpresasController {

    private InfoEmpresas current;
    private LazyDataModel items = null;
    @EJB
    private migr.session.InfoEmpresasFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;

    public InfoEmpresasController() {
    }

    public InfoEmpresas getSelected() {
        if (current == null) {
            current = new InfoEmpresas();
            selectedItemIndex = -1;
        }
        return current;
    }

    private InfoEmpresasFacade getFacade() {
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
        current = (InfoEmpresas) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new InfoEmpresas();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            recreateModel();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundlepg").getString("CreateSuccessMessage"));
            return prepareCreate();
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundlepg").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public String prepareEdit() {
        current = (InfoEmpresas) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundlepg").getString("UpdateSuccessMessage"));
            return "View";
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundlepg").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public String destroy() {
        current = (InfoEmpresas) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundlepg").getString("DeleteSuccessMessage"));
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundlepg").getString("PersistenceErrorOccured"));
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

    @FacesConverter(forClass = InfoEmpresas.class)
    public static class InfoEmpresasControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InfoEmpresasController controller = (InfoEmpresasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "infoEmpresasController");
            return controller.ejbFacade.find(getKey(value));
        }

        comp.emigrados.InfoEmpresasPK getKey(String value) {
            comp.emigrados.InfoEmpresasPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new comp.emigrados.InfoEmpresasPK();
            key.setSiglaE(values[0]);
            key.setCdEstrutura(Integer.parseInt(values[1]));
            key.setCadNre(Integer.parseInt(values[2]));
            try {
                key.setDtIni(DateFormat.getDateTimeInstance().parse(values[3]));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
           // key.setDtCriacao(values[3]);
            return key;
        }

        String getStringKey(comp.emigrados.InfoEmpresasPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getSiglaE());
            sb.append(SEPARATOR);
            sb.append(value.getCdEstrutura());
            sb.append(SEPARATOR);
            sb.append(value.getCadNre());
            sb.append(SEPARATOR);
            sb.append(value.getDtIni());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof InfoEmpresas) {
                InfoEmpresas o = (InfoEmpresas) object;
                return getStringKey(o.getInfoEmpresasPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + InfoEmpresasController.class.getName());
            }
        }
    }
}