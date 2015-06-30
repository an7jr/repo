package controle.beans;


import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import modelo.classes.IndexItem;
import modelo.classes.SearchResult;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryTermExtractor;
import org.apache.lucene.search.highlight.WeightedTerm;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import Indexacao.classes.DownloadDOU;
import bsh.This;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import configuracao.App;
import configuracao.Config;
import controle.classes.Searcher;
import controle.classes.SendEmail;


@SessionScoped
@ManagedBean
public class SearchBean {

    private ArrayList<SearchResult> results = null;
    private ArrayList<SearchResult> currentResults = null;
    private String searchValue;
    private String email;
    private boolean sent = false;
    private int pageRange;
    private int middleNumber;
    private int selectedNumber;
    private int lastPage;
    private List<Integer> options;
   
    
    public SearchBean() {
		
    	
    	
		//lastPage = 10;
	}
   
   
   	 //metodo utilizado em search.xhtml
    //retorna uma busca simples na pagina
   public String doSearch(){
	 //System.out.println(System.getProperty("user.dir")+"\\src\\index");
	   
	  
	   
	   try{
		   Searcher searcher = new Searcher(Config.INDEX_DIR);
	       this.results = searcher.findByContent(this.searchValue, Integer.parseInt(Config.DEFAULT_RESULT_SIZE));
	       
	       System.out.println(this.results.get(0).getData());
	       
	      	       
	       options = new ArrayList<Integer>();
	       selectedNumber = 1;
	       pageRange = 9;
	       lastPage = results.size()/pageRange + 1;
	       //this.middleNumber = pageRange/2;
	       this.currentResults = new ArrayList<SearchResult>();
//	       if(this.results.size() < pageRange){
//	    	   this.middleNumber = pageRange/2;
//	    	   for(int i = 0; i<this.results.size();i++){
//	    		   this.options.add(i + 1);
//	    		   this.currentResults.add(this.results.get(i+1));
//	    	   }
//	       }
//	       else{
//	    	   for(int i = 0; i<pageRange;i++){
//	    		   this.middleNumber = pageRange/2;
//	    		   this.options.add(i+1);
//	    		   this.currentResults.add(this.results.get(i+1));
//	    	   }
//	       }
	       
	       //acabou
	       
	       if(this.results.size() > pageRange){
	    	   this.middleNumber = pageRange/2 + 1;
	    	   if(lastPage >= pageRange){
	    		   for(int i = 0; i<pageRange;i++){
	    			   this.middleNumber = pageRange/2 + 1;
	    			   this.options.add(i+1);
	    			   //this.currentResults.add(this.results.get(i+1));
	    		   }
	    	   }
	    	   else{
	    		   for(int i = 0; i<lastPage;i++){
	    			   this.middleNumber = lastPage/2 + 1;
	    			   this.options.add(i+1);
	    			   //this.currentResults.add(this.results.get(i+1));
	    		   }
	    	   }
	       }	       
	       
	       currentResults = getPageList(selectedNumber - 1);
	       
	       
	   }catch(Exception e){
		  e.printStackTrace();
	   }
	   
	   
	   return "search.xhtml";
        
   }
   
   public ArrayList<SearchResult> getPageList(int page){
	   ArrayList<SearchResult> sresults = new ArrayList<SearchResult>();
	   
	   
	   for(int i= page * 10 ; i<page * 10 + 10 && i<results.size();i++){
		   sresults.add(results.get(i));
	   }
	   
	   return sresults;
   }
   
   //metodo utilizado em sendEmail.xhtml
   //envia email se houver resultado na busca, nao coloca resultados na pagina
   public String doSearchAndEmail(){
	  
	   String content = null;
	   boolean hasContent = false;
	   
	   try{
		   Searcher searcher = new Searcher(Config.INDEX_DIR);
	       this.results = searcher.findByContent(this.searchValue, Integer.parseInt(Config.DEFAULT_RESULT_SIZE));
	       
	       if(results.size() > 0){
	    	   hasContent = true;
	       }
	   }catch(Exception e){
		  e.printStackTrace();
	   }
	  
	   if(hasContent){
		   content = createHTMLstring(this.results);
		   
		   //SendEmail(para,de)
		   SendEmail sendEmail = new SendEmail(this.email,"visoestester@gmail.com");
		   if(sendEmail.sendHTMLMessage("Resultados da busca", content))
			   this.sent = true;
	   }
	  
	   	   
	   return "searchEmail.xhtml";
        
   }
   
   public boolean hasSecao(String r){	   
	   if(r == null) return false;
	   else if(r.trim().length()== 0 ) return false;
	   else return true;
   }
   
   
 //formata os resultados em lista (html)
   private String createHTMLstring(ArrayList<SearchResult> results){
	   StringBuilder html = new StringBuilder();
	   html.append("<p> Sua busca apareceu em: </p><br/><ul>");
	   for(SearchResult re : results){
		   String linkPagina = "<a href='http://pesquisa.in.gov.br/imprensa/jsp/visualiza/index.jsp?jornal="
	                             + re.getSecao() + "&pagina="+ re.getPagina() + "&data=" + re.getData() + "'>"
	                             		+ " Diário Oficial da União " + re.getData() + " Seção " + re.getSecao() 
	                             		+ " Página " + re.getPagina() + "</a>";
		   
		   html.append("<li>");
		   html.append(linkPagina);
		   html.append("<ul>");
		   //for(String fragmento : re.getFragments() ){
			   html.append("<li>");
			   
			   html.append(re.getFragmentos());
			   
			   html.append("</li>");
		   //}
		   html.append("</ul>");
		   
		   html.append("</li>");
	   }
	   html.append("</ul>");
	   return html.toString();
   }
   
public String btnProx(){
	   
	   
	   return changeToPage(selectedNumber + 1);
}
   
public String btnAnt(){
	   
	   
	   return changeToPage(selectedNumber - 1);
}

public boolean first(){
	
	if(selectedNumber == 1) return true;
	
	return false;
}

public boolean last(){
	
	if(selectedNumber == results.size()/pageRange + 1) return true;
	
	return false;
}
   
