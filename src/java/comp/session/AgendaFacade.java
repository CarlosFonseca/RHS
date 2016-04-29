/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.session;

import comp.view.Agenda;
import comp.view.Agenda_;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

/**
 *
 * @author bnf02107
 */
@Stateless
public class AgendaFacade extends AbstractFacade<Agenda> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public AgendaFacade() {
        super(Agenda.class);
    }
    
    public List<Agenda> findRG(Integer cadNre, Date start, Date end){
        
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root root = cq.from(entityClass);
        
        javax.persistence.criteria.Predicate[] predicate = new Predicate[2];
        predicate[0] = cb.equal(root.get(Agenda_.cadNre), cadNre);
        predicate[1] = cb.or(cb.between(root.get(Agenda_.hrIni), start, end), 
                             cb.between(root.get(Agenda_.hrFim), start, end)); 
        
        return findCriteria(null, predicate, null);  
    } 
    
}
