/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comp.file;

import comp.entities.IhtDeclaracao;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import comp.emigrados.DocumentosIdentificacao;
import comp.emigrados.UserInfoContratos;
/**
 *
 * @author bnf02107
 */

public class DocxExporterIHT extends  AbstractDocx {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
//       public ByteArrayOutputStream BAOSDocx (IhtDeclaracao ihtDeclaracao, Iterator<DocumentosIdentificacao> itDI){  
       public ByteArrayOutputStream BAOSDocx (IhtDeclaracao ihtDeclaracao){
        
            
        HashMap<String, String> mappings = new HashMap<String, String>();
        String text_ncc_nbi = "";
        String NIF = "";
        String sufixo = "";
        String contrato = "";
        
        WordprocessingMLPackage wordMLPackage = null;
//        String sourcePathFile = "";            
        String destinationPathFile = "IHT.docx";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        String fileName = "IHT";      
        DateFormat df = DateFormat.getDateInstance(3, Locale.getDefault());
        String freguesia = "";
        String conselho = "";
        
        NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso(true, false, true);            
        String s = numeroPorExtenso.converteMoeda(ihtDeclaracao.getEmpresa().getCapSocial());           

        if (ihtDeclaracao.getNre().getSexo().equals("F")) sufixo = "a";
        
        Iterator<DocumentosIdentificacao> itDI = ihtDeclaracao.getNre().getDocumentosIdentificacaoCollection().iterator();
        
        while (itDI.hasNext()) {
            DocumentosIdentificacao documentosIdentificacao = itDI.next();
            
            if (documentosIdentificacao.getDocumentosIdentificacaoPK().getCdDocId().equals("1"))
                text_ncc_nbi = documentosIdentificacao.getDocumentosIdentificacaoPK().getNrDocId() ;
//                text_ncc_nbi = "portador" + sufixo + " do Bilhete de Identidade n.º. " + documentosIdentificacao.getDocumentosIdentificacaoPK().getNrDocId() 
//                             + ", emitido pelo Arquivo de Identificação "  + documentosIdentificacao.getEntEmiss()
//                             + ", em " + df.format(documentosIdentificacao.getDtEmiss());
            
             if (documentosIdentificacao.getDocumentosIdentificacaoPK().getCdDocId().equals("7"))
                text_ncc_nbi = documentosIdentificacao.getDocumentosIdentificacaoPK().getNrDocId();
//                text_ncc_nbi = "portador" + sufixo + " do Cartão de Cidadão nº:" + documentosIdentificacao.getDocumentosIdentificacaoPK().getNrDocId();
             
             if (documentosIdentificacao.getDocumentosIdentificacaoPK().getCdDocId().equals("2"))
                NIF = documentosIdentificacao.getDocumentosIdentificacaoPK().getNrDocId() ;            
        }
        
        Double VIHT = (ihtDeclaracao.getValor()*12) / (ihtDeclaracao.getNre().getInfoProfs().getCdHorSemana().getNrHrsTrabSem()*52)*22* ihtDeclaracao.getTipoId().getValor() ;
        java.text.DecimalFormat decF = new java.text.DecimalFormat("###.##");        
        String viht = decF.format(VIHT);
        
        Iterator<UserInfoContratos> itUIC = ihtDeclaracao.getNre().getUserInfoContratosCollection().iterator();
        
        while (itUIC.hasNext()){
             UserInfoContratos userInfoContratos = itUIC.next();
             
             if (userInfoContratos.getEstado().equals("A"))
                 contrato = userInfoContratos.getTiposDeContrato().getDsComp();
        }
        
        
        mappings.put("NRE", ihtDeclaracao.getNre().getNre()+"");
        mappings.put("CD_IHT", (String) isNull(ihtDeclaracao.getTipoId().getId()));
        mappings.put("IHT", (String) isNull(ihtDeclaracao.getTipoId().getDsComp()));
        mappings.put("DS_SOCIAL", (String) isNull(ihtDeclaracao.getEmpresa().getDsSocial()));
        mappings.put("MORADA", (String) isNull(ihtDeclaracao.getEmpresa().getQacEstabelecimentos().getMorada()));
        if(ihtDeclaracao.getEmpresa().getQacEstabelecimentos().getQacCodigosPostais()!=null){
            mappings.put("EMP_CD_POSTAL", ihtDeclaracao.getEmpresa().getQacEstabelecimentos().getQacCodigosPostais().getQacCodigosPostaisPK().getCdPostal());
            mappings.put("EMP_NR_ORDEM_POSTAL", ihtDeclaracao.getEmpresa().getQacEstabelecimentos().getQacCodigosPostais().getQacCodigosPostaisPK().getNrOrdem()+"");
            mappings.put("EMP_CD_DESC", ihtDeclaracao.getEmpresa().getQacEstabelecimentos().getQacCodigosPostais().getDsArea());
        } else {
        mappings.put("CD_POSTAL", "");
        mappings.put("NR_ORDEM_POSTAL", "");
        mappings.put("CD_DESC", "");
        }
//        mappings.put("LOCAL", (String) isNull(ihtDeclaracao.getEmpresa().getQacEstabelecimentos().getLocal()));
        mappings.put("EXTENSO", s);
        mappings.put("NR_PSS_COLECT", ihtDeclaracao.getEmpresa().getNrPssColect()+ "");
//        mappings.put("TX_REPR", (String) isNull(ihtDeclaracao.getRepresentantesId().getDsLong()));
        mappings.put("NOME_COMP", (String) isNull(ihtDeclaracao.getNre().getNomeComp()));
//        if (ihtDeclaracao.getNre().getQacFreguesias()!=null) {
//            freguesia = ihtDeclaracao.getNre().getQacFreguesias().getDsComp();
//            if (ihtDeclaracao.getNre().getQacFreguesias().getQacConcelhos()!= null){
//                conselho = ihtDeclaracao.getNre().getQacFreguesias().getQacConcelhos().getDsConc();
//            }
//        }  
//        mappings.put("DS_FREG", freguesia);
//        mappings.put("DS_CONC", conselho);            
        mappings.put("TEXT_NCC_NBI", (String) text_ncc_nbi);
        mappings.put("NIF", NIF);
        mappings.put("CONTRATO", (String) contrato);        
        mappings.put("MORADA_1", (String) isNull(ihtDeclaracao.getNre().getEnderecos().getMorada1()));
        mappings.put("MORADA_2", (String) isNull(ihtDeclaracao.getNre().getEnderecos().getMorada2()));
        mappings.put("LOCALIDADE", (String) ihtDeclaracao.getNre().getEnderecos().getLocalidade());
        if(ihtDeclaracao.getNre().getEnderecos().getQacCodigosPostais()!=null){
            mappings.put("CD_POSTAL", ihtDeclaracao.getNre().getEnderecos().getQacCodigosPostais().getQacCodigosPostaisPK().getCdPostal());
            mappings.put("NR_ORDEM_POSTAL", ihtDeclaracao.getNre().getEnderecos().getQacCodigosPostais().getQacCodigosPostaisPK().getNrOrdem()+"");
            mappings.put("CD_DESC",  ihtDeclaracao.getNre().getEnderecos().getQacCodigosPostais().getDsArea());
        } else {
        mappings.put("CD_POSTAL", "");
        mappings.put("NR_ORDEM_POSTAL", "");
        mappings.put("CD_DESC", "");
        }
        
//        if (ihtDeclaracao.getNre().getInfoGeral().getQacEstabelecimentos()!=null){
//            mappings.put("ESTAB_MD", (String) isNull(ihtDeclaracao.getNre().getInfoGeral().getQacEstabelecimentos().getMorada()));
//            mappings.put("ESTAB_LOCAL", (String) isNull(ihtDeclaracao.getNre().getInfoGeral().getQacEstabelecimentos().getLocal()));
//        } else{
//            mappings.put("ESTAB_MD", "");
//            mappings.put("ESTAB_LOCAL", "");    
//        }    
        
        if(ihtDeclaracao.getCategoriaId().getId().equalsIgnoreCase("---"))
            mappings.put("CATEGORIA", (String) isNull(ihtDeclaracao.getNre().getInfoProfs().getCdFuncao().getDsComp()));
        else 
            mappings.put("CATEGORIA",  ihtDeclaracao.getCategoriaId().getDsComp());                

//        mappings.put("DESCRICAO", (String) isNull(ihtDeclaracao.getDsFuncDesc()));        
//        mappings.put("DES_TX", (String) isNull(ihtDeclaracao.getAlineaTextoId().getDsComp()));        
        mappings.put("HORA", (String) isNull(ihtDeclaracao.getTipoId().getDsAbrv()));        
        mappings.put("VALOR", (String) isNull(ihtDeclaracao.getValor()));        
        mappings.put("VIHT", (String) isNull(viht));   
                
//       if (ihtDeclaracao.getCategoriaId()==null) sourcePathFile ="c:/docx/iht/acordoIHT.docx";
//        else sourcePathFile = "c:/docx/iht/" + ihtDeclaracao.getCategoriaId().getFicheiro() + ".docx";       
       
       
       wordMLPackage = loadFile(wordMLPackage, ihtDeclaracao.getCategoriaId().getFicheiroId().getFicheiro());
//        wordMLPackage = loadFile(wordMLPackage, sourcePathFile); 
        processaWordprocessingMLPackage(wordMLPackage, mappings);        
        baos = wordToStream(wordMLPackage, baos); 
        
        return baos;
 }
    
    
    
}