   public String changeToPage(int page){
	   //this.selectedNumber = page;
	   
	  
	   if(page == this.selectedNumber){
		   this.selectedNumber = page;
		   
		   
		   return "search.xhtml";	 
	   }
	   this.selectedNumber = page;
	   int deslocamento = 0;
	   
	   middleNumber = (options.get(0) + options.get(options.size() - 1))/2 ;
	   
	   if(page > pageRange/2 &&  options.get(options.size() -1) < results.size() - pageRange/2 && options.size() == pageRange){
		   if(page != middleNumber){
			   deslocamento = page - middleNumber;			   
		   }
	   }
	   else if(page <= middleNumber ) {
		   deslocamento = -1 * (options.get(0) -1);
		   
		   //return "search.xhtml";
	   }
	   else {
		   deslocamento = 0;
		   
	   }
//	   if(page > this.middleNumber && page > 5  && this.options.size() == 10|| page < this.middleNumber && page < results.size() - 5){
//		   deslocamento = page - this.middleNumber;
//		   middleNumber = page;
//	   }
//	   else{
//		   System.out.println("entrei 3");
//		   return "search.xhtml";
//	   } 
	   //boolean limit = false;
	   
	   
	   
	  
	   
	   for(int i = 0; i<options.size();i++){
		   
		   options.set(i, options.get(i) + deslocamento);		   
	   }
	   
	  
	   
	   if(options.get(options.size() - 1) > lastPage){   
		  
	
		   
		   if(results.size() > pageRange){
			   
			
			   
			   for(int i = 0; i<pageRange;i++){
				   options.set(i, lastPage-pageRange+i+1);	
			   }
		   }
		   
		   else{		
			   
			   for(int i = 0; i<results.size();i++){
				   options.set(i, results.size() - i);	
			   }
		   }
		   
		   middleNumber = options.get(pageRange/2);
		   
	   }
	   
	   
	   //deslocamento = (this.selectedNumber - 1)*pageRange;
	   
//	   for(int i = 0; i< currentResults.size(); i++){
//		   currentResults.set(i, results.get(deslocamento + i));
//	   }
	   
	   
	   
	  
//	   else if(page < this.middleNumber && page > 5){
//		   deslocamento = page - this.middleNumber;
//	   }
	   
	   currentResults = getPageList(selectedNumber - 1);
	   
	   
	   return "search.xhtml";
   }
   
  /* private String formatDate(String date){
		String fDate;
		
		String[] dateArray = date.split("-");
		fDate = dateArray[2]+"/"+dateArray[1]+"/"+dateArray[0];
				
		return fDate;
		
	}*/
   
   public String dateConvert(String d){
	   String []date = d.split("/");
	   return (Integer.parseInt(date[0])) + " de " + DownloadDOU.getMonthName(Integer.parseInt(date[1]) - 1) + " de " + date[2];
   }
   
//   public void printPDF() {
//
//	 byte[] pdf = convertDocToByteArray("src/resources/temp/result.pdf");
//
//	FacesContext faces = FacesContext.getCurrentInstance();
//	          HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
//	          response.setContentType("application/pdf");
//	          response.setContentLength(pdf.length);
//	          response.setHeader("Content-disposition", "inline");
//
//	          response.setHeader("Cache-Control", "cache, must-revalidate");
//	          response.setHeader("Pragma", "public");
//
//	     try {
//	               ServletOutputStream out = response.getOutputStream();
//	               out.write(pdf);
//	          } catch (IOException e) {
//	               //log.debug("IOException in viewPdf: " + e.toString());
//	          }
//
//	          StateManager stateManager = (StateManager) faces.getApplication().getStateManager();
//	          stateManager.saveSerializedView(faces);
//
//	          faces.responseComplete();
//
//	}
//   
//   public static byte[] convertDocToByteArray(String sourcePath) {
//
//	      byte[] byteArray=null;
//	            try {
//	                  FileInputStream inputStream = new FileInputStream(sourcePath);
//
//
//	                  String inputStreamToString = inputStream.toString();
//	                  byteArray = inputStreamToString.getBytes();
//
//	                  inputStream.close();
//	            } catch (FileNotFoundException e) {
//	                 System.out.println("File Not found"+e);
//	            } catch (IOException e) {
//	            	System.out.println("IO Ex"+e);
//	            }
//	            return byteArray;
//	  }      
   
   
	public ArrayList<SearchResult> getResults() {
		return results;
	}	
	
	public void setResults(ArrayList<SearchResult> results) {
		this.results = results;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public List<Integer> getOptions() {
		return options;
	}

	public void setOptions(List<Integer> options) {
		this.options = options;
	}

	public int getSelectedNumber() {
		return selectedNumber;
	}

	public void setSelectedNumber(int selectedNumber) {
		this.selectedNumber = selectedNumber;
	}

	public ArrayList<SearchResult> getCurrentResults() {
		return currentResults;
	}

	public void setCurrentResults(ArrayList<SearchResult> currentResults) {
		this.currentResults = currentResults;
	}
  
	public int getLastPage() {
		return lastPage;
	}
	
	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	
	
}
