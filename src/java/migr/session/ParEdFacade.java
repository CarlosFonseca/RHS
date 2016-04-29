/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.ParEd;
import comp.emigrados.*;
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
public class ParEdFacade extends AbstractFacade<ParEd> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public ParEdFacade() {
        super(ParEd.class);
    }
    
        public List<ParEd> findIDCTList(){
        
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root rt = cq.from(entityClass);
        Predicate[] condicao = {cb.like(rt.get(ParEd_.cdGrupoEd).get(GruposEd_.cdGrupo), "IT")}; // IT SIGLA DO Grupo IDCT 
        cq.where(condicao);
        javax.persistence.Query q = getEntityManager().createQuery(cq);

        return  q.getResultList();
        
    }
    
}
