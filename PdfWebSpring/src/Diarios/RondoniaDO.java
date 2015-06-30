package Diarios;

import java.io.File;
import java.util.List;

import modelo.classes.IndexItem;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import Indexacao.classes.DownloadDOU;
import Indexacao.classes.Indexer;
import configuracao.Config;
import configuracao.FileDownloader;
import configuracao.SplitPDFFile;

public class RondoniaDO {
	
	public static void Download(String data, WebDriver driver) throws Exception {
		// TODO Auto-generated method stub
		
		String[] d = data.split("/");
		String dia = ""+Integer.parseInt(d[0]);
		String mes = d[1];
		String ano = d[2];
		
		//WebDriver driver = new PhantomJSDriver(cap);
		
		WebDriverWait wait = new WebDriverWait(driver, 30);
		driver.get("http://www.diof.ro.gov.br/");

		//wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//input[contains(@id, 'dataEdicaoPortal')]")));		
		
		Thread.sleep(1000);
		
		if(driver.findElements(By.xpath(".//select[contains(@class, 'ui-datepicker-year')]")).isEmpty()) return ; 
		
		Select selectYear = new Select(driver.findElement(By.xpath(".//select[contains(@class, 'ui-datepicker-year')]")));
		Select selectMonth = new Select(driver.findElement(By.xpath(".//select[contains(@class, 'ui-datepicker-month')]")));
		
		selectYear.selectByValue(ano);
		
		//0 = janeiro
		int m1 = Integer.parseInt(mes);
		int m2 = m1-1;
		
		selectMonth.selectByValue(""+m2);
		
		WebElement dateWidget = driver.findElement(By.xpath(".//table[contains(@class, 'ui-datepicker-calendar')]"));
		//List<WebElement> rows = dateWidget.findElements(By.tagName("tr"));
		List<WebElement> columns = dateWidget.findElements(By.tagName("td"));
		
		Thread.sleep(1000);	
		
	
		for (WebElement cell: columns){
		 //Select 13th Date 
			if (cell.getText().equals(dia)){
				
				if(cell.findElements(By.linkText(dia)).isEmpty()) return;
				cell.findElement(By.linkText(dia)).click();
				
				break;
			}		
		} 
		
		
		Thread.sleep(1000);
		
		if(driver.findElements(By.xpath(".//a[contains(@class, 'btn btn-mini')]")).isEmpty()) return;
		
		FileDownloader downloadTestFile = new FileDownloader(driver);
		
		String nomeArquivoPagina = "src/resources/temp/Rondonia/"
				 + dia + "_" + mes + "_"
				+ ano + "_Maranhao_.pdf";
		
		String linkPagina = driver.findElement(By.xpath(".//a[contains(@class, 'btn btn-mini')]")).getAttribute("href");
		
		String downloadFileAbsoluteLocation = downloadTestFile.downloadFile(linkPagina, nomeArquivoPagina);
		System.out.println(downloadFileAbsoluteLocation);
		
		int nPages = SplitPDFFile.splitPDF(nomeArquivoPagina);
		
		for (int pagina = 1; pagina <= nPages; pagina++) {
			
			String secao = "";
			
			String nomePag = nomeArquivoPagina.substring(0, nomeArquivoPagina.indexOf(".pdf")) 
                    + "_" + String.format("%d", pagina) + ".pdf";;
			
			PDDocument doc = PDDocument.load(nomePag);

			PDFTextStripper stripper = new PDFTextStripper();

			try {					
				
				System.out.println("Indexando " + "Rondonia"
						+ "" + data + " - Seção " + secao + "..."
						+ " Página " + pagina);
				System.out.println(nomePag);
				
				
				String content = stripper.getText(doc);

				IndexItem pdfIndexItem = new IndexItem(
						(long) nomePag.hashCode(),
						DownloadDOU.formatDateToFileName(data), String.valueOf(secao),
						content, String.valueOf(pagina), String.valueOf("Rondonia"), linkPagina);
				Indexer indexer = new Indexer(Config.INDEX_DIR);
				indexer.index(pdfIndexItem);
				indexer.close();
				
				new File(nomePag).delete();
				
			} catch (Exception e) {
				System.out.println("Falha na indexação "
						+ "Rondonia" + " " + data + " - Seção "
						+ secao);
			} finally {
				doc.close();
			}
		}
		
		new File(nomeArquivoPagina).delete();
		
	}
}
