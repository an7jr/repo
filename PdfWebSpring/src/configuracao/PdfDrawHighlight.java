package configuracao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import configuracao.*;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfDrawHighlight {
	
	public static void createHighlightedPDF(List<PDAnnotationTextMarkup> array , String color, String doc) {
        try {
            //PdfReader pdfReader = new PdfReader(Config.getAbsolutePath("/resources/temp/result3.pdf"));
            PdfReader pdfReader = new PdfReader(Config.getJbossDataLocation("/"+doc));
            
        	//PdfReader pdfReader = new PdfReader("C:/Users/Yaissa/Desktop/pd/result2.pdf");

        	
            PdfStamper pdfStamper;
            
            //PdfStamper pdfStamper = new PdfStamper(pdfReader,new FileOutputStream(Config.getAbsolutePath("/resources/temp/result4.pdf")));
            if(color.equalsIgnoreCase("yellow"))  pdfStamper = new PdfStamper(pdfReader,new FileOutputStream(Config.getJbossDataLocation("/result4.pdf")));
            else  pdfStamper = new PdfStamper(pdfReader,new FileOutputStream(Config.getJbossDataLocation("/result5.pdf")));
            
            Image image = Image.getInstance(Config.getJbossDataLocation("/"+color+"_rectangle.jpg"));
 
            for(int i=1; i<= pdfReader.getNumberOfPages(); i++){
 
                //put content under
                PdfContentByte content = pdfStamper.getUnderContent(i);
                
                for(PDAnnotationTextMarkup annotation : array){
                
//                	float lx = 196.50955f;
//                	float ux = 252.45505f;
//                	float ly = 1012.4036f;
//                	float uy = 1027.302f; 
                	image.setAbsolutePosition(annotation.getRectangle().getLowerLeftX(), annotation.getRectangle().getLowerLeftY());
                	//image.scaleAbsolute(ux-lx, uy-ly);
                	image.scaleAbsolute(annotation.getRectangle().getUpperRightX() - annotation.getRectangle().getLowerLeftX(), (annotation.getRectangle().getUpperRightY() - annotation.getRectangle().getLowerLeftY())*0.5f);
                	//image.setAbsolutePosition(lx, ly);
                	content.addImage(image);
                }
                
                
                //Text over the existing page
               
 
            }
 
            pdfStamper.close();
            pdfReader.close();
            
 
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
	
}