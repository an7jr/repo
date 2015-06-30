package Indexacao;



import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import configuracao.Config;




public class DownloadIndexEmail  implements ServletContextListener  {
	
	Timer timer = new Timer();
	
	public void contextDestroyed(ServletContextEvent event) {
		
		System.out.println("Finalizando thread de indexacao");
		
		timer.cancel(); 
		
		
		
	}

	public void contextInitialized(ServletContextEvent event) {
		
		System.out.println("Iniciando thread de indexacao");
		
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Config.TIME_HOUR));
        calendar.set(Calendar.MINUTE, Integer.parseInt(Config.TIME_MINUTES));
        calendar.set(Calendar.SECOND, Integer.parseInt(Config.TIME_SECONDS));
        Date startTime = calendar.getTime();
        
       
	    timer.schedule(new DownloadIndexEmailThread(),startTime,Integer.parseInt(Config.TIME_TO_WAIT));
	    
        
        //timer.schedule(new DownloadIndexEmailThread(),20*1000,86400000);
		
		
		
		
	}
	


}

