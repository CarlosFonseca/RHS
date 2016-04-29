/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.faces;

import comp.view.IhtDiferencas;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import migr.session.IhtDiferencasFacade;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author bnf02107
 */
@ManagedBean(name="IhtDiferencasController")
@SessionScoped
public class IhtDiferencasController {
    private IhtDiferencas current;
    private LazyDataModel items = null;
    @EJB
    private migr.session.IhtDiferencasFacade ejbFacade; 
    private int selectedItemIndex;
    private int tableWidth;
    
    /** Creates a new instance of IhtDiferencasController */
    public IhtDiferencasController() {
    }
    
        public IhtDiferencas getSelected() {
        if (current == null) {
            current = new IhtDiferencas();
            selectedItemIndex = -1;
        }
        return current;
    }

    private IhtDiferencasFacade getFacade() {
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
                    List lazyCad = null;
//                    if (todos==false)
                        lazyCad = getFacade().findRange(new int[]{fist, fist + pageSize},  sortField,  sortOder, filters);
                      //  lazyCad = getFacade().findAoServico(new int[]{fist, fist + pageSize},  sortField,  sortOder, filters);
                    
//                    else     
//                        lazyCad = getFacade().findAoServicoComIHT(new int[]{fist, fist + pageSize},  sortField,  sortOder, filters);
                    return lazyCad;
                }
            };

            items.setPageSize(10);
            items.setRowCount(getFacade().count());
        }
        return items;
    }
    
    
}
