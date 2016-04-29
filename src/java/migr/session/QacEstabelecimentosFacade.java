/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

/**
 *
 * @author bnf02107
 */
@Stateless
public class QacEstabelecimentosFacade extends AbstractFacade<QacEstabelecimentos> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public QacEstabelecimentosFacade() {
        super(QacEstabelecimentos.class);
    }
    
         public QacEstabelecimentos findByEmpEstab(String Empresa, String CdEstab){
        
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root rt = cq.from(entityClass);
        Predicate[] condicao = {cb.like(rt.get(QacEstabelecimentos_.qacEstabelecimentosPK).get(QacEstabelecimentosPK_.siglaE), Empresa),
                                cb.like(rt.get(QacEstabelecimentos_.qacEstabelecimentosPK).get(QacEstabelecimentosPK_.cdEstab), CdEstab)};
        cq.where(condicao);
        javax.persistence.Query q = getEntityManager().createQuery(cq);

        return (QacEstabelecimentos) q.getSingleResult();
        
    }
    
    
}
