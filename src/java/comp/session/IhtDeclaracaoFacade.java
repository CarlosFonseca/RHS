/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.session;

import comp.entities.IhtDeclaracao;
import comp.entities.IhtDeclaracao_;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
/**
 *
 * @author bnf02107
 */
@Stateless
public class IhtDeclaracaoFacade extends AbstractFacade<IhtDeclaracao> {
    
//    @Resource SessionContext ctx;
      
    
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public IhtDeclaracaoFacade() {
        super(IhtDeclaracao.class);
    }
    
//    public void edit(IhtDeclaracao entity) {
//         String callerKey = ctx.getCallerPrincipal().getName();   
//        if (getEntityManager().find(entityClass, entity.getId()).getLogNre().equals(callerKey))
//            getEntityManager().merge(entity);            
//    }
    
    
    public IhtDeclaracao findIHTByNre(int nre){
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root rt = cq.from(entityClass);
        
        javax.persistence.criteria.Subquery<Integer> sbMaxID =  cq.subquery(Integer.class);
        javax.persistence.criteria.Root sbrtMaxID= sbMaxID.from(IhtDeclaracao.class); 
        
        sbMaxID.select(cb.max(sbrtMaxID.get(IhtDeclaracao_.id)))
                .where(cb.equal(sbrtMaxID.get(IhtDeclaracao_.nre), nre));
        
        Predicate[] condicao = {cb.equal(rt.get(IhtDeclaracao_.nre), nre), 
                                cb.equal(rt.get(IhtDeclaracao_.id), sbMaxID),
                                cb.equal(rt.get(IhtDeclaracao_.estado), 'A')};
                                    
        
        cq.where(condicao);
        javax.persistence.Query q = getEntityManager().createQuery(cq);

        try {
            return  (IhtDeclaracao) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        
    }
    
    
    
}
