package configuracao;

import java.io.File;
import java.io.FileOutputStream;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public class SplitPDFFile {

	public static int splitPDF(String inFile) {
		// TODO Auto-generated method stub
		
		 try {
	            //String inFile = "C:\\Users\\Yaissa\\workspace\\PdfWebSpring\\src\\resources\\temp\\achei3.pdf";
	            //System.out.println ("Reading " + inFile);
	            PdfReader reader = new PdfReader(inFile);
	            
	            int n = reader.getNumberOfPages();
	            System.out.println ("Number of pages : " + n);
	            int i = 0;            
	            while ( i < n ) {
	                String outFile = inFile.substring(0, inFile.indexOf(".pdf")) 
	                    + "_" + String.format("%d", i + 1) + ".pdf";
	                
//	                String nomeArquivoPagina = "src/resources/temp/"
//	    					+ diario.getLocal() + "/" + date[0] + "_" + date[1] + "_"
//	    					+ date[2] + "_" + diario.getLocal() + "_" + secao + ".pdf";
//	                
//	                String nomeArquivoPagina = "src/resources/temp/"
//							+ diario.getLocal() + "/" + date[0] + "_" + date[1]
//							+ "_" + date[2] + "_" + diario.getLocal() + "_" + secao
//							+ "_" + pagina + ".pdf";
	                
//	                String outFile = inFile.substring(0, inFile.indexOf(".pdf")) 
//		                    + "-" + String.format("%03d", i + 1) + ".pdf";
	                
	                
	                
	                //System.out.println ("Writing " + outFile);
	                Document document = new Document(reader.getPageSizeWithRotation(1));
	                PdfCopy writer = new PdfCopy(document, new FileOutputStream(outFile));
	                
	                document.open();
	                
	                PdfImportedPage page = writer.getImportedPage(reader, ++i);
	                writer.addPage(page);
	                
	                document.close();
	                
	                writer.close();	                
	                
	            }
	            return n;
	            
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		 
		 	return 0;

	}

}
