/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.*;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import org.primefaces.model.SortOrder;

/**
 *
 * @author bnf02107
 */
@Stateless
public class QacAreasNegocioFacade extends AbstractFacade<QacAreasNegocio> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public QacAreasNegocioFacade() {
        super(QacAreasNegocio.class);
    }
    
  
       public List<QacAreasNegocio> findRange(int[] range, String sortField, SortOrder sortOder, Map filters) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root root = cq.from(entityClass);
        
        Predicate condicao = null;
        if (!filters.isEmpty() 
            || (filters.containsKey("globalFilter") && "".equals(filters.get("globalFilter").toString()) && filters.containsValue("%")) ){
            condicao = addChoicePredicate(new Predicate[filters.size()], filters, cb, root);
        }
        cq.select(root);
        
        Order[] ordenar = {cb.asc(root.get(QacAreasNegocio_.nrOrdem))};
        
         makeOrder(ordenar, sortField, sortOder, cb, root);

        return findCriteria(range,condicao, ordenar);
     }
       
  }
