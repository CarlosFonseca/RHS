package comp.faces;

import comp.emigrados.Cadastros;
import comp.entities.GlEstabelecimento;
import comp.entities.MedConvocatoria;
import comp.faces.util.JsfUtil;
import comp.session.MedConvocatoriaFacade;

import comp.view.Agenda;
import java.util.Date;
import java.util.Iterator;
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
import javax.faces.event.ActionEvent;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.SortOrder;


@ManagedBean(name = "medConvocatoriaController")
@SessionScoped
public class MedConvocatoriaController {

    private MedConvocatoria current;
    private LazyDataModel items = null;
    @EJB
    private comp.session.MedConvocatoriaFacade ejbFacade;
    @EJB
    private comp.session.AgendaFacade agendaFacade;

    private int selectedItemIndex;
    private int tableWidth;
    private ScheduleModel lazyModel;
    private ScheduleEvent event = new DefaultScheduleEvent(); 
    private Cadastros cadastros;
    
    public MedConvocatoriaController() {

        initLazyEventModel();
        
    }
    
    public String doSearch(){
        return prepareCreate(); 
    }

    public Cadastros getCadastros() {
        return cadastros;
    }

    public void setCadastros(Cadastros cadastros) {
        this.cadastros = cadastros;
    }
       
    

    public MedConvocatoria getSelected() {
        if (current == null) {
            current = new MedConvocatoria();
            selectedItemIndex = -1;
        }
        return current;
    }

    private MedConvocatoriaFacade getFacade() {
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
        current = (MedConvocatoria) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new MedConvocatoria();
        if (cadastros != null)
        current.setCadNre(cadastros);
     //   initLazyEventModel();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        String s = "";
        try {
            beforeSave();
            current.setEstado('A');
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
        current = (MedConvocatoria) getItems().getRowData();
        setCadastros(current.getCadNre());
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
        current = (MedConvocatoria) getItems().getRowData();
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
    
    
    public void initLazyEventModel(){
       lazyModel = new LazyScheduleModel() {               
            @Override
            public void loadEvents(Date start, Date end) {
               clear();   
               if (current!= null){
                   Iterator<Agenda> it =   agendaFacade.findRG(current.getCadNre().getNre(), start, end).iterator();
                   while (it.hasNext()) {
                        Agenda agenda = it.next();
                        addEvent(new DefaultScheduleEvent(agenda.getDsComp(),agenda.getHrIni(),agenda.getHrFim()));
                   }  
               } 
            }
        };        
    }
    

    public ScheduleModel getLazyModel() {
        return lazyModel;
    }

   public void onDateSelect(DateSelectEvent selectEvent) {
//       GlEstabelecimento GlEstabelecimento = null;
       if (current!=null &&  current.getDtIni() != selectEvent.getDate() ){       
            current.setDtIni(selectEvent.getDate());
            current.setDtFim(selectEvent.getDate());

            if (current.getCadNre().getGlestabQacestabNre() != null)
                current.setEstab(current.getCadNre().getGlestabQacestabNre().getId());            
       } 
//        event = new DefaultScheduleEvent(Math.random() + "", selectEvent.getDate(), selectEvent.getDate());  
    } 
    
    public void onEventSelect(ScheduleEntrySelectEvent selectEvent) {  
//        event = selectEvent.getScheduleEvent();  
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
        lazyModel = null;
        cadastros = null;
        items = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = MedConvocatoria.class)
    public static class MedConvocatoriaControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MedConvocatoriaController controller = (MedConvocatoriaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "medConvocatoriaController");
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
            if (object instanceof MedConvocatoria) {
                MedConvocatoria o = (MedConvocatoria) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + MedConvocatoriaController.class.getName());
            }
        }
    }
}