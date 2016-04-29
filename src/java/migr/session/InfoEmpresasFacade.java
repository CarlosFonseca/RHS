/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migr.session;

import comp.emigrados.*;
import comp.entities.*;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.primefaces.model.SortOrder;

/**
 *
 * @author bnf02107
 */
@Stateless
public class InfoEmpresasFacade extends AbstractFacade<InfoEmpresas> {
    @PersistenceContext(unitName = "RHSPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public InfoEmpresasFacade() {
        super(InfoEmpresas.class);
    }
    
        public List<InfoEmpresas> findRange(int[] range, String sortField, SortOrder sortOder, Map filters) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root root = cq.from(entityClass);
   
        int incialpredicate = 2;
         javax.persistence.criteria.Predicate[] predicateFixo = new Predicate[incialpredicate]  ;       
         predicateFixo[0] = cb.like(root.get(InfoEmpresas_.estado), "A");
         predicateFixo[1] = cb.like(root.get(InfoEmpresas_.cadastros).get(Cadastros_.cdSituacao).get(Situacoes_.projectos), "S");
         
         
//         Predicate condicaoFixa = cb.and(predicateFixo);
         
//         Predicate condicaoDinamica = addChoicePredicate(new Predicate[filters.size()], 0, filters, cb, root);
     
         Predicate condicao = null;
         if (!filters.isEmpty() 
           || (filters.containsKey("globalFilter") && "".equals(filters.get("globalFilter").toString()) && filters.containsValue("%")) ){
             condicao = makePredicate(predicateFixo, filters, cb, root);
         }             
         
        cq.select(root);

        Order[] ordenar = {cb.asc(root.get(InfoEmpresas_.infoEmpresasPK).get(InfoEmpresasPK_.siglaE)),
                           cb.asc(root.get(InfoEmpresas_.qacEstruturas).get(QacEstruturas_.nrOrdem)),
                           cb.asc(root.get(InfoEmpresas_.qacEstruturas).get(QacEstruturas_.qacEstruturasPK).get(QacEstruturasPK_.cdEstrutura)), 
                           cb.asc(root.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoGeral, JoinType.LEFT).get(InfoGeral_.cdGrupo)),
                           cb.asc(root.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).join(InfoProfs_.cdFuncInt, JoinType.LEFT ).get(FuncoesInternas_.preExperiencia)),                           
                           cb.desc(root.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).join(InfoProfs_.cdFuncInt, JoinType.LEFT ).get(FuncoesInternas_.cdFuncInt)),                           
                           cb.desc(root.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoGeral, JoinType.LEFT ).get(InfoGeral_.cdNivel))
                            };     
        
       makeOrder(ordenar, sortField, sortOder, cb, root);
       
       if (condicao!=null){
            return findCriteria(range, condicao, ordenar);    
       }else{   
            return findCriteria(range, predicateFixo, ordenar);
       }
        
       
   }
    
    
    
       public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root rt = cq.from(entityClass);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
       Predicate[] condicao = {cb.like(rt.get(InfoEmpresas_.estado), "A"), 
                                cb.like(rt.get(InfoEmpresas_.cadastros).get(Cadastros_.cdSituacao).get(Situacoes_.projectos), "S")};
         cq.where(condicao);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
     
   public List<Tuple> findAoServico(int[] range, String sortField, SortOrder sortOder, Map filters, String linha, String  tipo_dscomp){    
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root rt = cq.from(entityClass);

        Path <RemuneracoesEmpregado> rem = rt.join(InfoEmpresas_.cadastros).join(Cadastros_.remuneracoesEmpregadoCollection, JoinType.LEFT);        
        Path       <QacIhtTipo> qIHTtipo = rt.join(InfoEmpresas_.cadastros).join(Cadastros_.ihtDeclaracaoCollection, JoinType.LEFT).join(IhtDeclaracao_.tipoId, JoinType.LEFT);
        
        int incialpredicate = 6;
        
         Predicate[] predicateFixo = new Predicate[incialpredicate]  ;       
         predicateFixo[0] = cb.like(rt.get(InfoEmpresas_.estado), "A");
         predicateFixo[1] = cb.like(rt.get(InfoEmpresas_.cadastros).get(Cadastros_.cdSituacao).get(Situacoes_.projectos), "S");
         predicateFixo[2] = cb.and(cb.or(cb.equal(rem.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.cdEnquadra) , 18),
                                     cb.equal(rem.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.cdEnquadra) , 4),
                                     cb.isNull(rem.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.cdEnquadra)) ),
                               cb.or(cb.like(rem.get(RemuneracoesEmpregado_.status) , "A"),
                                     cb.isNull(rem.get(RemuneracoesEmpregado_.status)) ));
         predicateFixo[3] = cb.or(cb.like(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT).join(Cadastros_.ihtDeclaracaoCollection, JoinType.LEFT).get(IhtDeclaracao_.estado), "A"),
                              cb.isNull(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT).join(Cadastros_.ihtDeclaracaoCollection, JoinType.LEFT).get(IhtDeclaracao_.estado)) );
          
        if (!linha.equals("")) {
             predicateFixo[4] =  cb.like(rem.get(RemuneracoesEmpregado_.linhasEnquadramento).get(LinhasEnquadramento_.dsLinha),linha);   
         } else{
           predicateFixo[4] = cb.like(rt.get(InfoEmpresas_.estado), "A");
         }
         
         if(tipo_dscomp.equals("null") || tipo_dscomp.equals("null") || tipo_dscomp.equals("NULL"))
            predicateFixo[5] = cb.isNull(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT).join(Cadastros_.ihtDeclaracaoCollection, JoinType.LEFT).join(IhtDeclaracao_.tipoId, JoinType.LEFT).get(QacIhtTipo_.dsComp));
         else if (!tipo_dscomp.equals(""))
            predicateFixo[5] = cb.like(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT).join(Cadastros_.ihtDeclaracaoCollection, JoinType.LEFT).join(IhtDeclaracao_.tipoId, JoinType.LEFT).get(QacIhtTipo_.dsComp), tipo_dscomp);
            
         else
            predicateFixo[5] = cb.like(rt.get(InfoEmpresas_.estado), "A");                   

