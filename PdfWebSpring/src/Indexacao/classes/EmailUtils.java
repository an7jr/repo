package Indexacao.classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import modelo.classes.SearchResult;
import modelo.dao.CadastroBuscaDAO;
import modelo.entidades.CadastroBusca;
import modelo.entidades.DiarioOfficial;
import modelo.entidades.Jornal;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import Diarios.AlagoasDO;
import Diarios.DistritoDO;
import Diarios.EspiritoSantoDO;
import Diarios.RondoniaDO;
import Diarios.RioGrandeDoSulDO;
import Diarios.SantaCatarinaDO;
import configuracao.Config;
import controle.beans.IndexBean;
import controle.classes.Searcher;
import controle.classes.SendEmail;

import org.apache.commons.io.FileUtils;


import org.openqa.selenium.phantomjs.PhantomJSDriver;




public class EmailUtils {
		
	private static EntityManager manager = null;
	private static String ConnectErrorMessage = "";
	//envios de email estao usando a data de validade dos termos, se o termo nao tem data de validade, esta ativo tambem
	
	
	//envia uma mensagem diferente para cada entrada na tabela de Cadastro de buscas (varios emails por usuario)
	public static void mailToList(){
    	
    	EntityManager manager = getManager();
    	CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
    	
    	List<CadastroBusca> cadastrados = new ArrayList<CadastroBusca>();
    	cadastrados = repo.getLista();
    	
    	for(CadastroBusca cb : cadastrados){
    		Date hoje = new Date();
			
    		if(cb.getDataValidade() == null || hoje.before(cb.getDataValidade())){
    			doSearchAndEmail(cb.getEmail(),cb.getTermo());
    		}
  		
    		
    	}
    	
    	
    }
    
 	
    //agrupa os termos de cada email distinto e envia uma mensagem com todos os termos para cada, se houver resultado
	public static void groupSearchMailToList(){
	    EntityManager manager = getManager();
    	CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
    	
    	List<CadastroBusca> cadastrados = new ArrayList<CadastroBusca>();
    	cadastrados = repo.getLista();
    	
    	Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
    	Date hoje = new Date();
    	for(CadastroBusca cb : cadastrados){
    		if(map.containsKey(cb.getEmail())){
    			if(cb.getDataValidade() == null || hoje.before(cb.getDataValidade())){
    				map.get(cb.getEmail()).add(cb.getTermo());
    			}
    			
    		}
    		else{
    			if(cb.getDataValidade() == null || hoje.before(cb.getDataValidade())){
    				ArrayList<String> arr =  new ArrayList<String>();
    				arr.add(cb.getTermo());
    				map.put(cb.getEmail(), arr);
    			}
    		}
    		System.out.println(cb.getEmail() + " - " + cb.getTermo());
    	}
    	
    	
    	for (String key : map.keySet()){
    		
    		doMultipleSearchAndEmail(key,map.get(key));
    	}
 
    
    }
    
   
	
    //envia email se houver resultados de busca
	public static void doSearchAndEmail(String email, String termo){
		  
		   ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		   
		   boolean hasContent = false;
		   
		   String content = null;
		   
		   try{
			   Searcher searcher = new Searcher(Config.INDEX_DIR);
			   results = searcher.findByContentAndDate(termo, Integer.parseInt(Config.DEFAULT_RESULT_SIZE));
			   
			   if(results.size() > 0){
				   hasContent = true;
				   content = createHTMLstring(results);
			   }
		   }catch(Exception e){
			  e.printStackTrace();
		   }
		  
		   
		   if(hasContent){
			 //SendEmail(para,de)
			   SendEmail sendEmail = new SendEmail(email,"visoestester@gmail.com");
			   sendEmail.sendHTMLMessage("Resultados de busca", content);
				 
		   }
		   
		   
		  
	   }
	
