package migr.faces;

import comp.emigrados.QacEstabelecimentos;
import java.util.HashMap;
import migr.faces.util.JsfUtil;
import migr.session.QacEstabelecimentosFacade;

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

@ManagedBean(name = "qacEstabelecimentosController")
@SessionScoped
public class QacEstabelecimentosController {

    private QacEstabelecimentos current;
    private LazyDataModel items = null;
    @EJB
    private migr.session.QacEstabelecimentosFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;
    private Map<String, String> autoCompleteFilter = new HashMap<String, String>();

    public QacEstabelecimentosController() {
    }

    public QacEstabelecimentos getSelected() {
        if (current == null) {
            current = new QacEstabelecimentos();
            selectedItemIndex = -1;
        }
        return current;
    }

    private QacEstabelecimentosFacade getFacade() {
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
        current = (QacEstabelecimentos) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new QacEstabelecimentos();
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
        current = (QacEstabelecimentos) getItems().getRowData();
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
        current = (QacEstabelecimentos) getItems().getRowData();
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

        public List<QacEstabelecimentos> autoComplete(String dadosFiltro){
        
        autoCompleteFilter.clear();
        autoCompleteFilter.put("globalFilter",dadosFiltro);
        autoCompleteFilter.put("qacEstabelecimentosPK.cdEstab",dadosFiltro);
        autoCompleteFilter.put("dsComp",dadosFiltro);
        
        return getFacade().findRange( new int[]{0, 10}, null, null, autoCompleteFilter);
    }

    
    
    @FacesConverter(forClass = QacEstabelecimentos.class, value="QacEstabelecimentos")
    public static class QacEstabelecimentosControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            QacEstabelecimentosController controller = (QacEstabelecimentosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "qacEstabelecimentosController");
            return controller.ejbFacade.find(getKey(value));
        }

        comp.emigrados.QacEstabelecimentosPK getKey(String value) {
            comp.emigrados.QacEstabelecimentosPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new comp.emigrados.QacEstabelecimentosPK();
            key.setSiglaE(values[0]);
            key.setCdEstab(values[1]);
            return key;
        }

        String getStringKey(comp.emigrados.QacEstabelecimentosPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getSiglaE());
            sb.append(SEPARATOR);
            sb.append(value.getCdEstab());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof QacEstabelecimentos) {
                QacEstabelecimentos o = (QacEstabelecimentos) object;
                return getStringKey(o.getQacEstabelecimentosPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + QacEstabelecimentosController.class.getName());
            }
        }
    }
}