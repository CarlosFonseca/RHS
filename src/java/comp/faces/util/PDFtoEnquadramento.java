/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.faces.util;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import comp.entities.EnqFuncao;
import comp.entities.EnqFuncaoCompetencia;
import comp.entities.EnqFuncaoEstEmquadramento;
import comp.entities.EnqFuncaoRequisitos;
import comp.entities.EnqSubbandaCompetencia;
import comp.entities.QacEnqTipoCompetencia;
import comp.view.EnqFuncRequisitoAll;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author bnf02107
 */
public class PDFtoEnquadramento {
    
        private Font fonteTitulo = new Font(Font.HELVETICA, 13, Font.BOLD);
        private Font fonteEspaco = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);
        private Font fonteSbuTitulo = new Font(Font.HELVETICA, 8, Font.BOLD);
        private Font fontHead = new Font(Font.HELVETICA, 9);
        private Font font = new Font(Font.HELVETICA, 8);
        Image img = null;
    
        public String exportaList(FacesContext facesContext,  String filename, List<EnqFuncao> listEnqFuncao ) throws IOException {
        
                  HttpServletRequest request = (HttpServletRequest)facesContext.getCurrentInstance().getExternalContext(). getRequest();

 //               ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
 //               String logo = servletContext.getRealPath("") + File.separator + "resources/images" + File.separator + "banif_gb_rgb_positivo_hor.jpg";  
                  String logo = "http://"+ request.getServerName()+ ":"+ request.getServerPort() + request.getContextPath() +  "/resources/css/images/banif_gb_rgb_positivo_hor.jpg";     
                System.out.println("aqui");
                    
                System.out.println(logo);
                    
                try {
                img = Image.getInstance(new URL(logo));
            } catch (BadElementException ex) {
                Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                Document document = new Document(PageSize.A4);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, baos);
                
                HeaderFooter event = new HeaderFooter();
                
                writer.setBoxSize("left", new Rectangle(36, 54, 40, 788));  
                writer.setBoxSize("center", new Rectangle(36, 54, 550, 788));
                writer.setBoxSize("rigth", new Rectangle(36, 54, 1070, 788));
                
                
                writer.setPageEvent(event);

                document.open();

                Iterator<EnqFuncao> it = listEnqFuncao.iterator();
                
                while(it.hasNext()){
                    EnqFuncao enqFuncao = it.next();
                    doPage(document,enqFuncao);
                    document.newPage();
                }

                document.close();      
                writePDFToResponse(((HttpServletResponse) facesContext.getExternalContext().getResponse()), baos, filename);             

            } catch (DocumentException ex) {
            Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         return null;    
        }
        
        private void doPage(Document document, EnqFuncao enqFuncao) throws DocumentException{
            
            Paragraph p = new Paragraph("Perfil de Função", fonteTitulo);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(new Paragraph(".", fonteEspaco));
            PdfPTable tableFuncao = new PdfPTable(new float[]{1,1});
            addTblFuncao(tableFuncao, enqFuncao);
            document.add(tableFuncao);
            
            document.add(new Paragraph(".", fonteEspaco));
            PdfPTable tableDescritivo = new PdfPTable(1);           
            addTblDescritivo(tableDescritivo, enqFuncao);
            PdfPTable tableDescritivoBorder = new PdfPTable(new float[]{1});
            addTblDescritivoBorder(tableDescritivoBorder, tableDescritivo);
            document.add(tableDescritivoBorder);
            
            document.add(new Paragraph(".", fonteEspaco));
            PdfPTable tableCompetencias = new PdfPTable(new float[]{1,1});
            addTblCompetencias(tableCompetencias, enqFuncao);
            document.add(tableCompetencias);
            document.add(new Paragraph(".", fonteEspaco));
//            PdfPTable tableRequisitos = new PdfPTable(new float[]{3,4,4});
            
            PdfPTable tableRequisitos = new PdfPTable(new float[]{2,4});
            addTblRequisitos(tableRequisitos, enqFuncao);
            PdfPTable tableRequisitosBorder = new PdfPTable(new float[]{1});
            addTblRequisitosBorder(tableRequisitosBorder, tableRequisitos);            
            document.add(tableRequisitosBorder);
            
            document.add(new Paragraph(".", fonteEspaco));
            document.add(new Paragraph(".", fonteEspaco));
            document.add(new Paragraph(".", fonteEspaco));
            document.add(new Paragraph("Observações:", font));
            document.add(new Paragraph(".", fonteEspaco));
            document.add(new Paragraph(enqFuncao.getObservacao(), font));
        
        }
        
        public void addTblFuncao(PdfPTable pdfPTable , EnqFuncao enqFuncao){
            
            String familia = "";
            String Carreira = "";  
            
            if (enqFuncao.getFamilia() != null) familia = enqFuncao.getFamilia().getDsComp();
            if (enqFuncao.getCarreira() != null) Carreira = enqFuncao.getCarreira().getDsComp();
            
            
            pdfPTable.setWidthPercentage(100f);
            pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT); 
            pdfPTable.getDefaultCell().setBorder(0);

            addCell(pdfPTable, new Chunk("Função: ", fonteSbuTitulo), new Chunk(enqFuncao.getDsRedz(), font));
            addCell(pdfPTable, new Chunk("Empresa ", fonteSbuTitulo), new Chunk(enqFuncao.getEmpresa().getSiglaE(), font));
            addCell(pdfPTable, new Chunk("Família Funcional: ", fonteSbuTitulo), new Chunk(familia, font));
            addCell(pdfPTable, new Chunk("Carreira: ", fonteSbuTitulo), new Chunk(Carreira, font));
            if (enqFuncao.getEnqFuncaoEstEmquadramentoCollection().isEmpty()){
                if(enqFuncao.getEstG1()!= null)
                    {addCell(pdfPTable, new Chunk("Estrutura: ", fonteSbuTitulo), new Chunk(enqFuncao.getEstG1().getDsRedz(), font));}
                else 
                    {addCell(pdfPTable, new Chunk("Estrutura: ", fonteSbuTitulo), new Chunk("Todos os Órgãos (quando aplicável)", font));}
                    
                addCell(pdfPTable, new Chunk("Enquadramento: ", fonteSbuTitulo), new Chunk("", font));
            } else {
                PdfPCell cell = new PdfPCell();
                cell.setBorder(0);
                Paragraph p = new Paragraph();
                if(enqFuncao.getEstG1()!= null)
                    {
                        p.add(new Chunk("Estrutura: ", fonteSbuTitulo)); 
                        p.add(new Chunk(enqFuncao.getEstG1().getDsRedz(), font));                  
                    }
                else
                    {
                        p.add(new Chunk("Estrutura: ", fonteEspaco)); 
                        p.add(new Chunk(enqFuncao.getEstG1().getDsRedz(), font)
                    );}
                cell.addElement(p);
                cell.setRowspan(enqFuncao.getEnqFuncaoEstEmquadramentoCollection().size());
                pdfPTable.addCell(cell);
                cell = new PdfPCell();
                cell.setBorder(0);
                Iterator<EnqFuncaoEstEmquadramento> iterator = enqFuncao.getEnqFuncaoEstEmquadramentoCollection().iterator();
                Font tempSbuTitulo = fonteSbuTitulo;
                while (iterator.hasNext()){
                   p = new Paragraph(); 
                   EnqFuncaoEstEmquadramento enqFuncaoEstEmquadramento = iterator.next();
                    if(enqFuncao.getEstG1()!= null)
                        {p.add(new Chunk("Enquadramento: ", tempSbuTitulo));}
                    else
                        {p.add(new Chunk("Estrutura: ", tempSbuTitulo));} 
                   p.add(new Chunk(enqFuncaoEstEmquadramento.getQacEstruturas().getDsComp()+"", font));
                   cell.addElement(p);      
                   tempSbuTitulo = fonteEspaco;
                }
                pdfPTable.addCell(cell);
            }
        }
        
   private void addCell(PdfPTable pdfPTable, Chunk chunkOne, Chunk chunkTwo  ){
       PdfPCell cell = new PdfPCell();
       Paragraph p = new Paragraph();
       cell.setBorder(0);
       p.add(chunkOne);
       p.add(chunkTwo);
       cell.addElement(p);
       pdfPTable.addCell(cell);
       
   }      
        
    private void addTblDescritivoBorder(PdfPTable pdfPTable, PdfPTable pdfPTableConteudo) {
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
 //       pdfPTable.getDefaultCell().setBorder(0);
        pdfPTable.getDefaultCell().setPaddingLeft(4);
        pdfPTable.getDefaultCell().setPaddingRight(4);
        pdfPTable.addCell(pdfPTableConteudo);
    
    }

   
   
    private void addTblDescritivo(PdfPTable pdfPTable, EnqFuncao enqFuncao) {
        
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT); 
        pdfPTable.getDefaultCell().setBorder(0);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);        
        cell.addElement(new Phrase("Descritivo Genérico da Função", fonteSbuTitulo));
        pdfPTable.addCell(cell);
        
        String s[] = enqFuncao.getDescritivo().split("\\*");
        
        for(int i =0; i < s.length ; i++){
//            cell = new PdfPCell();
//            cell.setBorder(1);
            pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            if (s[i].contains("- ")) pdfPTable.getDefaultCell().setPaddingLeft(30);
            else pdfPTable.getDefaultCell().setPaddingLeft(0);
//            cell.addElement(new Phrase(s[i], font));
            pdfPTable.addCell(new Phrase(s[i], font));
        }
        
         
    }

    private void addTblCompetencias(PdfPTable pdfPTable, EnqFuncao enqFuncao) {
        PdfPTable pdfComp = null;
        QacEnqTipoCompetencia qacEnqTipoCompetencia = null;
        
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPTable.getDefaultCell().setBorder(0);
        pdfPTable.getDefaultCell().setPaddingTop(2);
        pdfPTable.getDefaultCell().setPaddingLeft(20);
        pdfPTable.getDefaultCell().setPaddingRight(20);
        pdfPTable.getDefaultCell().setPaddingBottom(20);
        
        
        //---------------------------------------------
        // Ordenar SubBanda - Nivél de Competência  
        //---------------------------------------------
        List<EnqSubbandaCompetencia> listEnqSubbandaCompetencias = (List<EnqSubbandaCompetencia>) enqFuncao.getSubbanda().getEnqSubbandaCompetenciaCollection();

        if(!listEnqSubbandaCompetencias.isEmpty()){
            Collections.sort(listEnqSubbandaCompetencias,new Comparator(){
                public int compare(Object o1, Object o2) {
                    EnqSubbandaCompetencia p1 = (EnqSubbandaCompetencia) o1;
                    EnqSubbandaCompetencia p2 = (EnqSubbandaCompetencia) o2;

                    int ordem1 = p1.getNivel().getCompetencia().getTipo().getOrdem()*100;
                        ordem1 += p1.getNivel().getCompetencia().getOrdem(); 

                    int ordem2 = p2.getNivel().getCompetencia().getTipo().getOrdem()*100;
                        ordem2 += p2.getNivel().getCompetencia().getOrdem();

                    return ordem1 < ordem2 ? -1 : (ordem1 > ordem2 ? +1 : 0); 
                }        
            });
        }
        
        //-------------------------------------------           
        // Ordenar Função - Nivél de Competência  
        //--------------------------------------------        
        List<EnqFuncaoCompetencia> listEnqFuncaoCompetencias = (List<EnqFuncaoCompetencia>) enqFuncao.getEnqFuncaoCompetenciaCollection();

        if (!listEnqFuncaoCompetencias.isEmpty()){
            Collections.sort(listEnqFuncaoCompetencias,new Comparator(){
                public int compare(Object o1, Object o2) {
                    EnqFuncaoCompetencia p1 = (EnqFuncaoCompetencia) o1;
                    EnqFuncaoCompetencia p2 = (EnqFuncaoCompetencia) o2;

                    int ordem1 = p1.getNivel().getCompetencia().getTipo().getOrdem()*100;
                        ordem1 += p1.getNivel().getCompetencia().getOrdem(); 

                    int ordem2 = p2.getNivel().getCompetencia().getTipo().getOrdem()*100;
                        ordem2 += p2.getNivel().getCompetencia().getOrdem();

                    return ordem1 < ordem2 ? -1 : (ordem1 > ordem2 ? +1 : 0); 
                }        
            });
        }
         //---------------------------------------------
        //Competências da função através da
        //SubBanda - Nivél de Competência  
        //----------------------------------------------        
        Iterator<EnqSubbandaCompetencia> it = listEnqSubbandaCompetencias.iterator();
        while (it.hasNext()) {
            EnqSubbandaCompetencia enqSubbandaCompetencia  = it.next();
            if (qacEnqTipoCompetencia != null && qacEnqTipoCompetencia != enqSubbandaCompetencia.getNivel().getCompetencia().getTipo()){
                pdfComp.addCell(new Phrase(".", fonteEspaco));
                pdfPTable.addCell(pdfComp);
            }
            
            if (qacEnqTipoCompetencia != enqSubbandaCompetencia.getNivel().getCompetencia().getTipo()){ 
                pdfComp = new PdfPTable(new float[]{3,1});
                pdfComp.setKeepTogether(true);
                pdfComp.setWidthPercentage(80f);
                pdfComp.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                pdfComp.addCell(new Phrase(enqSubbandaCompetencia.getNivel().getCompetencia().getTipo().getDsComp(), fonteSbuTitulo));
                pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfComp.addCell(new Phrase("Nível", fonteSbuTitulo));
            }
            pdfComp.getDefaultCell().setBackgroundColor(Color.WHITE);
            pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfComp.addCell(new Phrase(enqSubbandaCompetencia.getNivel().getCompetencia().getDsComp(), font));
            pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            if (enqSubbandaCompetencia.getNivel().getNivel()!=0)                
                pdfComp.addCell(new Phrase(enqSubbandaCompetencia.getNivel().getNivel()+"" , font));
            else  pdfComp.addCell(new Phrase("", font));
            
            qacEnqTipoCompetencia = enqSubbandaCompetencia.getNivel().getCompetencia().getTipo(); 
        }
        
        if (!it.hasNext()){
            pdfComp.addCell(new Phrase(".", fonteEspaco));
            pdfPTable.addCell(pdfComp);
        }          
        
        //-------------------------------------------
        //Competências da função através da                
        //Função - Nivél de Competência  
        //--------------------------------------------
        qacEnqTipoCompetencia = null;
        Iterator<EnqFuncaoCompetencia> iterator = listEnqFuncaoCompetencias.iterator();
        while (iterator.hasNext()) {
            EnqFuncaoCompetencia enqFuncaoCompetencia = iterator.next();
            if (qacEnqTipoCompetencia != null && qacEnqTipoCompetencia != enqFuncaoCompetencia.getNivel().getCompetencia().getTipo()){
                pdfComp.addCell(new Phrase(".", fonteEspaco));
                pdfPTable.addCell(pdfComp);
            }
            
            if (qacEnqTipoCompetencia != enqFuncaoCompetencia.getNivel().getCompetencia().getTipo()){ 
                pdfComp = new PdfPTable(new float[]{3,1});
                pdfComp.setKeepTogether(true);
                pdfComp.setWidthPercentage(80f);
                pdfComp.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                pdfComp.addCell(new Phrase(enqFuncaoCompetencia.getNivel().getCompetencia().getTipo().getDsComp(), fonteSbuTitulo));
                pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfComp.addCell(new Phrase("Nível", fonteSbuTitulo));
            }
            
            pdfComp.getDefaultCell().setBackgroundColor(Color.WHITE);
            pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfComp.addCell(new Phrase(enqFuncaoCompetencia.getNivel().getCompetencia().getDsComp(), font));
            pdfComp.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            if(enqFuncaoCompetencia.getNivel().getNivel()!=0)            
                pdfComp.addCell(new Phrase(enqFuncaoCompetencia.getNivel().getNivel()+"" , font));
            else 
                pdfComp.addCell(new Phrase("" , font));
            
            qacEnqTipoCompetencia = enqFuncaoCompetencia.getNivel().getCompetencia().getTipo(); 
        }
        
        if (!iterator.hasNext()){
            pdfComp.addCell(new Phrase(".", fonteEspaco));
            pdfPTable.addCell(pdfComp);
        }        
        
    }

    private void addTblRequisitos(PdfPTable pdfPTable, EnqFuncao enqFuncao) {
        pdfPTable.setKeepTogether(true);
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);   
        pdfPTable.getDefaultCell().setColspan(2);
        pdfPTable.addCell(new Phrase("Requisitos Desejáveis", fonteSbuTitulo));