	//junta todos os resultados de todos os termos e envia email se ao menos um dos termos tiver resultado
	public static void doMultipleSearchAndEmail(String email, List<String> termo){
		  
		   ArrayList<SearchResult> results;
		   StringBuilder content = new StringBuilder(); 
		   boolean hasContent = false;
		   
		   for(String tr : termo){
			   results = new ArrayList<SearchResult>();
			   
			   try{
				   Searcher searcher = new Searcher(Config.INDEX_DIR);
				   results = searcher.findByContentAndDate(tr, Integer.parseInt(Config.DEFAULT_RESULT_SIZE));
				   
				   if(results.size() > 0){
					   hasContent = true;
					   
					   content.append("<h3>Termo de busca: <i> " + tr + "</i></h3>");
					   
					   content.append(createHTMLstring(results) + "<br/><br/>");
				   }
				   
				   
				   
			   }catch(Exception e){
				  e.printStackTrace();
			   }
			  
			 
		   }
		   
		   if(hasContent){
			 //SendEmail(para,de)
			   SendEmail sendEmail = new SendEmail(email,"visoestester@gmail.com");
			   sendEmail.sendHTMLMessage("Resultados de busca", content.toString());  
			   
		   }
		   
			
		  
	   }
	
	//formata os resultados em lista (html)
	public static String createHTMLstring(ArrayList<SearchResult> results){
		   StringBuilder html = new StringBuilder();
		   html.append("<p> Este termo apareceu em: </p><br/><ul>");
		   for(SearchResult re : results){
			   String linkPagina = "<a href='http://pesquisa.in.gov.br/imprensa/jsp/visualiza/index.jsp?jornal="
		                             + re.getSecao() + "&pagina="+ re.getPagina() + "&data=" + re.getData() + "'>"
		                             		+ " Diário Oficial da União " + re.getData() + " Seção " + re.getSecao() 
		                             		+ " Página " + re.getPagina() + "</a>";
			   
			   html.append("<li>");
			   html.append(linkPagina);
			   html.append("<ul>");
			   //for(String fragmento : re.getFragmentos() ){
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
	   
	/*public static String formatDate(String date){
			String fDate;
			
			String[] dateArray = date.split("-");
			fDate = dateArray[2]+"/"+dateArray[1]+"/"+dateArray[0];
					
			return fDate;
			
		}*/

	public static EntityManager getManager() {
		if(manager == null){
			EntityManagerFactory factory =	Persistence.createEntityManagerFactory("buscaDB");
			manager = factory.createEntityManager();
			
		}
		return manager;
	}
	
	//metodo que realiza download, busca
	public static void downloadAndIndex(String sdate, String edate) {
        
    	DownloadDOU dl = new DownloadDOU(); 
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = null, endDate = null;
        
    	
        try{
	    	startDate = dateFormat.parse(sdate); 
	        endDate = dateFormat.parse(edate); 
	        
	        Calendar start = Calendar.getInstance();
	    	start.setTime(startDate);
	    	Calendar end = Calendar.getInstance();
	    	end.setTime(endDate);
	    	
	    	LocalDate d1 = new LocalDate(start.getTimeInMillis());
	    	LocalDate d2 = new LocalDate(end.getTimeInMillis());
	    	int days = Days.daysBetween(d1, d2).getDays();
	    	days += 1;
	    	
	    	days *=3;
	    	
	        float counter = 0;
	        
	        
	        manager = EmailUtils.getManager();
	        Query query = manager.createQuery("SELECT d FROM DiarioOfficial d");
    	 	
	        List<DiarioOfficial> diarios = (List<DiarioOfficial>)query.getResultList();
	        
	        ////
//	        System.out.println("removido "+ diarios.get(0).getLocal());
//	        diarios.remove(0);
//	        System.out.println("removido "+ diarios.get(0).getLocal());
//	        diarios.remove(0);
//	        System.out.println("removido "+ diarios.get(0).getLocal());
//	        diarios.remove(0);
//	        System.out.println("removido "+ diarios.get(1).getLocal());
//	        diarios.remove(1);
//	        System.out.println("removido "+ diarios.get(1).getLocal());
//	        diarios.remove(1);
//	        System.out.println("removido "+ diarios.get(1).getLocal());
//	        diarios.remove(1);
//	        System.out.println("removido "+ diarios.get(1));
//	        diarios.remove(1);
	        ////
	        
	        
	        //DiarioOfficial diario = diarios.get(1);
	        
	        
    		
    		
    		
	    	
	    	for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
	    	 	System.out.println(dateFormat.format(date));
	    	 	
	    	 	DesiredCapabilities cap = new DesiredCapabilities();
	    		cap.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:/Users/Yaissa/Desktop/phantomjs-2.0.0-windows/bin/phantomjs.exe");
	    		
	    		WebDriver driver = new PhantomJSDriver(cap);
	    		//WebDriver driver = new FirefoxDriver();
	    	 	
	    	 	IndexBean.status = (float) 3; 
	    	 	
	    	 	String data = dateFormat.format(date);
	    	 	
	    	 	AlagoasDO.Download(dateFormat.format(date), driver);
	    	 	DistritoDO.Download(data, driver);
	    	 	SantaCatarinaDO.Download(data, driver);
	    	 	RondoniaDO.Download(data, driver);
	    	 	EspiritoSantoDO.Download(data, driver);    	 	
	    	 	//RioGrandeDoSulDO.Download(data, driver);
	    	 	
	    		driver.quit();
	    	 	//dl.downloadDiarioAndIndex(dateFormat.format(date), "Rio Grande do Sul" , diario);
	    	 	
	    	 	for(DiarioOfficial diario : diarios){
	    	 		
	    	 		if(diario.getJornais().isEmpty()){
	    	 			System.out.println(diario.getLocal());
	    	 			if(diario.getPaginado()){
    	 					dl.downloadDiarioAndIndex(dateFormat.format(date), "" , diario);	    	 				
    	 				}
    	 				else{
    	 					dl.downloadDiarioAndIndexNoPag(dateFormat.format(date), "" , diario);	    	 				
    	 				}	    	 			
    	 				if(!ConnectErrorMessage.equals("")){
    	 					System.out.println("Download falhou para "+ diario.getLocal() +" "+ dateFormat.format(date));
    	 				}
	    	 		}
	    	 		else{
	    	 			for(Jornal jornal : diario.getJornais()){	    	 			
	    	 				if(diario.getPaginado()){
	    	 					dl.downloadDiarioAndIndex(dateFormat.format(date), jornal.getNome() , diario);	    	 				
	    	 				}
	    	 				else{
	    	 					dl.downloadDiarioAndIndexNoPag(dateFormat.format(date), jornal.getNome(), diario);	    	 				
	    	 				}	    	 			
	    	 				if(!ConnectErrorMessage.equals("")){
	    	 					System.out.println("Download falhou para "+ diario.getLocal() +" "+ dateFormat.format(date) + " secao " + jornal.getNome());
	    	 				}
	    	 			}
	    	 		}
	    	 	}    		
	    		
	    		
	    	 	/*
	    	 	//Dowload secao 1
	    		
	    	 	dl.downloadDiarioAndIndex(dateFormat.format(date), 1);
	    	 	if(!ConnectErrorMessage.equals("")){
	    	 		System.out.println("Download falhou para DOU "+ dateFormat.format(date) + " secao 1" );
	    	 	}
	    	 	counter++;
	    		IndexBean.status  = (float)(counter/days) * 100 ;
	    		System.out.println(IndexBean.status);
	    		
	    		//Dowload secao 2
	    		dl.downloadDiarioAndIndex(dateFormat.format(date), 2);
	    		if(!ConnectErrorMessage.equals("")){
	    	 		System.out.println("Download falhou para DOU "+ dateFormat.format(date) + " secao 2" );
	    	 	}
	    		counter++;
	    		IndexBean.status  = (float)(counter/days) * 100 ;
	    		System.out.println(IndexBean.status);
	    	    		
	    		//Dowload secao extra
	    		dl.downloadDiarioAndIndex(dateFormat.format(date), 3);
	    		if(!ConnectErrorMessage.equals("")){
	    	 		System.out.println("Download falhou para DOU "+ dateFormat.format(date) + " secao 3" );
	    	 	}
	    		counter++;
	    		IndexBean.status  = (float)(counter/days) * 100 ;
	    		System.out.println(IndexBean.status);
	    		*/
	    		
	    		/*String[] day = dateFormat.format(date).split("/");
				File dir = new File("src/resources/temp/"+day[0] + "/");
				dl.deleteDir(dir);
				dir = new File("src/resources/");
				dl.deleteDir(dir);*/
				
	    	}
	    
                
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
        
        
	}

	
	
	public static String getConnectErrorMessage() {
		return ConnectErrorMessage;
	}


	public static void setConnectErrorMessage(String connectErrorMessage) {
		ConnectErrorMessage = connectErrorMessage;
	}
	
	
	

}
