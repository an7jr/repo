package Indexacao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import Indexacao.classes.EmailUtils;

public class DownloadIndexEmailThread extends TimerTask {
	
	   
	public void run() {
		System.out.println("Iniciando indexacao, busca e envio de email.");
		
		Date date = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	String startDate = "12/11/2014";
	    String endDate = sdf.format(date);
	    	
		EmailUtils.downloadAndIndex(startDate, endDate);
		EmailUtils.groupSearchMailToList();
       
        
    }
			
	   
}
