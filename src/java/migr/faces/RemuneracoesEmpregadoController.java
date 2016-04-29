package migr.faces;

import java.text.DateFormat;
import java.text.ParseException;
import comp.emigrados.RemuneracoesEmpregado;
import migr.faces.util.JsfUtil;
import migr.session.RemuneracoesEmpregadoFacade;

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

@ManagedBean(name = "pgremuneracoesEmpregadoController")
@SessionScoped
public class RemuneracoesEmpregadoController {

    private RemuneracoesEmpregado current;
    private LazyDataModel items = null;
    @EJB
    private migr.session.RemuneracoesEmpregadoFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;

    public RemuneracoesEmpregadoController() {
    }

    public RemuneracoesEmpregado getSelected() {
        if (current == null) {
            current = new RemuneracoesEmpregado();
            selectedItemIndex = -1;
        }
        return current;
    }

    private RemuneracoesEmpregadoFacade getFacade() {
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
        current = (RemuneracoesEmpregado) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new RemuneracoesEmpregado();
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
        current = (RemuneracoesEmpregado) getItems().getRowData();
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
        current = (RemuneracoesEmpregado) getItems().getRowData();
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

        public String iht(int nre){

        int cd_linha = 0;
        String Isencao ="";

            try {
                cd_linha = ejbFacade.findIHTByNre(nre).getRemuneracoesEmpregadoPK().getCdLinha();
            } catch (Exception e) {
            }


        if (cd_linha == 1)
                Isencao = "P";

         if (cd_linha == 2)
                Isencao = "T";

        return Isencao;
    }
    
    
    @FacesConverter(forClass = RemuneracoesEmpregado.class)
    public static class RemuneracoesEmpregadoControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RemuneracoesEmpregadoController controller = (RemuneracoesEmpregadoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "remuneracoesEmpregadoController");
            return controller.ejbFacade.find(getKey(value));
        }

        comp.emigrados.RemuneracoesEmpregadoPK getKey(String value) {
            comp.emigrados.RemuneracoesEmpregadoPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new comp.emigrados.RemuneracoesEmpregadoPK();
            key.setCadNre(Integer.parseInt(values[0]));
            key.setCdLinha(Integer.parseInt(values[1]));
            key.setCdEnquadra(Integer.parseInt(values[2]));
            try {
                key.setDtCriacao(DateFormat.getDateTimeInstance().parse(values[3]));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
           // key.setDtCriacao(values[3]);
            return key;
        }

        String getStringKey(comp.emigrados.RemuneracoesEmpregadoPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getCadNre());
            sb.append(SEPARATOR);
            sb.append(value.getCdLinha());
            sb.append(SEPARATOR);
            sb.append(value.getCdEnquadra());
            sb.append(SEPARATOR);
            sb.append(value.getDtCriacao());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RemuneracoesEmpregado) {
                RemuneracoesEmpregado o = (RemuneracoesEmpregado) object;
                return getStringKey(o.getRemuneracoesEmpregadoPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RemuneracoesEmpregadoController.class.getName());
            }
        }
    }
}