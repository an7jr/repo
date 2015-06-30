package Diarios;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import modelo.classes.IndexItem;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import Indexacao.classes.DownloadDOU;
import Indexacao.classes.Indexer;
import configuracao.Config;
import configuracao.SplitPDFFile;

public class AlagoasDO {

	public static void Download(String data, WebDriver driver) throws Exception {

		// driver.

		String[] d = data.split("/");
		String dia = d[0];
		String mes = d[1];
		String ano = d[2];

		// WebDriver driver = new FirefoxDriver();

		WebDriverWait wait = new WebDriverWait(driver, 30);

		driver.get("http://www.doeal.com.br//busca#/p=1&q=%20&di=" + ano + ""
				+ mes + "" + dia + "&df=" + ano + "" + mes + "" + dia
				+ "&f=true");
		
		Thread.sleep(2000);
		if(driver.findElements(By.xpath(".//input[contains(@name, 'psq')]")).isEmpty()) return ;		
		// Thread.sleep(1000);
		driver.findElement(By.xpath(".//input[contains(@name, 'psq')]"))
				.click();

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By
		// .className("pull-left")));

		Thread.sleep(2000);

		if (!driver.findElements(By.className("pull-left")).isEmpty()) {

			String linkpagina = driver.findElement(By.className("pull-left"))
					.getAttribute("href");

			System.out.println("link = " + linkpagina);

			// driver.quit();

			System.out.println(linkpagina);

			String nomeArquivoPagina = "src/resources/temp/Alagoas/"
					 + dia + "_" + mes + "_"
					+ ano + "_Alagoas_.pdf";			
			
			

			File file = new File(nomeArquivoPagina);

			try {
				FileUtils.copyURLToFile(new URL(linkpagina), file);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int nPages = SplitPDFFile.splitPDF(nomeArquivoPagina);
									
			new File(nomeArquivoPagina).delete();
			
			for (int pagina = 1; pagina <= nPages; pagina++) {
				
				String secao = "";
				
				String nomePag = nomeArquivoPagina.substring(0, nomeArquivoPagina.indexOf(".pdf")) 
	                    + "_" + String.format("%d", pagina) + ".pdf";;
				
				PDDocument doc = PDDocument.load(nomePag);

				PDFTextStripper stripper = new PDFTextStripper();

				try {					
					
					System.out.println("Indexando " + "Alagoas"
							+ "" + data + " - Seção " + secao + "..."
							+ " Página " + pagina);
					System.out.println(nomePag);
					
					
					String content = stripper.getText(doc);

					IndexItem pdfIndexItem = new IndexItem(
							(long) nomePag.hashCode(),
							DownloadDOU.formatDateToFileName(data), String.valueOf(secao),
							content, String.valueOf(pagina), String.valueOf("Alagoas"), linkpagina);
					Indexer indexer = new Indexer(Config.INDEX_DIR);
					indexer.index(pdfIndexItem);
					indexer.close();
					
					new File(nomePag).delete();
					
				} catch (Exception e) {
					System.out.println("Falha na indexação "
							+ "Alagoas" + " " + data + " - Seção "
							+ secao);
				} finally {
					doc.close();
				}
			}

		}
		//driver.quit();

	}
}
