/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.session;

import comp.entities.QacIhtAlineaTexto;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author bnf02107
 */
@Stateless
public class QacIhtAlineaTextoFacade extends AbstractFacade<QacIhtAlineaTexto> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public QacIhtAlineaTextoFacade() {
        super(QacIhtAlineaTexto.class);
    }
    
}
