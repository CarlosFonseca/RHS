/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.file;

import comp.faces.IhtCapearAcordos.CapearAcordosAgupados;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import comp.emigrados.Cadastros;

/**
 *
 * @author bnf02107
 */

public class DocxExporterIHTCapear extends  AbstractDocx {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
        
    public ByteArrayOutputStream BAOSDocx (CapearAcordosAgupados capearAcordosAgupados,byte[] sourceByteFile){


        HashMap<String, String> mappings = new HashMap<String, String>(); 
        String afterText ="De acordo com preceituado no nº.3 do artigo 218º. do Código de Trabalho, junto remetemos os acordos escritos de isenção de horário de trabalho, referentes ao(s) seguinte(s) empregado(s):";
        List<P> ps = new ArrayList<P>();
        
        WordprocessingMLPackage wordMLPackage = null;
//        String sourcePathFile = "c:/docx/iht/capearAcordo.docx";        
        String destinationPathFile = "capearAcordo.docx";
        File file = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        String fileName = "CapearAcordo";
            
        mappings.put("DS_REDZ", capearAcordosAgupados.getParEd().getDsRedz());
        mappings.put("MORADA", capearAcordosAgupados.getParEd().getMorada());
        if(capearAcordosAgupados.getParEd().getQacCodigosPostais()!= null){
            mappings.put("CD_POSTAL", capearAcordosAgupados.getParEd().getQacCodigosPostais().getQacCodigosPostaisPK().getCdPostal());
            mappings.put("NR_ORDEM", capearAcordosAgupados.getParEd().getQacCodigosPostais().getSubCodPostal());
            mappings.put("DS_AREA", capearAcordosAgupados.getParEd().getQacCodigosPostais().getDsArea());             
        } else {
            mappings.put("CD_POSTAL", "");
            mappings.put("NR_ORDEM", "");
            mappings.put("DS_AREA", "");                    
        }
        mappings.put("REFERENCIA", capearAcordosAgupados.getRefencia());
        
        for (Cadastros cadastros : capearAcordosAgupados.getCadastros()) { 
            ps.add(addParagraph("   - " + cadastros.getNomeComp()));
            ps.add(addParagraph(""));
        }
        
         wordMLPackage = loadFile(wordMLPackage, sourceByteFile);
    //   wordMLPackage = loadFile(wordMLPackage, sourcePathFile);            
       insertParagraph(wordMLPackage, afterText, ps);
       processaWordprocessingMLPackage(wordMLPackage, mappings);                       
       baos = wordToStream(wordMLPackage, baos); 
       
       return baos;

    }
    
}
