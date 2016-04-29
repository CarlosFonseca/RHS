/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.RemuneracoesEmpregado;
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
public class RemuneracoesEmpregadoFacade extends AbstractFacade<RemuneracoesEmpregado> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public RemuneracoesEmpregadoFacade() {
        super(RemuneracoesEmpregado.class);
    }
    
    public RemuneracoesEmpregado findIHTByNre(int nre){
        
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root rt = cq.from(entityClass);
        Predicate[] condicao = {cb.like(rt.get(RemuneracoesEmpregado_.status), "A"),
                                cb.isNull(rt.get(RemuneracoesEmpregado_.dtInactivo)),
                                cb.or(cb.equal(rt.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.cdEnquadra), 8),
                                      cb.equal(rt.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.cdEnquadra), 4)),
                                cb.equal(rt.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.cadNre), nre)};
        cq.where(condicao);
        javax.persistence.Query q = getEntityManager().createQuery(cq);

            try {
                return (RemuneracoesEmpregado) q.getSingleResult();
            } catch (Exception e) {
                return null;
            }
    } 
    
    
}
