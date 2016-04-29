/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.session;

import comp.entities.GlTipoEmpresa;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import migr.session.AbstractFacade;

/**
 *
 * @author bnf02107
 */
@Stateless
public class GlTipoEmpresaFacade extends AbstractFacade<GlTipoEmpresa> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public GlTipoEmpresaFacade() {
        super(GlTipoEmpresa.class);
    }
    
}
