/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.QacCodigosPostais;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author bnf02107
 */
@Stateless
public class QacCodigosPostaisFacade extends AbstractFacade<QacCodigosPostais> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public QacCodigosPostaisFacade() {
        super(QacCodigosPostais.class);
    }
    
}
