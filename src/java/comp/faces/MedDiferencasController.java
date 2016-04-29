/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.faces;

import comp.emigrados.QacEmpresas;
import comp.session.MedDiferencasFacade;
import comp.view.MedDiferencas;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author bnf02107
 */
@ManagedBean
@SessionScoped
public class MedDiferencasController {
    
    
    private MedDiferencas current;
    private LazyDataModel items = null;
    @EJB
    private comp.session.MedDiferencasFacade ejbFacade;
    private int tableWidth;
    private QacEmpresas qacEmpresas = null;
    private String sitGeografica = "";
    
    /** Creates a new instance of MedDiferencasController */
    public MedDiferencasController() {
    }

    public QacEmpresas getQacEmpresas() {
        return qacEmpresas;
    }

    public void setQacEmpresas(QacEmpresas qacEmpresas) {
        this.qacEmpresas = qacEmpresas;
    }

    public String getSitGeografica() {
        return sitGeografica;
    }

    public void setSitGeografica(String sitGeografica) {
        this.sitGeografica = sitGeografica;
    }
    
    public MedDiferencas getSelected() {
        if (current == null) {
            current = new MedDiferencas();
        }
        return current;
    }

    private MedDiferencasFacade getFacade() {
        return ejbFacade;
    }

    public int getTableWidth() {
        tableWidth = ((getSelected().getClass().getDeclaredFields().length - 6) * 115);
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }
 
    public LazyDataModel getItems() {
        if (items == null) {
            items = new LazyDataModel() {

                @Override
                public List load(int fist, int pageSize, String sortField, SortOrder sortOder, Map filters) {
                    if (qacEmpresas != null)                    
                        filters.put("siglaE", qacEmpresas.getSiglaE());
                    if (!"".equals(sitGeografica))
                        filters.put("sitGeo", sitGeografica);
                    List lazyCad = getFacade().findRange(new int[]{fist, fist + pageSize}, sortField, sortOder, filters);
                    return lazyCad;
                }
            };

            items.setPageSize(10);
            items.setRowCount(getFacade().count());
        }
        return items;
    }
        

    
}
