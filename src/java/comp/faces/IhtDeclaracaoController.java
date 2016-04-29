package comp.faces;

import comp.entities.IhtDeclaracao;
import comp.file.DocxExporterIHT;
import comp.faces.util.JsfUtil;
import comp.session.IhtDeclaracaoFacade;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import javax.servlet.http.HttpServletResponse;
import comp.emigrados.Cadastros;
import org.primefaces.model.SortOrder;


@ManagedBean (name="ihtDeclaracaoController")
@SessionScoped
public class IhtDeclaracaoController {

    private IhtDeclaracao current;
    private LazyDataModel items = null;
    @EJB private comp.session.IhtDeclaracaoFacade ejbFacade;
    private int selectedItemIndex;
    private int tableWidth;    
    private Cadastros cadastros;
    
    public IhtDeclaracaoController() {
    }

    public String doSearch(){
//        return null;
    return prepareCreate();
    }

    public Cadastros getCadastros() {
        return cadastros;
    }

    public void setCadastros(Cadastros cadastros) {
        this.cadastros = cadastros;
    }
       
    public IhtDeclaracao getSelected() {
        if (current == null) {
            current = new IhtDeclaracao();
            selectedItemIndex = -1;
        }
        return current;
    }

    private IhtDeclaracaoFacade getFacade() {
        return ejbFacade;
    }

    public int getTableWidth(){        
        tableWidth = ((getSelected().getClass().getDeclaredFields().length -6)*115);
        return tableWidth ;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }

    private void beforeSave() throws Exception  {
        getSelected().setLogNre(JsfUtil.getUserPrincipal());
        getSelected().setDtLog(new java.util.Date());                 
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (IhtDeclaracao)getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {

            current = new IhtDeclaracao();
            selectedItemIndex = -1;
            
            getSelected().setNre(cadastros);
            
//     na segunda versão de IHT deixou-se usar a descrição            
//            if(cadastros.getIhtDados().getFuncoesInternas()!=null){
//                getSelected().setDsFuncDesc(cadastros.getIhtDados().getFuncoesInternas().getDescricao());    
//            }
            
            getSelected().setDtPessoa(new java.util.Date());
            
            if (cadastros.getIhtDados().getCategoriaId()!=null)
                getSelected().setCategoriaId( cadastros.getIhtDados().getCategoriaId());
            
            if (cadastros.getIhtDados().getC3() != null)
                getSelected().setValor(cadastros.getIhtDados().getC3());

            if (cadastros.getIhtDados().getAlineaTextoId() != null)
            getSelected().setAlineaTextoId(cadastros.getIhtDados().getAlineaTextoId());

            if (cadastros.getIhtDados().getQacEmpresas() != null)
            getSelected().setEmpresa(cadastros.getIhtDados().getQacEmpresas());

      
            return "Create";

    }
    
    public String create() {
        try {
            beforeSave();
            getFacade().create(current);
//            nrePesq = 0;
			//recreateModel();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("resources/Bundle").getString("CreateSuccessMessage"));
            return "View";
        } catch (Exception e) {
	    //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("resources/Bundle").getString("PersistenceErrorOccured"));
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }
    
   
    public String performExport2Docx() {
         DocxExporterIHT docxExporterIHT = new DocxExporterIHT();
              
         HttpServletResponse httpServletResponse  = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
         ByteArrayOutputStream baos = null;
       
         try {
             baos = docxExporterIHT.BAOSDocx(current);
         } catch (Exception e) {
             JsfUtil.addErrorMessage(e, e.getMessage());
         }

        try {    
              if (baos!=null)  
              JsfUtil.writeFileToResponse(httpServletResponse,  baos,"IHT.docx");
        } catch (IOException ex) {
                JsfUtil.addErrorMessage("Ocorreu um erro no processo de Exportar o ficheiro");
        }
         
         
         return null;
     }
     
     
    
    
    public String prepareEdit() {
        current = (IhtDeclaracao)getItems().getRowData();
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
        current = (IhtDeclaracao)getItems().getRowData();
        selectedItemIndex =  getItems().getRowIndex();        
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
            selectedItemIndex = count-1;
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex+1}).get(0);
        }
    }

    public LazyDataModel getItems() {
        if (items == null) {
            items = new LazyDataModel() {

                @Override
                public List load(int fist, int pageSize, String sortField, SortOrder sortOder, Map filters) {
                    List lazyCad  =  getFacade().findRange(new int[]{fist,fist+pageSize}, sortField, sortOder, filters);
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
//        nrePesq = 0 ;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }
    
    public String iht(int nre){
        String Isencao ="";
        try {
            Isencao = ejbFacade.findIHTByNre(nre).getTipoId().getDsComp();
        } catch (Exception e) {
            Isencao= e.getMessage();
        }

        return Isencao;
    }
    

    @FacesConverter(forClass=IhtDeclaracao.class)
    public static class IhtDeclaracaoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            IhtDeclaracaoController controller = (IhtDeclaracaoController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ihtDeclaracaoController");
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
            if (object instanceof IhtDeclaracao) {
                IhtDeclaracao o = (IhtDeclaracao) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: "+IhtDeclaracaoController.class.getName());
            }
        }

    }

}