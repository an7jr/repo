package Diarios;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import modelo.classes.IndexItem;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import Indexacao.classes.DownloadDOU;
import Indexacao.classes.Indexer;
import configuracao.Config;
import configuracao.SplitPDFFile;

public class SantaCatarinaDO {
	public static void Download(String data, WebDriver driver)
			throws InterruptedException {
		// TODO Auto-generated method stub

		String[] d = data.split("/");
		String dia = d[0];
		String mes = d[1];
		String ano = d[2];

		// WebDriver driver = new FirefoxDriver();

		driver.get("http://doe.sea.sc.gov.br/Portal/ListarJornal.aspx");
		
		if(driver.findElements(By
						.xpath(".//select[contains(@name , 'ctl00$ContentPlaceHolder1$ddlAno')]")).isEmpty()) return ;

		new Select(
				driver.findElement(By
						.xpath(".//select[contains(@name , 'ctl00$ContentPlaceHolder1$ddlAno')]")))
				.selectByValue(ano);
		Thread.sleep(200);

		new Select(
				driver.findElement(By
						.xpath(".//select[contains(@name , 'ctl00$ContentPlaceHolder1$ddlMes')]")))
				.selectByValue("" + Integer.parseInt(mes));
		Thread.sleep(200);

		new Select(
				driver.findElement(By
						.xpath(".//select[contains(@name , 'ctl00$ContentPlaceHolder1$ddlDia')]")))
				.selectByValue("" + Integer.parseInt(dia));
		Thread.sleep(200);

		driver.findElement(By.id("ctl00_ContentPlaceHolder1_ImageButton1"))
				.click();

		Thread.sleep(200);

		if (driver
				.findElements(
						By.xpath(".//div[contains(@id , 'ctl00_ContentPlaceHolder1_tblResultados')]"))
				.isEmpty())
			return;

		WebElement resultWidget = driver
				.findElement(By
						.xpath(".//div[contains(@id , 'ctl00_ContentPlaceHolder1_tblResultados')]"));

		WebElement result = resultWidget.findElements(By.tagName("tr")).get(1);

		result.findElements(By.tagName("td")).get(3)
				.findElement(By.xpath(".//input[contains(@value , 'ABRIR')]"))
				.click();

		String mwh = driver.getWindowHandle();

		Set s = driver.getWindowHandles();
		// this method will gives you the handles of all opened windows

		Iterator ite = s.iterator();
		ite.next();
		String popupHandle = ite.next().toString();
		driver.switchTo().window(popupHandle);
		String text = driver.getCurrentUrl();
		driver.close();
		driver.switchTo().window(mwh);

		// driver.quit();

		String cd = text.split("cd=")[1];

		String linkpagina = "http://doe.sea.sc.gov.br/Portal/VisualizarArquivoJornal.aspx?cd="
				+ cd + "&amp";

		System.out.println(linkpagina);

		String nomeArquivoPagina = "src/resources/temp/SantaCatarina/" + dia
				+ "_" + mes + "_" + ano + "_SantaCatarina_.pdf";

		File file = new File(nomeArquivoPagina);

		try {
			FileUtils.copyURLToFile(new URL(linkpagina), file);
			System.out.println("baixado");

			int nPages = SplitPDFFile.splitPDF(nomeArquivoPagina);

			for (int pagina = 1; pagina <= nPages; pagina++) {

				String secao = "";

				String nomePag = nomeArquivoPagina.substring(0,
						nomeArquivoPagina.indexOf(".pdf"))
						+ "_"
						+ String.format("%d", pagina) + ".pdf";
				;

				PDDocument doc = PDDocument.load(nomePag);

				PDFTextStripper stripper = new PDFTextStripper();

				try {

					System.out.println("Indexando " + "Santa Catarina" + ""
							+ data + " - Seção " + secao + "..." + " Página "
							+ pagina);
					System.out.println(nomePag);

					String content = stripper.getText(doc);

					IndexItem pdfIndexItem = new IndexItem(
							(long) nomePag.hashCode(),
							DownloadDOU.formatDateToFileName(data),
							String.valueOf(secao), content,
							String.valueOf(pagina), String.valueOf("Santa Catarina"), linkpagina);
					Indexer indexer = new Indexer(Config.INDEX_DIR);
					indexer.index(pdfIndexItem);
					indexer.close();

					 new File(nomePag).delete();

				} catch (Exception e) {
					System.out.println("Falha na indexação " + "Santa Catarina"
							+ " " + data + " - Seção " + secao);
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
