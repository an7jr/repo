package Diarios;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import modelo.classes.IndexItem;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.server.browserlaunchers.Sleeper;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import Indexacao.classes.DownloadDOU;
import Indexacao.classes.Indexer;
import configuracao.Config;
import configuracao.SplitPDFFile;

public class EspiritoSantoDO {
	
	public static void Download(String data, WebDriver driver) throws InterruptedException {
		// TODO Auto-generated method stub
		
		String[] d = data.split("/");
		String dia = d[0];
		String mes = d[1];
		String ano = d[2];
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		String currentDate = dateFormat.format(cal.getTime());
		
		
		WebDriverWait wait = new WebDriverWait(driver, 30);
		driver.get("https://dio.es.gov.br/portal/visualizacoes/diario_oficial");	
		
		Thread.sleep(2000);
		
		if(driver.findElements(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")).isEmpty()) return ;
		//wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")));	
		
		String diaAtual = dateFormat.format(cal.getTime()).split("/")[0];
		String mesAtual = dateFormat.format(cal.getTime()).split("/")[1];
		String anoAtual = dateFormat.format(cal.getTime()).split("/")[2];
		
		
		
		driver.findElement(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")).sendKeys(diaAtual+""+mesAtual+""+anoAtual);
		driver.findElement(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")).sendKeys(Keys.TAB);		
		
		Thread.sleep(500);
		
		String linkAntigo = driver.findElement(By.xpath(".//a[contains(@id, 'baixar-diario-completo')]")).getAttribute("href");
		
		driver.get("https://dio.es.gov.br/portal/visualizacoes/diario_oficial");		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")));	
		
		
		driver.findElement(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")).sendKeys(dia+""+mes+""+ano);
		driver.findElement(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")).sendKeys(Keys.TAB);
		
		Thread.sleep(1000);		
		
		
		String link = driver.findElement(By.xpath(".//a[contains(@id, 'baixar-diario-completo')]")).getAttribute("href");
		
		System.out.println(linkAntigo);
		System.out.println(link);
		
		if(!(currentDate.equalsIgnoreCase(dia+"/"+mes+"/"+ano) || !(linkAntigo.equalsIgnoreCase(link)))) return ;		
		
		String nomeArquivoPagina = "src/resources/temp/EspiritoSanto/"
				 + dia + "_" + mes + "_"
				+ ano + "_EspiritoSanto_.pdf";		
		
		
		File file = new File(nomeArquivoPagina);
		
		try {			
			FileUtils.copyURLToFile(new URL(link), file);
			
			int nPages = SplitPDFFile.splitPDF(nomeArquivoPagina);	
			
			for (int pagina = 1; pagina <= nPages; pagina++) {
				
				String secao = "";
				
				String nomePag = nomeArquivoPagina.substring(0, nomeArquivoPagina.indexOf(".pdf")) 
	                    + "_" + String.format("%d", pagina) + ".pdf";;
				
				PDDocument doc = PDDocument.load(nomePag);

				PDFTextStripper stripper = new PDFTextStripper();

				try {					
					
					System.out.println("Indexando " + "Espírito Santo"
							+ "" + data + " - Seção " + secao + "..."
							+ " Página " + pagina);
					System.out.println(nomePag);
					
					
					String content = stripper.getText(doc);

					IndexItem pdfIndexItem = new IndexItem(
							(long) nomePag.hashCode(),
							DownloadDOU.formatDateToFileName(data), String.valueOf(secao),
							content, String.valueOf(pagina), String.valueOf("Espírito Santo"), link);
					Indexer indexer = new Indexer(Config.INDEX_DIR);
					indexer.index(pdfIndexItem);
					indexer.close();
					
					//new File(nomePag).delete();
					
				} catch (Exception e) {
					System.out.println("Falha na indexação "
							+ "Espírito Santo" + " " + data + " - Seção "
							+ secao);
				} finally {
					doc.close();
				}
			}
			
			new File(nomeArquivoPagina).delete();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
