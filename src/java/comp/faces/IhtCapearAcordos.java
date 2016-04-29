/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.faces;


import comp.file.DocxExporterIHTCapear;
import comp.faces.util.JsfUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import comp.emigrados.Cadastros;
import comp.emigrados.ParEd;
import comp.session.GlFicheiroFacade;

/**
 *
 * @author bnf02107
 */
@ManagedBean
@SessionScoped
public class IhtCapearAcordos {
    
    private Cadastros cadastros;
    private ParEd parEd;

    @EJB private GlFicheiroFacade glFicheiroFacade;

    private CapearAcordos selectCapearAcordos;    
    private List<CapearAcordos> capearAcordos = new ArrayList<CapearAcordos>() ; 
    
    private CapearAcordosAgupados selectCapearAcordosAgupados;     
    private List<CapearAcordosAgupados> capearAcordosAgupados = new ArrayList<CapearAcordosAgupados>() ; 

    public IhtCapearAcordos() {
    }

    public String doSearch(){
//        return null;
    return Create();
    }

    public Cadastros getCadastros() {
        return cadastros;
    }

    public void setCadastros(Cadastros cadastros) {
        this.cadastros = cadastros;
    }
    
    
    public List<CapearAcordos> getCapearAcordos() {
        return capearAcordos;
    }

    public void setCapearAcordos(List<CapearAcordos> capearAcordos) {
        this.capearAcordos = capearAcordos;
    }

    public CapearAcordos getSelectCapearAcordos() {
        return selectCapearAcordos;
    }

    public void setSelectCapearAcordos(CapearAcordos selectCapearAcordos) {
        this.selectCapearAcordos = selectCapearAcordos;
    }

    public List<CapearAcordosAgupados> getCapearAcordosAgupados() {
        return capearAcordosAgupados;
    }

    public void setCapearAcordosAgupados(List<CapearAcordosAgupados> capearAcordosAgupados) {
        this.capearAcordosAgupados = capearAcordosAgupados;
    }

    public CapearAcordosAgupados getSelectCapearAcordosAgupados() {
        return selectCapearAcordosAgupados;
    }

    public void setSelectCapearAcordosAgupados(CapearAcordosAgupados selectCapearAcordosAgupados) {
        this.selectCapearAcordosAgupados = selectCapearAcordosAgupados;
    }
    
    public String Create(){

            try{
            if (cadastros.getInfoGeral().getQacEstabelecimentos()!=null){
                if (cadastros.getInfoGeral().getQacEstabelecimentos().getQacEstabParEd()!=null)                    
                parEd = cadastros.getInfoGeral().getQacEstabelecimentos().getQacEstabParEd().getPar_ed();    
            }                
            } catch(Exception e ){                
            }

            capearAcordos.add(new CapearAcordos(parEd, cadastros));
          
            return null;
//        }
    }
    
    public String Destroy(){
        capearAcordos.remove(selectCapearAcordos);
        return null;
    }
    
    public String prepareComunicação(){        
        ConverterCapearAcordos();        
        return "ListC";
    }
    
    private void SortCapearAcordos(){
             Collections.sort (capearAcordos, new Comparator() {  
            public int compare(Object o1, Object o2) {  
                CapearAcordos p1 = (CapearAcordos) o1;  
                CapearAcordos p2 = (CapearAcordos) o2;  
                return p1.getParEd().getCdEd() < p2.parEd.getCdEd() ? -1 : (p1.getParEd().getCdEd() > p2.parEd.getCdEd() ? +1 : 0);  
            }  
        });         
    }
    
    

    private void ConverterCapearAcordos(){        
        SortCapearAcordos();  
        
        capearAcordosAgupados = new ArrayList<CapearAcordosAgupados>() ; 
        List<Cadastros> cadastroses = new  ArrayList<Cadastros>();
        parEd = null;
        
        Iterator<CapearAcordos> it = capearAcordos.iterator();
        while (it.hasNext()) {
            CapearAcordos capearAcordos1 = it.next();
           if (parEd== null){
               cadastroses.add(capearAcordos1.getCadastros());
           } else if(capearAcordos1.getParEd().getCdEd().equals(parEd.getCdEd())){ 
              cadastroses.add(capearAcordos1.getCadastros());
           } else {
             capearAcordosAgupados.add(new CapearAcordosAgupados(parEd, null,cadastroses.toArray( new Cadastros[]{})));
             cadastroses.removeAll(cadastroses);
             cadastroses.add(capearAcordos1.getCadastros());
           }  
           parEd = capearAcordos1.parEd;
        }
        
        if(!it.hasNext())
            capearAcordosAgupados.add(new CapearAcordosAgupados(parEd, null,cadastroses.toArray( new Cadastros[]{})));        
    }
    

    public String  performExport2Docx(){        
     DocxExporterIHTCapear docxExporterIHTCapear = new DocxExporterIHTCapear();    
       HttpServletResponse httpServletResponse  = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       ;
       
       try{
           baos = docxExporterIHTCapear.BAOSDocx(selectCapearAcordosAgupados, glFicheiroFacade.find(4).getFicheiro());
         } catch (Exception e) {
             JsfUtil.addErrorMessage(e, e.getMessage());
         }
       
       try {                   
              JsfUtil.writeFileToResponse(httpServletResponse, baos  ,"CapearAcordo.docx");
        } catch (IOException ex) {
            JsfUtil.addErrorMessage("Ocorreu um erro no processo de Exportar o ficheiro");
        }
        return null;
    }
    

    public class CapearAcordosAgupados{
        
        private ParEd parEd;
        private String Refencia;
        private Cadastros[] cadastros;

        public CapearAcordosAgupados(ParEd parEd, String Refencia, Cadastros[] cadastros) {
            this.parEd = parEd;
            this.Refencia = Refencia;
            this.cadastros = cadastros;
        }

        public String getRefencia() {
            return Refencia;
        }

        public void setRefencia(String Refencia) {
            this.Refencia = Refencia;
        }

        public Cadastros[] getCadastros() {
            return cadastros;
        }

        public void setCadastros(Cadastros[] cadastros) {
            this.cadastros = cadastros;
        }

        public ParEd getParEd() {
            return parEd;
        }

        public void setParEd(ParEd parEd) {
            this.parEd = parEd;
        }
        
        
        
    }

    public class CapearAcordos{

        private ParEd parEd;
        private Cadastros cadastros;


        public CapearAcordos() {
        }
        
         public CapearAcordos(ParEd parEd, Cadastros cadastros) {
            this.cadastros = cadastros;
            this.parEd = parEd;
        }
        
        public Cadastros getCadastros() {
            return cadastros;
        }

        public void setCadastros(Cadastros cadastros) {
            this.cadastros = cadastros;
        }

        public ParEd getParEd() {
            return parEd;
        }

        public void setParEd(ParEd parEd) {
            this.parEd = parEd;
        }
    
    }
    
}