//        Predicate condicaoFixa = cb.and(predicateFixo);         
//         Predicate condicaoDinamica = addChoicePredicate(new Predicate[filters.size()], 0, filters, cb, rt);
         
         Predicate condicao = null;
         if (!filters.isEmpty() 
           || (filters.containsKey("globalFilter") && "".equals(filters.get("globalFilter").toString()) && filters.containsValue("%")) ){
             condicao = makePredicate(predicateFixo, filters, cb, rt);
         }             
         
        Order[] ordenar = {cb.desc( rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).get(InfoProfs_.dtFuncao))
                            };

         makeOrder(ordenar, sortField, sortOder, cb, rt);
 
        CompoundSelection<Tuple> compoundSelection = cb.tuple(rt.get(InfoEmpresas_.infoEmpresasPK).get(InfoEmpresasPK_.siglaE).alias("siglaE"),
                                                              rt.join(InfoEmpresas_.cadastros, JoinType.LEFT).get(Cadastros_.nre).alias("nre"),
                                                              rt.join(InfoEmpresas_.cadastros, JoinType.LEFT).get(Cadastros_.nomeComp).alias("nomeComp"),
                                                              rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).join(InfoProfs_.cdFuncao, JoinType.LEFT ).get(FuncoesOficiais_.dsComp).alias("Categoria") ,
                                                              rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).get(InfoProfs_.dtFuncao).alias("dtCategoria") ,                                                              
                                                              rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).join(InfoProfs_.cdFuncInt, JoinType.LEFT ).get(FuncoesInternas_.dsComp).alias("Funcao") ,
                                                              rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).get(InfoProfs_.dtFuncaoInt).alias("dtFuncao") ,
                                                              rem.get(RemuneracoesEmpregado_.linhasEnquadramento).get(LinhasEnquadramento_.dsLinha).alias("cdLinha"),
                                                              rem.get(RemuneracoesEmpregado_.remuneracoesEmpregadoPK).get(RemuneracoesEmpregadoPK_.dtCriacao).alias("dtLinha"),
                                                              qIHTtipo.get(QacIhtTipo_.dsComp).alias("Acordo") );
  
         if (condicao!=null){
            return findCriteria(range, compoundSelection, condicao, ordenar, true);    
       }else{   
            return findCriteria(range,compoundSelection, predicateFixo, ordenar, true);
       }

        
//       return findCriteria(range, compoundSelection, condicaoFixa, condicaoDinamica, ordenar, true);
   
    }
   

   
   
    
    
    //-------------------------------------------------------------------------
   
     private List doFindBy(CriteriaBuilder cb, Root rt,Predicate[] condicao){
   
        
        Order[] ordenar = {cb.asc(rt.get(InfoEmpresas_.qacEstruturas).get(QacEstruturas_.nrOrdem)),
                           cb.asc(rt.get(InfoEmpresas_.qacEstruturas).get(QacEstruturas_.qacEstruturasPK).get(QacEstruturasPK_.cdEstrutura)), 
                           cb.asc(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoGeral, JoinType.LEFT).get(InfoGeral_.cdGrupo)),
                           cb.asc(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).join(InfoProfs_.cdFuncInt, JoinType.LEFT ).get(FuncoesInternas_.preExperiencia)),                           
                           cb.desc(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoProfs, JoinType.LEFT ).join(InfoProfs_.cdFuncInt, JoinType.LEFT ).get(FuncoesInternas_.cdFuncInt)),                           
                           cb.desc(rt.join(InfoEmpresas_.cadastros, JoinType.LEFT ).join(Cadastros_.infoGeral, JoinType.LEFT ).get(InfoGeral_.cdNivel))
                            };

        return findCriteria(null,condicao,ordenar);       
    }


  


    
    
}
