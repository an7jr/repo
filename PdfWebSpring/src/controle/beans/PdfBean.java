package controle.beans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import modelo.classes.IndexItem;
import modelo.classes.SearchResult;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryTermExtractor;
import org.apache.lucene.search.highlight.WeightedTerm;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.hibernate.hql.internal.classic.HavingParser;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import configuracao.App;
import configuracao.Config;
import controle.classes.Searcher;

@ManagedBean
@SessionScoped
public class PdfBean {

	private boolean highlightedText;
	private boolean highlightedTerm;
	private String selectedTitle;
	private boolean highlightedTitle;
	private  List<String> titles;
	private List<PDAnnotationTextMarkup> arrayText;
    private List<PDAnnotationTextMarkup> arrayTerm;
    private List<PDAnnotationTextMarkup> arrayTermEmpty;
	private List<PDAnnotationTextMarkup> arrayTextEmpty;
	private HashMap<String ,  List<PDAnnotationTextMarkup> > hashPositions;	
	private List<String> terms;
	
	private String pg;
	private String searchValue;
	private String secao;
	private String data;
	private String local;
	private String parser;
	private String update;	
	private String titulos;

	public void pdfLoad() throws Exception {
			
			
		  if(update.equalsIgnoreCase("true")){
			  
		 parser = "";
		//PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT)
		 highlightedTerm = false;
		 highlightedTitle= false;
    	 highlightedText = true;
		 arrayTermEmpty = new ArrayList<PDAnnotationTextMarkup>();
		 arrayTextEmpty = new ArrayList<PDAnnotationTextMarkup>();
		 System.out.println("executei1");
		 System.out.println("bean criado");	  
			  
		  System.out.println("entrei");	
		  
		  Searcher searcher = new Searcher(Config.INDEX_DIR);
		  parser = "conteudo:("+searchValue+") AND data:("+data+") AND secao:("+secao+") AND pagina:("+pg+") local:("+local+")"; 
		  ArrayList<SearchResult> results = searcher.findByContent(parser, Integer.parseInt(Config.DEFAULT_RESULT_SIZE));
		  
		  
		  
		  String link = results.get(0).getLink();
		  System.out.println(link);
		  
		  System.out.println("search =" + searchValue);
		  System.out.println("pagina ="+ pg);
		
		
		   titles = new ArrayList<String>();
		   
		   String[] array = titulos.split("#");
		   
		   for(String titulo : array){
			   titles.add(titulo);
		   }
//		   titles.add("EXTRATO DE TERMO DE EXECUÇÃO");
//		   titles.add("GABINETE DO MINISTRO");
//		   titles.add("AVISO DE LICITAÇÃO");
		   
		   
		   hashPositions = new HashMap<String, List<PDAnnotationTextMarkup>>();
		   //return "/user/pdfResult.xhtml?faces-redirect=true";
			
		   	System.out.println("Começando a baixar");
		   
			//String nameFile = Config.getAbsolutePath("/resources/temp/pdfResultAux.pdf");
		   	String nameFile = Config.getJbossDataLocation("/pdfResultAux.pdf");
			File file = new File(nameFile);		

			try {
				
				FileUtils.copyURLToFile(new URL(link), file);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
			System.out.println("baixado");
			
			//chamar metodo com numero da pagina
			
		   //k	
		   
			//String outFile = Config.getAbsolutePath("/resources/temp/result3.pdf");
			String outFile = Config.getJbossDataLocation("/result3.pdf");	  
		   
		   PdfReader reader = new PdfReader(nameFile);
		   Document document = new Document(reader.getPageSizeWithRotation(1));	   
		   
	       PdfCopy writer = new PdfCopy(document, new FileOutputStream(outFile));       
	      
	       document.open();
	       
	       PdfImportedPage page = null;
	       
	       if(reader.getNumberOfPages() > 1){
	    	   page = writer.getImportedPage(reader, Integer.parseInt(pg));
	       }
	       else page = writer.getImportedPage(reader, 1);       
	       
	       page.closePath();
	       
	       writer.addPage(page);     
	       
	       document.close();        
	       writer.close();
	       reader.close();
	       
	       String queryString = "" + searchValue + "";
	       
	       StandardAnalyzer analyzer = new StandardAnalyzer();
	       
	       QueryParser contentQueryParser = new QueryParser(IndexItem.CONTEUDO, analyzer);
	   	
	       Query query = contentQueryParser.parse(queryString);      
	       
	       QueryTermExtractor q = new QueryTermExtractor();
	       
	       WeightedTerm[] text = q.getTerms(query, true, "conteudo");
	       
	       
	       WeightedTerm[] terms = new WeightedTerm[titles.size()];
	       for(int i = 0; i<titles.size();i++){
	    	   terms[i] = new WeightedTerm(1, titles.get(i));	    	  
	       }	      
	       
	       List<List<PDAnnotationTextMarkup>> annotations = App.highlight(outFile, text, terms, titles, hashPositions);
	       setArrayText(annotations.get(0));
	       setArrayTerm(annotations.get(1));
	       
	       
	       //return "pdfResult.xhtml";
		}
	}
	
	
	
	public String write(String param1 , String param2){
		
		return" O valor e "+ param1 + ", "+param2;
	}
	
	public String redirect(){
		return  "/user/pdfResult.xhtml?faces-redirect=truelocal="+local+"&data="+data+"&secao="+secao+"&pg="+pg+"&update=false&search="+searchValue;
	}
	
public String highlightTerm() throws FileNotFoundException, IOException{		
		
		if(highlightedText)	App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayText, arrayTerm, "blue");
		else App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, arrayTerm, "blue");
		
