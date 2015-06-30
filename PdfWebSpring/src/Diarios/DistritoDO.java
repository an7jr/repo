package Diarios;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import modelo.classes.IndexItem;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import Indexacao.classes.DownloadDOU;
import Indexacao.classes.Indexer;
import configuracao.Config;
import configuracao.FileDownloader;
import configuracao.SplitPDFFile;

public class DistritoDO {

	// Janeiro

	public static void Download(String data, WebDriver driver) throws Exception {
		// TODO Auto-generated method stub

		String[] d = data.split("/");
		String dia = d[0];
		String mes = DownloadDOU.getMonthName(Integer.parseInt(d[1]));
		//System.out.println(mes);
		String ano = d[2];

		// WebDriver driver = new FirefoxDriver();

		FileDownloader downloadTestFile = new FileDownloader(driver);

		List<String> linkPage = new ArrayList<String>();

		driver.get("http://www.gdf.df.gov.br/");
			
		Thread.sleep(2000);
		
		if(driver.findElements(By.tagName("iframe")).isEmpty()) return ;
		
		String wh = driver.getWindowHandle();

		driver.switchTo().frame("blockrandom");
		Thread.sleep(2000);

		WebElement form1 = driver.findElement(By.id("form1"));
		form1.click();
		WebDriverWait wait = new WebDriverWait(driver, 5);

		Thread.sleep(1000);

		List<WebElement> fields = form1.findElements(By.tagName("input"));
		List<WebElement> clicks = form1.findElements(By.tagName("a"));

		clicks.get(0).click();
		fields.get(0).sendKeys(ano);
		fields.get(0).sendKeys(Keys.ENTER);

		driver.switchTo().window(wh);
		driver.switchTo().frame("blockrandom");
		Thread.sleep(500);//
		form1 = driver.findElement(By.id("form1"));
		form1.click();
		fields = form1.findElements(By.tagName("input"));
		clicks = form1.findElements(By.tagName("a"));
		clicks.get(1).click();
		fields.get(1).sendKeys(mes + Keys.ENTER);
		//Thread.sleep(200);
		//fields.get(1).sendKeys(Keys.ENTER);
		
		Thread.sleep(1000);
		
		driver.switchTo().window(wh);
		driver.switchTo().frame("blockrandom");
		Thread.sleep(1000);

		form1 = driver.findElement(By.id("form1"));
		form1.click();
		fields = form1.findElements(By.tagName("input"));
		clicks = form1.findElements(By.tagName("a"));
		clicks.get(1).click();
		
		//Thread.sleep(1000);
		
		if(!(fields.size() < 3)){
		//if (!driver.findElements(By.id("nome_dodf_vermelho")).isEmpty()) {
			List<String> diarios = new ArrayList<String>();
			WebElement diarioWidget = form1.findElements(By.tagName("select"))
					.get(2);
			List<WebElement> options = diarioWidget.findElements(By
					.tagName("option"));
			for (WebElement option : options) {
				if (!option.getAttribute("value").isEmpty()) {

					if (option.getAttribute("value").split(" ")[3].split("-")[0]
							.equalsIgnoreCase(dia)) {
						diarios.add(option.getAttribute("value"));
					}
				}
			}

			for (String diario : diarios) {
				driver.switchTo().window(wh);
				driver.switchTo().frame("blockrandom");
				Thread.sleep(2000);//
				form1 = driver.findElement(By.id("form1"));
				form1.click();
				fields = form1.findElements(By.tagName("input"));
				if(fields.size() < 3) return ;
				clicks = form1.findElements(By.tagName("a"));
				clicks.get(2).click();
				fields.get(2).sendKeys(diario);
				System.out.println(diario);
				fields.get(2).sendKeys(Keys.ENTER);

				driver.switchTo().window(wh);
				driver.switchTo().frame("blockrandom");
				Thread.sleep(500);//
				form1 = driver.findElement(By.id("form1"));
				form1.click();			
				

				int count = 0;

				for (WebElement link : form1.findElements(By.id("link_dodf"))) {
					linkPage.add(link.getAttribute("href"));
					//String secao = link.getText();					
					
					String []teste = diario.split(" ");
					String secao;
					System.out.println(diario);
					if(DistritoDO.hasString(teste, "EXTRA")){
						 secao = "Edicao-Extra";
					}
					else if(!DistritoDO.hasString(teste, "SUPLEMENTO")){
						System.out.println(teste[1]);
						secao = "Secao "+ link.getText().split(" ")[1];
					}
					else if(!teste[teste.length - 1].equalsIgnoreCase("SUPLEMENTO")){
						
						 secao = "SUPLEMENTO" + " "+ teste[teste.length - 1];
					}
					else{
						 secao = "SUPLEMENTO";
					}
					
					String nomeArquivoPagina = "src/resources/temp/"
							+ "DistritoFederal" + "/" + dia + "_" + mes
							+ "_" + ano + "_" + "DistritoFederal" + "_" + secao +".pdf";
					
					String linkPagina = link.getAttribute("href");
					
					downloadTestFile.downloadFile(linkPagina, nomeArquivoPagina);
					
					int nPages = SplitPDFFile.splitPDF(nomeArquivoPagina);

					for (int pagina = 1; pagina <= nPages; pagina++) {						

						String nomePag = nomeArquivoPagina.substring(0,
								nomeArquivoPagina.indexOf(".pdf"))
								+ "_"
								+ String.format("%d", pagina) + ".pdf";
						;

						PDDocument doc = PDDocument.load(nomePag);

						PDFTextStripper stripper = new PDFTextStripper();

						try {

							System.out.println("Indexando " + "Distrito Federal" + ""
									+ data + " - Seção " + secao + "..." + " Página "
									+ pagina);
							System.out.println(nomePag);

							String content = stripper.getText(doc);

							IndexItem pdfIndexItem = new IndexItem(
									(long) nomePag.hashCode(),
									DownloadDOU.formatDateToFileName(data),
									String.valueOf(secao), content,
									String.valueOf(pagina), String.valueOf("Distrito Federal"), linkPagina);
							Indexer indexer = new Indexer(Config.INDEX_DIR);
							indexer.index(pdfIndexItem);
							indexer.close();

							// new File(nomePag).delete();

						} catch (Exception e) {
							System.out.println("Falha na indexação " + "Distrito Federal"
									+ " " + data + " - Seção " + secao);
						} finally {
							doc.close();
						}
					}
					
					new File(nomeArquivoPagina).delete();
				}				
				
				
				driver.navigate().back();
				//
				Thread.sleep(2000);
				
				
				
			}

//			int count = 0;
//			for (String a : linkPage) {
//				System.out.println(a);
//				System.out.println(downloadTestFile.downloadFile(a, "achei"
//						+ count));
//				count++;
//
//			}
		}
		
	}
	
	public static boolean hasString(String []a , String b){
		
		for(String c : a){
			if(c.trim().equalsIgnoreCase(b.trim())) return true;
		}
		
		return false;
	}

}
