package Diarios;

import modelo.classes.IndexItem;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import Indexacao.classes.DownloadDOU;
import Indexacao.classes.Indexer;
import configuracao.Config;
import configuracao.FileDownloader;

public class RioGrandeDoSulDO {

	public static void Download(String data, WebDriver driver) throws Exception {
		// TODO Auto-generated method stub
		
		String[] date = data.split("/");
		String dia = date[0];
		String mes = date[1];
		String ano = date[2];
						
		driver.get("http://www.servico.corag.com.br/diarioOficial/verJornal.php?pg=001&jornal=doe&dt="+dia+"-"+mes+"-"+ano);
		
		Thread.sleep(1000);
		if (driver.getCurrentUrl().equalsIgnoreCase("http://www.servico.corag.com.br/diarioOficial/jornal-nao-existe.php")) return ;		
		
		driver.findElement(By.id("btn-forward")).click();
		
		Thread.sleep(1000);
		
		int nPages = Integer.parseInt(driver.findElement(By.xpath(".//input[contains(@name , 'pg')]")).getAttribute("value"));
		
		System.out.println(nPages);
		
		for(int p = 1; p<=nPages ; p++){
			driver.get("http://www.servico.corag.com.br/diarioOficial/verJornal.php?pg="+p+"&jornal=doe&dt="+dia+"-"+mes+"-"+ano);
			
			FileDownloader downloadTestFile = new FileDownloader(driver);
			
			String nomeArquivoPagina = "src/resources/temp/"
					+ "RioGrandeDoSul" + "/" + date[0] + "_" + date[1]
					+ "_" + date[2] + "_" + "RioGrandeDoSul" + "_" + "doe"
					+ "_" + p + ".pdf";
			
			System.out.println(downloadTestFile.downloadFile("http://www.servico.corag.com.br/diarioOficial/jornal.php", nomeArquivoPagina));
			
			PDDocument doc = PDDocument.load(nomeArquivoPagina);

			PDFTextStripper stripper = new PDFTextStripper();

			try {
				System.out.println("Indexando " + "RioGrandeDoSul"
						+ "" + data + " - Seção " + "" + "..."
						+ " Página " + p);
				String content = stripper.getText(doc);

				IndexItem pdfIndexItem = new IndexItem(
						(long) nomeArquivoPagina.hashCode(),
						DownloadDOU.formatDateToFileName(data),
						String.valueOf("doe"), content,
						String.valueOf(p), String.valueOf("RioGrandeDoSul"), "http://www.servico.corag.com.br/diarioOficial/jornal.php");
				Indexer indexer = new Indexer(Config.INDEX_DIR);
				indexer.index(pdfIndexItem);
				indexer.close();
			} catch (Exception e) {
				System.out.println("Falha na indexação "
						+ "RioGrandeDoSul" + " " + data + " - Seção "
						+ "" + " Página " + p);
			} finally {
				doc.close();
			}
			
		}
		
	}

}
