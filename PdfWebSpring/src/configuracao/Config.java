package configuracao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class Config {

	public static String INDEX_DIR;
	public static String DEFAULT_RESULT_SIZE;
	public static String TIME_TO_WAIT;
	public static String TIME_HOUR;
	public static String TIME_MINUTES;
	public static String TIME_SECONDS;
	
	
	static{
		
		try {
			System.out.println("CARREGANDO CONFIGURAÇÕES DO SISTEMA");
			Properties props = new Properties();
			
			URL url = new URL("file:///" + padronizaDiretorio(System.getProperty("jboss.server.config.dir") + "/config.properties")); 
			props.load(url.openStream());
			System.out.println("ARQUIVO DE CONFIGURAÇÃO CARREGADO. ATRIBUINDO VALORES");
		
		
			INDEX_DIR = props.getProperty("INDEX_DIR");
			DEFAULT_RESULT_SIZE = props.getProperty("DEFAULT_RESULT_SIZE");		
			TIME_TO_WAIT = props.getProperty("TIME_TO_WAIT");
			TIME_HOUR = props.getProperty("TIME_HOUR");
			TIME_MINUTES = props.getProperty("TIME_MINUTES");
			TIME_SECONDS = props.getProperty("TIME_SECONDS");
			
			
			System.out.println("-----------------------------");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String padronizaDiretorio(String diretorio){
		String diretorioPadronizado = diretorio.replaceAll("[\\\\/]+", "/");
		return diretorioPadronizado;
	}
	
	public static String getAbsolutePath(String path){
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = facesContext.getExternalContext();
	    
	    String relativeWebPath = path;
	    ServletContext servletContext = (ServletContext)externalContext.getContext();
	    String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
		
	    return absoluteDiskPath;
	}
	
	public static String getJbossDataLocation(String path){
		String dataPath = System.getProperty("jboss.server.data.dir");
		
		return dataPath + path;
		
	}
	
}





