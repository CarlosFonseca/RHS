/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.faces.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import comp.emigrados.InfoEmpresas;
import migr.session.RemuneracoesEmpregadoFacade;

/**
 *
 * @author bnf02107
 */
public class QPtoPDF {
    
    private migr.session.RemuneracoesEmpregadoFacade ejbFacade;
    
    Font fonteTitulo = new Font(Font.HELVETICA, 13, Font.BOLD);
    Font fonteEspaco = new Font(Font.HELVETICA, 13, Font.BOLD, Color.WHITE);
    Font fonteSbuTitulo = new Font(Font.HELVETICA, 8, Font.BOLD);
    Font fontHead = new Font(Font.HELVETICA, 9);
    Font font = new Font(Font.HELVETICA, 8);
    
    String msg = null;
    
    public String exportaQP(FacesContext facesContext,  String filename, String empresa, String direccao, List<InfoEmpresas> listInfoEmpresas ) throws IOException {
        
        if (listInfoEmpresas.isEmpty()){
            msg = "Não existem dados para o critério seleccionado";            
        }
        else{
            try {
                ejbFacade = (RemuneracoesEmpregadoFacade) new InitialContext().lookup("java:module/RemuneracoesEmpregadoFacade");
            } catch (NamingException ex) {
                Logger.getLogger(QPtoPDF.class.getName()).log(Level.SEVERE, null, ex);
            }



           try {
                Document document = new Document(PageSize.A4.rotate());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, baos);


                HeaderFooter event = new HeaderFooter();

                writer.setBoxSize("rigthTop", new Rectangle(36, 54, 1550, 550));
                writer.setBoxSize("left", new Rectangle(36, 54, 78, 788));
                writer.setBoxSize("center", new Rectangle(36, 54, 788, 788));
                writer.setBoxSize("rigth", new Rectangle(36, 54, 1550, 788));

                writer.setPageEvent(event);

                document.open();

                PdfPTable tableMaster = new PdfPTable(1);
                tableMaster.setWidthPercentage(100f);
                tableMaster.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                tableMaster.getDefaultCell().setBorder(0);
                tableMaster.addCell(new Phrase(empresa, fonteTitulo));
                tableMaster.addCell(new Phrase(".", fonteEspaco));
                tableMaster.addCell(new Phrase(direccao, fonteTitulo));
                tableMaster.addCell(new Phrase(".", fonteEspaco));
                tableMaster.setHeaderRows(4);
                PdfPTable table = getTable(listInfoEmpresas);     //Gera tabela das estruturas 
                tableMaster.addCell(table);
                document.add(tableMaster);           
                document.close();      
                writePDFToResponse(((HttpServletResponse) facesContext.getExternalContext().getResponse()), baos, filename); 

            } catch (DocumentException ex) {
                Logger.getLogger(QPtoPDF.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
 
       return msg;
}
    
    private PdfPTable getTable(List<InfoEmpresas> listInfoEmpresas){
        
        PdfPTable table = null;
        int cdEstrutura=0;
        long semVaga=0;
        long totalSemVaga=0;
        long empregados =0;
        long totalEmpregados =0;
        String quadro = "";
        Integer totalQuadro = 0;
        Integer totalQuadroAdock = 0;
        long total = 0;
        long totalTotal = 0;

       
        DateFormat df = DateFormat.getDateInstance(3, Locale.getDefault());
  
        PdfPTable tableMaster = new PdfPTable(1);
        tableMaster.setWidthPercentage(100f);
        tableMaster.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableMaster.getDefaultCell().setBorder(0);

        
        Iterator<InfoEmpresas> it = listInfoEmpresas.iterator();

        while(it.hasNext()){
         InfoEmpresas ie = it.next();
         
         if((ie.getInfoEmpresasPK().getCdEstrutura() != cdEstrutura) && (cdEstrutura != 0)){

                table.getDefaultCell().setBorderWidthTop(1);
                table.getDefaultCell().setBorderColor(Color.LIGHT_GRAY);
                table.getDefaultCell().setColspan(12); 
                PdfPTable tblTEstrutura = getPdfPTable(semVaga, empregados, quadro, total) ; // Totais da estruturas
                table.addCell(tblTEstrutura);
                tableMaster.addCell(table);
          }

        if(cdEstrutura != ie.getQacEstruturas().getQacEstruturasPK().getCdEstrutura()){
            table = new PdfPTable(new float[]{3,20,6,6,12,3,2,6,3,10,18,12});
            table.setWidthPercentage(100f);
            table.getDefaultCell().setColspan(12);
            table.setKeepTogether(true);              
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT); 
            table.addCell(new Phrase(ie.getQacEstruturas().getDsComp(), fonteSbuTitulo));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setColspan(1);
            table.getDefaultCell().setBorder(1);
            table.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(new Phrase("Nre", fontHead));
            table.addCell(new Phrase("Nome", fontHead));
            table.addCell(new Phrase("Dt Nasc", fontHead));
            table.addCell(new Phrase("Dt Admiss", fontHead));
            table.addCell(new Phrase("Habilitações", fontHead));
            table.addCell(new Phrase("Grp", fontHead));
            table.addCell(new Phrase("Nv", fontHead));
            table.addCell(new Phrase("Dt Nível", fontHead));
            table.addCell(new Phrase("IHT", fontHead));
            table.addCell(new Phrase("Categoria", fontHead));
            table.addCell(new Phrase("Função", fontHead));
            table.addCell(new Phrase("Vínculo", fontHead));
           }
        
         table.getDefaultCell().setBorder(0);
         table.getDefaultCell().setBackgroundColor(null);
         
         addColumnValue(table,ie.getCadastros().getNre().toString(),font,Element.ALIGN_RIGHT);
         addColumnValue(table,ie.getCadastros().getNomeRedz(),font,Element.ALIGN_LEFT);
         addColumnValue(table,df.format(ie.getCadastros().getDtNasc()),font,Element.ALIGN_CENTER);
         addColumnValue(table,df.format(ie.getCadastros().getInfoGeral().getDtAdmiss()),font,Element.ALIGN_CENTER);
         if (ie.getCadastros().getCdHabLit()!= null) 
            addColumnValue(table,ie.getCadastros().getCdHabLit().getDsRedz(),font,Element.ALIGN_LEFT);
         else 
            addColumnValue(table,"",font,Element.ALIGN_LEFT);
         if (ie.getCadastros().getInfoGeral()!=null)
             addColumnValue(table,ie.getCadastros().getInfoGeral().getCdGrupo(),font,Element.ALIGN_CENTER);
         else 
             addColumnValue(table,"",font,Element.ALIGN_CENTER);
         if (ie.getCadastros().getInfoGeral().getCdNivel()!=null)
            addColumnValue(table,ie.getCadastros().getInfoGeral().getCdNivel()+"" ,font,Element.ALIGN_CENTER);
         else 
             addColumnValue(table,"" ,font,Element.ALIGN_CENTER);
         if (ie.getCadastros().getInfoGeral().getDtNivel()!=null)
            addColumnValue(table,df.format(ie.getCadastros().getInfoGeral().getDtNivel()),font,Element.ALIGN_CENTER);
         else  
             addColumnValue(table,"",font,Element.ALIGN_CENTER);
         addColumnValue(table,getIHT(ie.getCadastros().getNre()),font,Element.ALIGN_CENTER);
         if (ie.getCadastros().getInfoProfs().getCdFuncao()!= null) 
             addColumnValue(table,ie.getCadastros().getInfoProfs().getCdFuncao().getDsComp(),font,Element.ALIGN_LEFT);
         else 
             addColumnValue(table,"",font,Element.ALIGN_LEFT);
         if (ie.getCadastros().getInfoProfs().getCdFuncInt()!=null) 
             addColumnValue(table,ie.getCadastros().getInfoProfs().getCdFuncInt().getDsComp(),font,Element.ALIGN_LEFT);
         else 
             addColumnValue(table,"",font,Element.ALIGN_LEFT);
         addColumnValue(table,ie.getCadastros().getCdSituacao().getDsRedz(),font,Element.ALIGN_LEFT);
         
    //------------------------------------------------------------     
          if(ie.getInfoEmpresasPK().getCdEstrutura() == cdEstrutura){
              if (ie.getCadastros().getCdSituacao().getCdSituacao()==21){
                semVaga +=1;
                totalSemVaga +=1;
              }
              
              empregados += 1;
              totalEmpregados += 1;
//              quadro = ie.getQacEstruturas().getPontuacao();
              
             if ((ie.getCadastros().getCdSituacao().getCdSituacao() !=21)
             && (!ie.getCadastros().getInfoProfs().getCdFuncao().getCdFuncao().equals("SL"))){
                    total += 1;
                    totalTotal += 1;
             }
              
          } else if(ie.getInfoEmpresasPK().getCdEstrutura() != cdEstrutura){
              semVaga = 0;
              
               if (ie.getCadastros().getCdSituacao().getCdSituacao()==21){
                semVaga = 1;
                totalSemVaga += 1;
              }
              
              empregados = 1;
              totalEmpregados += 1;
              quadro = ie.getQacEstruturas().getPontuacao();
              try {
                  totalQuadro +=  Integer.parseInt(quadro.substring(0, 4));                    
              } catch (Exception e) {
                  Logger.getLogger(e.getMessage());
              }
              
              try {
//                  String a = quadro.substring(0,1);
                  totalQuadroAdock +=  Integer.parseInt(quadro.substring(6, 7)); 

              } catch (Exception e) {
                  Logger.getLogger(e.getMessage());
              }
              
             
             if ((ie.getCadastros().getCdSituacao().getCdSituacao() !=21)
             && (!ie.getCadastros().getInfoProfs().getCdFuncao().getCdFuncao().equals("SL"))){
                    total = 1;
                    totalTotal +=1;
             }
                         
          }   
          
         cdEstrutura = ie.getQacEstruturas().getQacEstruturasPK().getCdEstrutura();          
        }   
        
        if (!it.hasNext()){
            table.getDefaultCell().setBorderWidthTop(1);
            table.getDefaultCell().setBorderColor(Color.LIGHT_GRAY);
            table.getDefaultCell().setColspan(12); 
            PdfPTable tblTEstrutura = getPdfPTable(semVaga, empregados, quadro, total) ; //Total da última estrutura
            table.addCell(tblTEstrutura);
            table.getDefaultCell().setBorder(0);
            PdfPTable tblTotalHead= getPdfPTableHeaderTotal() ;
            table.addCell(tblTotalHead);
            PdfPTable tblTotal= getPdfPTableTotal(totalQuadro,totalQuadroAdock, totalTotal , totalSemVaga) ; //Total da Sintese
            table.addCell(tblTotal);
            tableMaster.addCell(table);
        }
        

        return tableMaster;
  }  
    
  private PdfPTable getPdfPTable(long semVaga, long empregados, String quadro,long total){
      
            PdfPTable tblTEstrutura = new PdfPTable(new float[]{2,20,20,2,10,4,10,2});
            
            tblTEstrutura.getDefaultCell().setBorder(0);
            tblTEstrutura.addCell(getCellResult(new Phrase(semVaga+"", fontHead)));            
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            tblTEstrutura.addCell(new Phrase("E.S/Vaga", fontHead));          
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            tblTEstrutura.addCell(new Phrase("Nº Empregados", fontHead));            
            tblTEstrutura.addCell(getCellResult(new Phrase(empregados+"", fontHead)));            
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            tblTEstrutura.addCell(new Phrase("Quadro", fontHead));           
            tblTEstrutura.addCell(getCellResult(new Phrase(quadro+"", fontHead)));            
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            tblTEstrutura.addCell(new Phrase("Total", fontHead));           
            tblTEstrutura.addCell(getCellResult(new Phrase(total+"", fontHead)));
            tblTEstrutura.setSpacingAfter(3);           

            tblTEstrutura.getDefaultCell().setColspan(8);
            tblTEstrutura.addCell("");
            tblTEstrutura.setSpacingAfter(10);
      
            return tblTEstrutura;
  }  
  
     private PdfPTable getPdfPTableHeaderTotal(){
      
            PdfPTable tblTEstrutura = new PdfPTable(new float[]{20,4});
            
            tblTEstrutura.getDefaultCell().setBorder(0);
            tblTEstrutura.addCell("");
            tblTEstrutura.addCell(getCellResult(new Phrase("Quadro Sintese", fontHead)));

      
            return tblTEstrutura;
  }  
  
   private PdfPTable getPdfPTableTotal(long totalQuadro, long totalQuadroAdock, long totalTotal,long totalSemVaga){
      
            PdfPTable tblTEstrutura = new PdfPTable(new float[]{11,11,1,3});
            
            tblTEstrutura.getDefaultCell().setBorder(0);
            tblTEstrutura.addCell("");
            tblTEstrutura.addCell("");
            if (totalQuadroAdock>0){
            tblTEstrutura.addCell(getCellResult(new Phrase(totalQuadro + " + " + totalQuadroAdock, fontHead)));     
            }else{
            tblTEstrutura.addCell(getCellResult(new Phrase(totalQuadro+"", fontHead)));     
            }                                    
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            tblTEstrutura.addCell(new Phrase("Quadro", fontHead));
            
            tblTEstrutura.addCell("");
            tblTEstrutura.addCell("");
            tblTEstrutura.addCell(getCellResult(new Phrase(totalTotal+"",  fontHead)));
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            tblTEstrutura.addCell(new Phrase("Situação", fontHead)); 

            tblTEstrutura.addCell("");
            tblTEstrutura.addCell("");
            tblTEstrutura.addCell(getCellResult(new Phrase(totalQuadro + totalQuadroAdock - totalTotal+"", fontHead)));             
            tblTEstrutura.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            tblTEstrutura.addCell(new Phrase("Vagas", fontHead));           

            tblTEstrutura.addCell("");
            tblTEstrutura.addCell("");
            tblTEstrutura.addCell(getCellResult(new Phrase(totalSemVaga+"", fontHead)));            
            tblTEstrutura.getDefaultCell().setHorizontalAlignment( Element.ALIGN_LEFT);
            tblTEstrutura.addCell(new Phrase("E.S/Vagas", fontHead));           

      
            return tblTEstrutura;
  }  
  
  
  
  private PdfPCell getCellResult(Phrase phrase ){
      
            PdfPCell cellResult = new PdfPCell(phrase);             
            cellResult.setBackgroundColor(Color.LIGHT_GRAY);
            cellResult.setHorizontalAlignment(Element.ALIGN_CENTER);
            return cellResult;
      
  }
  
   private void addColumnValue(PdfPTable pdfTable, String data, Font font,int Alignment){
       
       pdfTable.getDefaultCell().setHorizontalAlignment(Alignment);             
       pdfTable.addCell(new Phrase(data,font)) ;
         
   
   }
  
      public String getIHT(int nre){

        int cd_linha = 0;
        String Isencao ="";

         try {
  //          cd_linha =  (Integer) ejbFacade.findSingleResult("RemuneracoesEmpregado.findIhtByCadNre", new String[]{"cadNre"}, new Object[]{nre} );
            cd_linha =  ejbFacade.findIHTByNre(nre).getRemuneracoesEmpregadoPK().getCdLinha();
        } catch (Exception e) {
            Logger.getLogger(e.getMessage());
        }
         
        if (cd_linha == 1)
                Isencao = "P";

         if (cd_linha == 2)
                Isencao = "T";

        return Isencao;
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
            Rectangle rectRigthTop = writer.getBoxSize("rigthTop");
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_RIGHT,  new Phrase("CONFIDENCIAL", fontHead)  ,
                    (rectRigthTop.getLeft() + rectRigthTop.getRight()) / 2, rectRigthTop.getTop(), 0);            
        }
        
        @Override
         public void onEndPage(PdfWriter writer, Document document) {
                        
            Rectangle rectLeft = writer.getBoxSize("left");
            Rectangle rectCenter = writer.getBoxSize("center");
            Rectangle rectRigth = writer.getBoxSize("rigth");
            
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_LEFT, new Phrase("DRH - Direcção de Recursos Humanos", fontHead),
                    (rectLeft.getLeft() + rectLeft.getRight()) / 2, rectLeft.getBottom() - 18, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, new Phrase(df.format(new java.util.Date()), fontHead),
                    (rectCenter.getLeft() + rectCenter.getRight()) / 2, rectCenter.getBottom() - 18, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_RIGHT,  new Phrase( String.format( "página %d " , pagenumber), fontHead)  ,
                    (rectRigth.getLeft() + rectRigth.getRight()) / 2, rectRigth.getBottom() - 18, 0);

         }       

     }
    
    
}