//        pdfPTable.addCell(new Phrase("Críticos", fonteSbuTitulo));
//        pdfPTable.addCell(new Phrase("Descritivo", fonteSbuTitulo));
        pdfPTable.getDefaultCell().setColspan(1);
        pdfPTable.getDefaultCell().setBackgroundColor(Color.WHITE);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);   

        //------------------------------------------------------------------
        //--------------- Com requesitos lançados---------------------------
        
       
        //-------------------------------------------           
        // Ordenar Função - Nivél de Competência  
        //--------------------------------------------        
        List<EnqFuncaoRequisitos> listEnqFuncaoRequisitosCollection = (List<EnqFuncaoRequisitos>) enqFuncao.getEnqFuncaoRequisitosCollection();
        
        if(!listEnqFuncaoRequisitosCollection.isEmpty()){
        
            Collections.sort(listEnqFuncaoRequisitosCollection,new Comparator(){
                public int compare(Object o1, Object o2) {
                    EnqFuncaoRequisitos p1 = (EnqFuncaoRequisitos) o1;
                    EnqFuncaoRequisitos p2 = (EnqFuncaoRequisitos) o2;

                    int ordem1 = p1.getQacEnqRequisitos().getOrdem();

                    int ordem2 = p2.getQacEnqRequisitos().getOrdem();

                    return ordem1 < ordem2 ? -1 : (ordem1 > ordem2 ? +1 : 0); 
                }        
            });

           Iterator<EnqFuncaoRequisitos> it = listEnqFuncaoRequisitosCollection.iterator();

            while (it.hasNext()) {
                EnqFuncaoRequisitos enqFuncaoRequisitos = it.next();
                pdfPTable.addCell(new Phrase(enqFuncaoRequisitos.getQacEnqRequisitos().getDsComp() , font));
                pdfPTable.addCell(new Phrase(enqFuncaoRequisitos.getCriticos() , font));
    //            pdfPTable.addCell(new Phrase(enqFuncaoRequisitos.getDesejaveis() , font));

            }
        }
        
         

         //-------------------------------------------------------------------
        //------------------------ Sem requesitos ----------------------------
  
        
        Iterator<EnqFuncRequisitoAll> iterator = enqFuncao.getEnqFuncRequisitoAllCollection().iterator();
       
        while (iterator.hasNext()) {
            EnqFuncRequisitoAll enqFuncRequisitoAll = iterator.next();
            pdfPTable.addCell(new Phrase(enqFuncRequisitoAll.getEnqRequisito().getDsComp() , font));
//            pdfPTable.addCell(new Phrase("." , fonteEspaco));
            pdfPTable.addCell(new Phrase("." , fonteEspaco));
        }
      //---------------------------------------------------

                  
    }
    
    private void addTblRequisitosBorder(PdfPTable pdfPTable, PdfPTable pdfPTableConteudo) {
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPTable.getDefaultCell().setBorder(0);
        pdfPTable.getDefaultCell().setPaddingLeft(20);
        pdfPTable.getDefaultCell().setPaddingRight(20);
        pdfPTable.addCell(pdfPTableConteudo);
    
    }
    
    
   private void writePDFToResponse(HttpServletResponse response, ByteArrayOutputStream baos, String fileName) throws IOException, DocumentException {     
        response.setContentType("application/pdf");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".pdf");
        response.setContentLength(baos.size());
        
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }
   
   
       class HeaderFooter extends PdfPageEventHelper {
         
        int pagenumber;
        Font fontHead = new Font(Font.HELVETICA, 9);
        DateFormat df = DateFormat.getDateInstance(3, Locale.getDefault());
   
        @Override
        public void onStartPage(PdfWriter writer, Document document) {            
            pagenumber++;        
        }
        
        @Override
         public void onEndPage(PdfWriter writer, Document document) {
            
            Rectangle rectLeft = writer.getBoxSize("left");
            Rectangle rectCenter = writer.getBoxSize("center");
            Rectangle rectRigth = writer.getBoxSize("rigth");
            
            
            //logo
        PdfContentByte overLogo = writer.getDirectContent();
        
       try {
           overLogo.addImage(img, 160, 0, 0.05f * img.getHeight(), 0.65f * img.getHeight(), 40, 800);
       } catch (DocumentException ex) {
           Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
       }

                    
            
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        BaseFont bf;
            try {
                bf = BaseFont.createFont();
                over.beginText();
                over.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
                over.setFontAndSize(bf, 1);
                over.setTextMatrix(0, 6, -6, 1, 20, 64);
                over.showText("IMP_RH_PF_Ago2012_02");
                over.endText(); 
                over.restoreState();                                
            } catch (DocumentException ex) {
                Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            }

        PdfContentByte overLine = writer.getDirectContent();
        overLine.saveState();
        BaseFont bfLine;
            try {
                bfLine = BaseFont.createFont();
                overLine.beginText();
                overLine.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
                overLine.setFontAndSize(bfLine, 10);
                overLine.setTextMatrix(40, 55);
                overLine.showText("___________________________________________________________________________________________");
                overLine.endText(); 
                overLine.restoreState();                                
            } catch (DocumentException ex) {
                Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PDFtoEnquadramento.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_LEFT, new Phrase("DRH - Direcção de Recursos Humanos", fontHead),
                    (rectLeft.getLeft() + rectLeft.getRight()) / 2, rectLeft.getBottom() - 18, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, new Phrase(df.format(new java.util.Date()), fontHead),
                    (rectCenter.getLeft() + rectCenter.getRight()) / 2, rectCenter.getBottom() - 18, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_RIGHT,  new Phrase( String.format( "Página %d " , pagenumber), fontHead)  ,
                    (rectRigth.getLeft() + rectRigth.getRight()) / 2, rectRigth.getBottom() - 18, 0);

         }       

     }

        
}
