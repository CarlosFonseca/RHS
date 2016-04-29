/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.session;

import comp.entities.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import migr.session.AbstractFacade;

/**
 *
 * @author bnf02107
 */
@Stateless
public class GlEstabelecimentoFacade extends AbstractFacade<GlEstabelecimento> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public GlEstabelecimentoFacade() {
        super(GlEstabelecimento.class);
    }
    
}
