package configuracao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.lucene.search.highlight.WeightedTerm;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;





//import org.apache.pdfbox.util.PDFTextAnnotator;
 
public class App {	

  //@ManagedProperty(value="#{pdfConfigurationBean}")
  //static PdfConfigurationBean bean; 	
	
	
  static List<List<PDAnnotationTextMarkup>> annotateExample(String fileName,WeightedTerm[] text, WeightedTerm[] terms, List<String> titles, HashMap<String, List<PDAnnotationTextMarkup>> hash) throws Exception {	
	  
    PDDocument pdDoc = null;
    File file = new File(fileName);
 
    if (!file.isFile()) {
      System.err.println("File " + fileName + " does not exist.");
      return null;
    }
    
    PDFParser parser = new PDFParser(new FileInputStream(file));
 
    parser.parse();
    pdDoc = new PDDocument(parser.getDocument());
    
    PDFTextAnnotator pdfAnnotator = new PDFTextAnnotator("UTF-8"); // create new annotator
    //pdfAnnotator.setLineSeparator(""); // kinda depends on what you want to match
    pdfAnnotator.initialize(pdDoc);    
    List<PDAnnotationTextMarkup> arrayTerm = new ArrayList<PDAnnotationTextMarkup>();    
    List<PDAnnotationTextMarkup> arrayText = new ArrayList<PDAnnotationTextMarkup>(); 
    List<List<PDAnnotationTextMarkup>> result = new ArrayList<List<PDAnnotationTextMarkup>>();
    
    String regex = "";
    
    for(int i = 0; i<terms.length;i++)
    	
    {    	
    	regex += terms[i].getTerm() + "|";

    }
    
    arrayTerm = pdfAnnotator.highlight(pdDoc, regex, titles, hash, false);
    
    regex = "";
    
    for(int i = 0; i<text.length;i++)
    {
    	regex += text[i].getTerm() + "|";

    }
    
    arrayText = pdfAnnotator.highlight(pdDoc, regex, titles, hash, true);
    
    result.add(arrayText);
    result.add(arrayTerm);
    //bean.setArrayTerm(arrayTerm);
    //bean.setArrayText(arrayText);
    
    PdfDrawHighlight.createHighlightedPDF(arrayText, "yellow", "result3.pdf");
    PdfDrawHighlight.createHighlightedPDF(new ArrayList<PDAnnotationTextMarkup>(), "blue" , "result4.pdf");
    
    //pdDoc.save("/home/user/result2.pdf");
    pdDoc.close();
    
    try {
      if (parser.getDocument() != null) {
        parser.getDocument().close();
      }
      if (pdDoc != null) {
        pdDoc.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return result;
  }
 
  public static List<List<PDAnnotationTextMarkup>> highlight(String name, WeightedTerm text[], WeightedTerm term[], List<String> titles, HashMap<String, List<PDAnnotationTextMarkup>> hash) throws Exception {
    //annotateExample("C:/Users/Yaissa/Desktop/doe20141125_004.pdf");
    //annotateExample("C:/Users/Yaissa/Desktop/certidao.pdf");
	  
	  return annotateExample(name, text, term, titles, hash);

  }
  
  public static void highlightPositions(String fileName, List<PDAnnotationTextMarkup> arrayText, List<PDAnnotationTextMarkup> arrayTerm, String color) throws FileNotFoundException, IOException{ 	
	  
	  
	  	PDDocument pdDoc = null;
	    File file = new File(fileName);
	 
	    if (!file.isFile()) {
	      System.err.println("File " + fileName + " does not exist.");
	      return;
	    }
	    
	    PDFParser parser = new PDFParser(new FileInputStream(file));
	 
	    parser.parse();
	    pdDoc = new PDDocument(parser.getDocument());
	    
	    PDFTextAnnotator pdfAnnotator = new PDFTextAnnotator("UTF-8"); // create new annotator
	    //pdfAnnotator.setLineSeparator(""); // kinda depends on what you want to match
	    pdfAnnotator.initialize(pdDoc); 
	    
	    PdfDrawHighlight.createHighlightedPDF(arrayText, "yellow", "result3.pdf");
	    PdfDrawHighlight.createHighlightedPDF(arrayTerm, color , "result4.pdf");
	    
	    //pdDoc.save("/home/user/result2.pdf");
	    pdDoc.close();
	    
	    try {
	      if (parser.getDocument() != null) {
	        parser.getDocument().close();
	      }
	      if (pdDoc != null) {
	        pdDoc.close();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  
  }
 
}