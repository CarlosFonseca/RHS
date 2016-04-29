/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.docx4j.TextUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.LoadFromZipNG;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;
import org.docx4j.wml.P;

/**
 *
 * @author bnf02107
 */
public abstract class AbstractDocx {

    
        protected  WordprocessingMLPackage loadFile(WordprocessingMLPackage wordMLPackage, byte[] sourcePathFile ){
            
            InputStream inputStream = new ByteArrayInputStream(sourcePathFile);

            LoadFromZipNG loader = new LoadFromZipNG();
        try {
            wordMLPackage = (WordprocessingMLPackage)loader.get(inputStream);
        } catch (Docx4JException ex) {
            Logger.getLogger(AbstractDocx.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wordMLPackage;
        
    }  
    
    
    
    protected  WordprocessingMLPackage loadFile(WordprocessingMLPackage wordMLPackage, String sourcePathFile ){
        try {
            wordMLPackage = WordprocessingMLPackage.load(new File(sourcePathFile));
        } catch (Docx4JException ex) {
            Logger.getLogger(AbstractDocx.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wordMLPackage;
        
    }   
    
    protected P addParagraph(String value){
          org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();      
          org.docx4j.wml.P para = factory.createP();     
          org.docx4j.wml.Text t = factory.createText();
          t.setValue(value);
          org.docx4j.wml.R run = factory.createR();
          run.getContent().add(t);
          para.getContent().add(run);
          return para;        
    }
    
    
    protected void insertParagraph(WordprocessingMLPackage pkg, String afterText, List<P> ps){
        Body b = pkg.getMainDocumentPart().getJaxbElement().getBody();
        int addPoint = -1, count = 0;
        for (Object o : b.getContent()) {
            if (o instanceof P && getElementText(o).startsWith(afterText)) {
                addPoint = count + 1;
                break;
             }
             count++;
            }
            if (addPoint != -1){
                Iterator<P> it = ps.iterator();
                while (it.hasNext()) {
                    P paragraph = it.next();
                    b.getContent().add(addPoint, paragraph);                    
                }

            }else {
                // didn't find paragraph to insert after...
            }
        }

       String getElementText(Object jaxbElem){
           StringWriter sw = new StringWriter();
           try {
               TextUtils.extractText(jaxbElem, sw);
           } catch (Exception exception) {
           }
            return sw.toString();
        }
        
    
      protected void processaWordprocessingMLPackage(WordprocessingMLPackage wordMLPackage, HashMap<String, String> mappings) {
       
        try {                
            MainDocumentPart wordDocumentPart = wordMLPackage.getMainDocumentPart();
            org.docx4j.wml.Document wmlDocument = wordDocumentPart.getJaxbElement();
            String xml = XmlUtils.marshaltoString(wmlDocument, true);
            Object obj = XmlUtils.unmarshallFromTemplate(xml, mappings);  
            wordDocumentPart.setJaxbElement((Document) obj);        
            wordMLPackage.addTargetPart(wordDocumentPart);
           
       } catch (Docx4JException ex) {
               Logger.getLogger(AbstractDocx.class.getName()).log(Level.SEVERE, null, ex);
       }  catch (Exception e) {
               Logger.getLogger(AbstractDocx.class.getName()).log(Level.SEVERE, null, e);
       }
   
    }  
      
       protected ByteArrayOutputStream wordToStream(WordprocessingMLPackage wordMLPackage, ByteArrayOutputStream baos){
                
        SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
        
        try {
            saver.save(baos);
        } catch (Docx4JException ex) {
            Logger.getLogger(AbstractDocx.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         return baos;       
    }
      
      
    protected Object isNull(Object o){  
        
        if (o == null || o == "")
            return "";
        else
            return o + "";  
    }
    
    
    
}
