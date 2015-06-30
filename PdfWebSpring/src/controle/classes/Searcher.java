package controle.classes;

import modelo.classes.IndexItem;
import modelo.classes.SearchResult;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.QueryTermExtractor;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.WeightedTerm;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class Searcher {
 
    private IndexSearcher searcher;
    private QueryParser contentQueryParser;
    private StandardAnalyzer analyzer;

    public Searcher(String indexDir) throws IOException {
       
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexDir))));
        analyzer = new StandardAnalyzer();  
        //busca sera feita pelo campo content
        contentQueryParser = new QueryParser(IndexItem.CONTEUDO, analyzer);
        
    }
        
    //metodo utilizado nas paginas de search
    //busca exatamente a expressao digitada
    public ArrayList<SearchResult> findByContent(String queryString, int numOfResults) throws ParseException, IOException {
    	SearchResult sr;
    	queryString = "" + queryString + "";
    	
        Query query = contentQueryParser.parse(queryString);
        
       
       
       //a
        
        
       ScoreDoc[] queryResults = searcher.search(query, numOfResults).scoreDocs;        
       int total = queryResults.length;      
       ArrayList<SearchResult> result = new ArrayList<SearchResult>();
        
       if(total > 0){   
    	   
    	 Formatter formatter = new SimpleHTMLFormatter("<b>", "</b>");
    	   //Formatter formatter = new SimpleHTMLFormatter("","");
	       QueryScorer queryScorer = new QueryScorer(query);       
	       
	       Highlighter highlighter = new Highlighter(formatter, queryScorer);
	       highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, 300));
	       highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
	       
	       TokenStream stream;
	       String[] fragmentos;
	       String conteudo;
	       String data;
	       String secao;
	       String pagina;
	       String local;
	       String link;
	       Document doc;
	       
    	   for(int i = 0 ; i< total; i++){
           	doc = searcher.doc(queryResults[i].doc);
           	conteudo = doc.get("conteudo");
           	secao = doc.get("secao");
           	data = doc.get("data");           	
           	pagina = doc.get("pagina");
           	local = doc.get("local");
           	link = doc.get("link");
           	
           	try{
           		String frag = "";
           		
   	        	stream =  analyzer.tokenStream("conteudo", new StringReader(conteudo));
    	        	 
   	        	fragmentos =  highlighter.getBestFragments(stream, conteudo, 1);

   	        	if(fragmentos.length > 0) frag = fragmentos[0];
   	        	
   	        	
   	        	else{
   	        		
   	        		if(conteudo.length() > 100) frag = conteudo.substring(0, 300) + "...";
   	        		else frag = conteudo;
   	        	}
   	        	
   	        	data = this.formatDateIndexToShow(data);
   	        	
   	        	sr = new SearchResult(data,secao,pagina,frag, local, link);
   	        	
   	        	result.add(sr);
   	        	
   	        	 
           	}catch(Exception e){
           		e.printStackTrace();
           	}
           	
          }
       }

       return result;
    }
    
    //metodo utilizado no envio dos emails
    //busca exatamente a expressao desejada
    //na data atual
    public ArrayList<SearchResult> findByContentAndDate(String queryString, int numOfResults) throws ParseException, IOException {
    	
    	SearchResult sr;
    	Date hoje = new Date();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    	
		String dataHoje = dateFormat.format(hoje);
		
		queryString = "data:\"" + dataHoje + "\"" + " AND conteudo: \"" + queryString + "\"";
    	
    	
        Query query = contentQueryParser.parse(queryString);      
        ScoreDoc[] queryResults = searcher.search(query, numOfResults).scoreDocs;        
        int total = queryResults.length;      
        ArrayList<SearchResult> result = new ArrayList<SearchResult>();
        
       if(total > 0){   
    	   
    	   Formatter formatter = new SimpleHTMLFormatter("<b>", "</b>");
    	   //Formatter formatter = new SimpleHTMLFormatter("","");
	       QueryScorer queryScorer = new QueryScorer(query);
	       Highlighter highlighter = new Highlighter(formatter, queryScorer);
	       highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer,300));
	       highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
	       
	       TokenStream stream;
	       String[] fragmentos;
	       String conteudo;
	       String data;
	       String secao;
	       String pagina;
	       String local;
	       String link;
	       
	       Document doc;
	       
    	   for(int i = 0 ; i< total; i++){
           	doc = searcher.doc(queryResults[i].doc);
           	conteudo = doc.get("conteudo");
           	secao = doc.get("secao");
           	data = doc.get("data");           	
           	pagina = doc.get("pagina");
           	local = doc.get("local");
           	link = doc.get("link");
           	
           	try{
           		String frag = "";
           		
   	        	stream =  analyzer.tokenStream("conteudo", new StringReader(conteudo));
    	        	 
   	        	fragmentos =  highlighter.getBestFragments(stream, conteudo, 1);
   	        	
   	        	if(fragmentos.length > 0) frag = fragmentos[0];  	        	
   	        	
   	        	else{
   	        		
   	        		if(conteudo.length() > 100) frag = conteudo.substring(0, 300) + "...";
   	        		else frag = conteudo;
   	        	}
   	        	
   	        	
   	        	data = this.formatDateIndexToShow(data);
   	        
   	        	sr = new SearchResult(data,secao,pagina,frag, local, link);
   	        	
   	        	result.add(sr);
   	        	
   	        	 
           	}catch(Exception e){
           		e.printStackTrace();
           	}
           	
          }
       }

       return result;
    }
    
    public String formatDateIndexToShow(String date){
    	String date1 = date.substring(6,8)  + "/" + date.substring(4,6) + "/" + date.substring(0,4) ;
    	
    	
    	return date1;
    }
    
  
}