		highlightedTitle = false;
		highlightedTerm = true;
		
		return redirect();
   }
   
   public String unHighlightTerm() throws FileNotFoundException, IOException{		
		
		if(highlightedText)	App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayText, arrayTermEmpty, "blue");
		else App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, arrayTermEmpty, "blue");
		
		highlightedTerm = false;
		
		return redirect();
	}
   
   public String highlightText() throws FileNotFoundException, IOException{		
		
		if(highlightedTerm)	App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayText, arrayTerm, "blue");
		else if(!highlightedTerm && highlightedTitle) App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayText, hashPositions.get(selectedTitle), "red");
		else App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayText, arrayTermEmpty, "blue");		
		highlightedText = true;		
		
		return redirect();
   }
   
   public String unHighlightText() throws FileNotFoundException, IOException{		
		
		if(highlightedTerm)	App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, arrayTerm, "blue");
		else if(!highlightedTerm && highlightedTitle) App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, hashPositions.get(selectedTitle), "red");
		else App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, arrayTermEmpty, "blue");		
		highlightedText = false;	
		
		return redirect();
		
  }
   
   public String unHighlightAll() throws FileNotFoundException, IOException{		
		
		System.out.println("entrei no botao");
	   
		App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, arrayTermEmpty, "blue");
		
		highlightedText = false;
		highlightedTerm = false;
		highlightedTitle = false;
		
		return redirect();
  }
   
    public String highlightTitle(String title) throws FileNotFoundException, IOException{
    	
    	//String action = (String)event.getComponent().getAttributes().get("action");    	
    	
    	this.selectedTitle = title;
    	highlightedTitle = true;
    	highlightedTerm = false;    	
    	if(highlightedText) App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayText, hashPositions.get(selectedTitle), "red");
    	else App.highlightPositions(Config.getJbossDataLocation("/result3.pdf"), arrayTextEmpty, hashPositions.get(selectedTitle), "red");    	
    	
    	return redirect();
    }

	public boolean isHighlightedText() {
		return highlightedText;
	}

	public void setHighlightedText(boolean highlightedText) {
		this.highlightedText = highlightedText;
	}

	public boolean isHighlightedTerm() {
		return highlightedTerm;
	}

	public void setHighlightedTerm(boolean highlightedTerm) {
		this.highlightedTerm = highlightedTerm;
	}

	public String getSelectedTitle() {
		return selectedTitle;
	}

	public void setSelectedTitle(String selectedTitle) {
		this.selectedTitle = selectedTitle;
	}

	public boolean isHighlightedTitle() {
		return highlightedTitle;
	}

	public void setHighlightedTitle(boolean highlightedTitle) {
		this.highlightedTitle = highlightedTitle;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<PDAnnotationTextMarkup> getArrayText() {
		return arrayText;
	}

	public void setArrayText(List<PDAnnotationTextMarkup> arrayText) {
		this.arrayText = arrayText;
	}

	public List<PDAnnotationTextMarkup> getArrayTerm() {
		return arrayTerm;
	}

	public void setArrayTerm(List<PDAnnotationTextMarkup> arrayTerm) {
		this.arrayTerm = arrayTerm;
	}

	public List<PDAnnotationTextMarkup> getArrayTermEmpty() {
		return arrayTermEmpty;
	}

	public void setArrayTermEmpty(List<PDAnnotationTextMarkup> arrayTermEmpty) {
		this.arrayTermEmpty = arrayTermEmpty;
	}

	public List<PDAnnotationTextMarkup> getArrayTextEmpty() {
		return arrayTextEmpty;
	}

	public void setArrayTextEmpty(List<PDAnnotationTextMarkup> arrayTextEmpty) {
		this.arrayTextEmpty = arrayTextEmpty;
	}	

	public HashMap<String, List<PDAnnotationTextMarkup>> getHashPositions() {
		return hashPositions;
	}

	public void setHashPositions(
			HashMap<String, List<PDAnnotationTextMarkup>> hashPositions) {
		this.hashPositions = hashPositions;
	}	

	public String getPg() {
		return pg;
	}

	public void setPg(String pg) {
		this.pg = pg;		
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {	
		
		this.searchValue = searchValue;		
		
	}

	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
		
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		String[] d = data.split("/");
		if(d.length>1){
		this.data = d[2]+""+d[1]+""+d[0];
		
		}
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {		
		this.local = local;		
	}

	public String getParser() {
		return parser;
	}

	public void setParser(String parser) {
		this.parser = parser;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	public String getTitulos() {
		return titulos;
	}

	public void setTitulos(String titulos) {
		this.titulos = titulos;
	}	
	
}
