/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import org.primefaces.model.SortOrder;

/**
 *
 * @author bnf02107
 */
public abstract class AbstractFacade<T> {
    protected Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
//------------------------------------------------------------------------------------------------     
        
    public List<T> findCriteria(int[] range,Predicate condicoes , Order[] Ordencao ) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        cq.where(condicoes); 
        if(Ordencao!=null) {
            cq.orderBy(Ordencao);
        }

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        if (range != null ){
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
        }
        return q.getResultList();
    } 

    
    public List<T> findCriteria(int[] range,Predicate[] condicoes , Order[] Ordencao ) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        cq.where(condicoes); 
        if(Ordencao!=null) {
            cq.orderBy(Ordencao);
        } else {
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem")), cb.asc(cq.from(entityClass).get("id"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
        }    
            

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        if (range != null ){
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
        }
        return q.getResultList();
    } 
 
    
    public List<Tuple> findCriteria(int[] range, CompoundSelection<Tuple> compoundSelection, Predicate[] condicao, Order[] Ordencao, boolean destintas ) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        cq.select(compoundSelection);
        cq.distinct(destintas);
        cq.where(condicao); 
        if(Ordencao!=null) {
            cq.orderBy(Ordencao);
        } else {
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem")), cb.asc(cq.from(entityClass).get("id"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
        }    

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        if (range != null ){
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
        }
        return q.getResultList();
    } 

        public List<Tuple> findCriteria(int[] range, CompoundSelection<Tuple> compoundSelection, Predicate condicao, Order[] Ordencao, boolean destintas ) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        cq.select(compoundSelection);
        cq.distinct(destintas);
        cq.where(condicao); 
        if(Ordencao!=null) {
            cq.orderBy(Ordencao);
        } else {
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem")), cb.asc(cq.from(entityClass).get("id"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
        }    

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        if (range != null ){
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
        }
        return q.getResultList();
    } 

    public List<Tuple> findCriteria(int[] range, CompoundSelection<Tuple> compoundSelection, Predicate[] condicao, Order[] Ordencao, Expression[] compoundGroup) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        cq.select(compoundSelection);
        cq.where(condicao); 
        cq.groupBy(compoundGroup);
        if(Ordencao!=null) {
            cq.orderBy(Ordencao);
        } else {
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
            try {
               Order[] ordenar = {cb.asc(cq.from(entityClass).get("ordem")), cb.asc(cq.from(entityClass).get("id"))};
               cq.orderBy(ordenar);
           } catch (Exception e) {
            }
        }    

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        if (range != null ){
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
        }
        return q.getResultList();
    } 
    
       
   //------------------------------------------------------------------------------------------------     
       

    
   public List<T> findRange(int[] range, String sortField, SortOrder sortOder, Map filters) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root root = cq.from(entityClass);
        Predicate[] predicate = new Predicate[filters.size()];
        if (!filters.isEmpty() 
            || (filters.containsKey("globalFilter") && "".equals(filters.get("globalFilter").toString()) && filters.containsValue("%")) ){
                cq.where(addChoicePredicate(predicate, filters, cb, root));
        }
        cq.select(root);

        Expression expression = null ;       
        if (sortField != null){
            expression = makeExpression(sortField, root); 
           if (sortOder == SortOrder.ASCENDING) {
                cq.orderBy(cb.asc(expression));
           }else {
                cq.orderBy(cb.desc(expression));
           }
        }
        
        // apanha o erro dos filtros e devolve sem filtro para não estoirar
        try {
            javax.persistence.Query q = getEntityManager().createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
       } catch (Exception e) {
       } finally{            
            cq.where();
       }
        
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
   
   
     protected Predicate makePredicate(Predicate[] predicateFixo, Map filters, javax.persistence.criteria.CriteriaBuilder cb, javax.persistence.criteria.Root rt ){
        Predicate[] predicate = new Predicate[filters.size()];
        Predicate condicaoFixa = cb.and(cb.and(predicateFixo),addChoicePredicate(predicate, filters, cb, rt)) ;
      return condicaoFixa;
  }

  
    protected Predicate addChoicePredicate (Predicate[] predicate, Map filters, javax.persistence.criteria.CriteriaBuilder cb, javax.persistence.criteria.Root rt ){
        List<Predicate> predicates = new ArrayList<Predicate>();        
        if ( filters.containsKey("globalFilter") && !"".equals(filters.get("globalFilter").toString()) ) {
                String valor = filters.get("globalFilter").toString();
                filters.remove("globalFilter");
                addFilterPredicate(predicates, valor, filters, cb, rt );
                predicate = new Predicate[predicates.size()];
                predicates.toArray(predicate);
                return cb.or(predicate);                
        }else{
            if ( filters.containsKey("globalFilter")){
                 filters.remove("globalFilter");
            }     
            addPredicate(predicates, filters, cb, rt );
            predicate = new Predicate[predicates.size()];
            predicates.toArray(predicate);
            return cb.and(predicate);                
        }
   }

   protected void addFilterPredicate (List<Predicate> predicates,String valor, Map filters, javax.persistence.criteria.CriteriaBuilder cb, javax.persistence.criteria.Root rt ){     
        java.util.Iterator it = filters.entrySet().iterator();        
        Expression expression = null ; 
        while (it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
//                System.out.println("");
//                System.out.println("aaaaaaa");
//                System.out.println(pairs.getKey().toString() + " aqui " + valor);
            if (pairs.getKey()!= null){
                expression = makeExpression(pairs.getKey().toString(), rt);
 //               System.out.println(expression.getJavaType().getCanonicalName());
                if (!valor.matches("\\d*")) {
                    if (expression.getJavaType().getCanonicalName().equals("java.lang.String")){
                        predicates.add(cb.like(cb.lower(expression), "%" + valor.toLowerCase()+"%"));
                    }
                } else{    
                    if (expression.getJavaType().getCanonicalName().equals("java.lang.String")){
                        predicates.add(cb.like(cb.lower(expression), "%" + valor.toLowerCase()+"%")); 
                    } else if (expression.getJavaType().getCanonicalName().equals("char")) {
                        predicates.add(cb.like(cb.lower(expression), "X")); 
                    } else if (expression.getJavaType().getCanonicalName().equals("java.util.Date")){
                        predicates.add(cb.like(cb.lower(expression), "%" + valor+"%")); 
                    } else if (valor.matches("-?\\d+(.\\d+)?")) {                        
                        predicates.add(cb.equal(expression, valor));                  
                    } else {                        
                        predicates.add(cb.equal(expression, cb.literal(9999999)));                    
                    }
                }
           }
       }           
  }         
         
  protected void addPredicate (List<Predicate> predicates,  Map filters, javax.persistence.criteria.CriteriaBuilder cb, javax.persistence.criteria.Root rt ){     
      
        java.util.Iterator it = filters.entrySet().iterator();        
        Expression expression = null ; 
        while (it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
//                System.out.println();             
//                System.out.println("aaaaaaa");
//                System.out.println(pairs.getKey().toString() + " aqui " + pairs.getValue().toString().substring(0, 1));
            if (pairs.getKey()!= null){
                expression = makeExpression(pairs.getKey().toString(), rt);
                String value = pairs.getValue().toString();               
//                System.out.println(expression.getJavaType().getCanonicalName());
                if (expression.getJavaType().getCanonicalName().equals("java.lang.String")){
                    if (value.toLowerCase().equals("isnull")) {
                        predicates.add(cb.isNull(expression));
                    } else {
                        predicates.add(cb.like(cb.lower(expression),  value.toLowerCase()));
                    } 
                } else if (expression.getJavaType().getCanonicalName().equals("char")) {
                    predicates.add(cb.like(cb.lower(expression), value.toLowerCase())); 
                } 
                 else if (expression.getJavaType().getCanonicalName().equals("java.util.Date")){
                     if (value.matches("\\d{1,2}/\\d{1,2}/\\d{4}")){
                         predicates.add(cb.equal(expression, value.substring(6, 10)+"-"+value.substring(3, 5)+"-"+value.substring(0, 2)));                            
                    }else if (value.matches("%/\\d{1,2}/\\d{4}") || value.matches("\\d{1,2}/\\d{4}")){
                         Calendar calendar = Calendar.getInstance();
                         String ano = value.replace("%/", "").substring(3, 7);
                         String mes = value.replace("%/", "").substring(0, 2);
                         calendar.set(Integer.parseInt(ano),Integer.parseInt(mes)-1, 1);
                         int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                         predicates.add(cb.between(expression, ano+"-"+mes+"-01", ano+"-"+mes+"-"+maxDay));                         
                    }else if (value.matches("%/\\d{4}") || value.matches("\\d{4}")){
                           String ano = value.replace("%/", "");
                         predicates.add(cb.between(expression, ano+"-01-01", ano+"-12-31"));                         
                    }
                } else if (value.matches("-?\\d+(.\\d+)?")) {
                    predicates.add(cb.equal(expression, value));
                } else if (value.substring(0, 1).equals("<") && value.substring(1).matches("-?\\d+(.\\d+)?")) {
                    predicates.add(cb.lessThan(expression, value.substring(1)));
                } else if (value.substring(0, 1).equals(">") && value.substring(1).matches("-?\\d+(.\\d+)?")) {
                    predicates.add(cb.greaterThan(expression, value.substring(1)));
                } else if (value.substring(0, 2).equals("<>") && value.substring(2).matches("-?\\d+(.\\d+)?")) {
                    predicates.add(cb.notEqual(expression, value.substring(2)));
                }
           }
       }           
  }         
  
    protected void makeOrder (Order[] ordenar, String sortField, SortOrder sortOder, javax.persistence.criteria.CriteriaBuilder cb, javax.persistence.criteria.Root rt ){     
        Expression expression = null ; 
        if (sortField != null){
            expression = makeExpression(sortField, rt); 
            ordenar = new Order[1];
            if (sortOder == SortOrder.ASCENDING) {
               ordenar[0] = cb.asc(expression);
            } else {
                ordenar[0] = cb.desc(expression);
            }
        } 
  }
      
      
  protected Expression makeExpression (String sortField, javax.persistence.criteria.Root rt ) {
      Expression expression = null ;
      
      String s[] = sortField.split("\\.");
      if (s[s.length-1].equals("toString()")) s[s.length-1]= "dsRedz";

      switch(s.length){
          case 1: expression =rt.get(s[0]); break;
          case 2: expression =rt.join(s[0]).get(s[1]); break;
          case 3: expression =rt.join(s[0]).join(s[1]).get(s[2]); break;
          case 4: expression =rt.join(s[0]).join(s[1]).join(s[2]).get(s[3]); break;     
          case 5: expression =rt.join(s[0]).join(s[1]).join(s[2]).join(s[3]).get(s[4]); break;
          case 6: expression =rt.join(s[0]).join(s[1]).join(s[2]).join(s[3]).join(s[4]).get(s[5]); break;
          case 7: expression =rt.join(s[0]).join(s[1]).join(s[2]).join(s[3]).join(s[4]).join(s[5]).get(s[6]);  break;    
      }
      
      return expression;      
  }
    
    
}
