package comp.faces;

import comp.entities.EnqFuncao;
import comp.faces.util.JsfUtil;
import comp.faces.util.PDFtoEnquadramento;
import comp.session.EnqFuncaoFacade;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
//import pg.entities.QacAreasNegocio;
//import pg.entities.QacEmpresas;

@ManagedBean(name = "enqFuncaoController")
@SessionScoped
public class EnqFuncaoController {

    private EnqFuncao current;
    private LazyDataModel items = null;
    @EJB
    private comp.session.EnqFuncaoFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;
    private List<EnqFuncao> lazyCd;
    
//    private pg.entities.QacAreasNegocio qacAreasNegocio;
//    private comp.entities.QacAreasNegocio areasNegocio;
//    private pg.entities.QacEmpresas qacEmpresas;
//    private comp.entities.QacEmpresas empresas;
    
    public EnqFuncaoController() {
    }

//    public QacAreasNegocio getQacAreasNegocio() {
//        return qacAreasNegocio;
//    }
//
//    public void setQacAreasNegocio(QacAreasNegocio qacAreasNegocio) {
//        this.qacAreasNegocio = qacAreasNegocio;
//    }
//
//    public QacEmpresas getQacEmpresas() {
//        return qacEmpresas;
//    }
//
//    public void setQacEmpresas(QacEmpresas qacEmpresas) {
//        this.qacEmpresas = qacEmpresas;
//    }
//    
    public EnqFuncao getSelected() {
        if (current == null) {
            current = new EnqFuncao();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EnqFuncaoFacade getFacade() {
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
//        if (qacEmpresas!= null){
//            empresas = new comp.entities. QacEmpresas(qacEmpresas.getSiglaE(), qacEmpresas.getCdRepFiscal(), qacEmpresas.getDsSocial(), qacEmpresas.getNrPssColect(), qacEmpresas.getCapSocial(), qacEmpresas.getTxRepNacn(), qacEmpresas.getTxRepEstg(), qacEmpresas.getTxRepPubl(), qacEmpresas.getNomeInd(), qacEmpresas.getNrOrdem());
//            getSelected().setEmpresa(empresas);
//        }
//        if(qacAreasNegocio!=null){
//            areasNegocio = new comp.entities.QacAreasNegocio(qacAreasNegocio.getCdSbu(), qacAreasNegocio.getDsComp(), qacAreasNegocio.getDsRedz(), qacAreasNegocio.getDsAbrv(), qacAreasNegocio.getNrOrdem());
//            getSelected().setEstG1(areasNegocio); 
//        }
        
        getSelected().setLogNre(JsfUtil.getUserPrincipal());
        getSelected().setDtLog(new java.util.Date());
    }
    
//    private void beforePrepareEdit(){
//        if (getSelected().getEmpresa()!= null) 
//            qacEmpresas = new QacEmpresas(getSelected().getEmpresa().getSiglaE());
//        if (getSelected().getEstG1()!= null) 
//            qacAreasNegocio = new QacAreasNegocio(getSelected().getEstG1().getCdSbu());
//    }
    
    
    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (EnqFuncao) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new EnqFuncao();
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
        current = (EnqFuncao) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
//        beforePrepareEdit();
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
        current = (EnqFuncao) getItems().getRowData();
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
                    lazyCd = lazyCad;
                    return lazyCad;
                }
            };

            items.setPageSize(10);
            items.setRowCount(getFacade().count());
        }
        return items;
    }

  public String doPDF(){
      
      PDFtoEnquadramento pdf = new PDFtoEnquadramento();
        try {
            pdf.exportaList( FacesContext.getCurrentInstance(), "Perfis", lazyCd);
        } catch (IOException ex) {
            Logger.getLogger(EnqFuncaoController.class.getName()).log(Level.SEVERE, null, ex);
        }
      
      return null;
  }
    
    private void recreateModel() {
        items = null;
//        qacAreasNegocio = null;
//        qacEmpresas = null;
//        areasNegocio = null;
//        empresas = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = EnqFuncao.class)
    public static class EnqFuncaoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EnqFuncaoController controller = (EnqFuncaoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "enqFuncaoController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof EnqFuncao) {
                EnqFuncao o = (EnqFuncao) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EnqFuncaoController.class.getName());
            }
        }
    }
}