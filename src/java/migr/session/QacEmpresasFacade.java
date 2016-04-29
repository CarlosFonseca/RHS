/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.QacEmpresas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author bnf02107
 */
@Stateless
public class QacEmpresasFacade extends AbstractFacade<QacEmpresas> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public QacEmpresasFacade() {
        super(QacEmpresas.class);
    }
    
}
