package Indexacao.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import modelo.classes.IndexItem;
import modelo.entidades.DiarioOfficial;

import org.apache.commons.io.FileUtils;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import configuracao.Config;
import configuracao.SplitPDFFile;

public class DownloadDOU {

	public boolean downloadDiarioAndIndexNoPag(String data, String secao,
			DiarioOfficial diario) {

		List<File> files = new ArrayList<File>();

		try {

			System.out.println("Fazendo download " + diario.getLocal() + " "
					+ data + " - Seção " + secao);
			// String nomeArquivoPagina = "src/resources/temp/" +
			// diario.getLocal() +"/" + data + "_"+ diario.getLocal() +"_" +
			// secao +"_" + pagina + ".pdf";
			// String linkPagina =
			// "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer?jornal="
			// + secao + "&pagina=" + pagina + "&data=" + data +
			// "&captchafield=firistAccess";
			String linkPagina = diario.getUrl();
			String[] date = data.split("/");

			String nomeArquivoPagina = "src/resources/temp/"
					+ diario.getLocal() + "/" + date[0] + "_" + date[1] + "_"
					+ date[2] + "_" + diario.getLocal() + "_" + secao + ".pdf";

			String linkPaginaAux = linkPagina.replaceAll("ssecao", secao);
			linkPagina = linkPaginaAux;
			linkPaginaAux = linkPagina.replaceAll("ddia", date[0]);
			linkPagina = linkPaginaAux.replaceAll("mmes", "" + date[1]);
			linkPaginaAux = linkPagina.replaceAll("aano", date[2]);
			linkPagina = linkPaginaAux.replaceAll("emes",
					"" + DownloadDOU.getMonthName(Integer.parseInt(date[1])));

			System.out.println(linkPagina);

			File filePagina = new File(nomeArquivoPagina);
			try {

				FileUtils.copyURLToFile(new URL(linkPagina), filePagina);

			} catch (ConnectException ce) {
				EmailUtils.setConnectErrorMessage("Falha na conexão");
				System.out.println("Falha na conexão");

			} catch (UnknownHostException ue) {
				EmailUtils.setConnectErrorMessage("Falha na conexão");
				System.out.println("Falha na conexão");
			} catch (FileNotFoundException f) {
				// TODO: handle exception
				System.out.println("acabou pagina");
			} catch (IOException io) {
				System.out.println("pagina inexistente");
			}

			if ((filePagina.length() / 1024) < 2) {
				return false;
			}

			EmailUtils.setConnectErrorMessage("");

			files.add(filePagina);

			System.out.println(nomeArquivoPagina);

			int nPages = SplitPDFFile.splitPDF(nomeArquivoPagina);
			
			filePagina.delete();

			if (nPages == 0)
				return false;

			for (int pagina = 1; pagina <= nPages; pagina++) {
				
				String nomePag = nomeArquivoPagina.substring(0, nomeArquivoPagina.indexOf(".pdf")) 
	                    + "_" + String.format("%d", pagina) + ".pdf";;
				
				PDDocument doc = PDDocument.load(nomePag);

				PDFTextStripper stripper = new PDFTextStripper();

				try {					
					
					System.out.println("Indexando " + diario.getLocal()
							+ "" + data + " - Seção " + secao + "..."
							+ " Página " + pagina);
					System.out.println(nomePag);
					
					
					String content = stripper.getText(doc);

					IndexItem pdfIndexItem = new IndexItem(
							(long) nomePag.hashCode(),
							formatDateToFileName(data), String.valueOf(secao),
							content, String.valueOf(pagina), diario.getLocal(), String.valueOf(linkPagina));
					Indexer indexer = new Indexer(Config.INDEX_DIR);
					indexer.index(pdfIndexItem);
					indexer.close();
					
					//new File(nomePag).delete();
					
				} catch (Exception e) {
					System.out.println("Falha na indexação "
							+ diario.getLocal() + " " + data + " - Seção "
							+ secao);
				} finally {
					doc.close();
				}
			}

			// for(File file: files) file.delete();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return true;

	}

	/*
	 * (public boolean downloadDiario(String data, String secao){
	 * 
	 * List<File> files = new ArrayList<File>();
	 * 
	 * try{ int pagina = 1; while(true){ String nomeArquivoPagina =
	 * "src/resources/temp" + "/" + data + "_" + secao +"_" + pagina + ".pdf";
	 * String linkPagina =
	 * "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer?jornal=" + secao
	 * + "&pagina=" + pagina + "&data=" + data + "&captchafield=firistAccess";
	 * File filePagina = new File(nomeArquivoPagina);
	 * FileUtils.copyURLToFile(new URL(linkPagina), filePagina);
	 * 
	 * if((filePagina.length()/1024)<2){ if(pagina==1){ filePagina.delete();
	 * return false; } break; } else files.add(filePagina); pagina++; }
	 * 
	 * 
	 * PDFMergerUtility mergePdf = new PDFMergerUtility(); for(File file :
	 * files) mergePdf.addSource(file);
	 * mergePdf.setDestinationFileName("src/resources" + "/DO" + secao + "-" +
	 * formatDateToFileName(data) + ".pdf"); mergePdf.mergeDocuments();
	 * 
	 * for(File file: files) file.delete();
	 * 
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } finally{
	 * 
	 * }
	 * 
	 * return true;
	 * 
	 * }
	 */

//	public boolean downloadDiarioAndIndexNoPagFuncionando(String data,
//			String secao, DiarioOfficial diario) {
//
//		List<File> files = new ArrayList<File>();
//
//		try {
//
//			System.out.println("Fazendo download " + diario.getLocal() + " "
//					+ data + " - Seção " + secao);
//			// String nomeArquivoPagina = "src/resources/temp/" +
//			// diario.getLocal() +"/" + data + "_"+ diario.getLocal() +"_" +
//			// secao +"_" + pagina + ".pdf";
//			// String linkPagina =
//			// "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer?jornal="
//			// + secao + "&pagina=" + pagina + "&data=" + data +
//			// "&captchafield=firistAccess";
//			String linkPagina = diario.getUrl();
//			String[] date = data.split("/");
//
//			String nomeArquivoPagina = "src/resources/temp/"
//					+ diario.getLocal() + "/" + date[0] + "_" + date[1] + "_"
//					+ date[2] + "_" + diario.getLocal() + "_" + secao + ".pdf";
//
//			String linkPaginaAux = linkPagina.replaceAll("ssecao", secao);
//			linkPagina = linkPaginaAux;
//			linkPaginaAux = linkPagina.replaceAll("ddia", date[0]);
//			linkPagina = linkPaginaAux.replaceAll("mmes", "" + date[1]);
//			linkPaginaAux = linkPagina.replaceAll("aano", date[2]);
//			linkPagina = linkPaginaAux.replaceAll("emes",
//					"" + DownloadDOU.getMonthName(Integer.parseInt(date[1])));
//
//			System.out.println(linkPagina);
//
//			File filePagina = new File(nomeArquivoPagina);
//			try {
//
//				FileUtils.copyURLToFile(new URL(linkPagina), filePagina);
//
//			} catch (ConnectException ce) {
//				EmailUtils.setConnectErrorMessage("Falha na conexão");
//				System.out.println("Falha na conexão");
//
//			} catch (UnknownHostException ue) {
//				EmailUtils.setConnectErrorMessage("Falha na conexão");
//				System.out.println("Falha na conexão");
//			} catch (FileNotFoundException f) {
//				// TODO: handle exception
//				System.out.println("acabou pagina");
//			} catch (IOException io) {
//				System.out.println("pagina inexistente");
//			}
//
//			if ((filePagina.length() / 1024) < 2) {
//				return false;
//			}
//
//			EmailUtils.setConnectErrorMessage("");
//
//			files.add(filePagina);
//
//			System.out.println(nomeArquivoPagina);
//
//			PDDocument doc = PDDocument.load(nomeArquivoPagina);
//
//			PDFTextStripper stripper = new PDFTextStripper();
//
//			try {
//				System.out.println("Indexando " + diario.getLocal() + "" + data
//						+ " - Seção " + secao);
//				String content = stripper.getText(doc);
//
//				IndexItem pdfIndexItem = new IndexItem(
//						(long) nomeArquivoPagina.hashCode(),
//						formatDateToFileName(data), String.valueOf(secao),
//						content, String.valueOf(1));
//				Indexer indexer = new Indexer(Config.INDEX_DIR);
//				indexer.index(pdfIndexItem);
//				indexer.close();
//			} catch (Exception e) {
//				System.out.println("Falha na indexação " + diario.getLocal()
//						+ " " + data + " - Seção " + secao);
//			} finally {
//				doc.close();
//			}
//
//			// for(File file: files) file.delete();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//		}
//
//		return true;
//
//	}

	public boolean downloadDiarioAndIndex(String data, String secao,
			DiarioOfficial diario) {

		List<File> files = new ArrayList<File>();

		try {
			int pagina = 1;
			while (true) {

				System.out.println("Fazendo download " + diario.getLocal()
						+ " " + data + " - Seção " + secao + "..." + " Página "
						+ pagina);
				// String nomeArquivoPagina = "src/resources/temp/" +
				// diario.getLocal() +"/" + data + "_"+ diario.getLocal() +"_" +
				// secao +"_" + pagina + ".pdf";
				// String linkPagina =
				// "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer?jornal="
				// + secao + "&pagina=" + pagina + "&data=" + data +
				// "&captchafield=firistAccess";
				String linkPagina = diario.getUrl();

				String[] date = data.split("/");

				String nomeArquivoPagina = "src/resources/temp/"
						+ diario.getLocal() + "/" + date[0] + "_" + date[1]
						+ "_" + date[2] + "_" + diario.getLocal() + "_" + secao
						+ "_" + pagina + ".pdf";

				String linkPaginaAux = linkPagina.replaceAll("ssecao", secao);
				linkPagina = linkPaginaAux.replaceAll(
						"ppagina",
						""
								+ DownloadDOU.getDecimalFormat("" + pagina,
										diario.getFormatoPagina()));
				linkPaginaAux = linkPagina.replaceAll("ddia", date[0]);
				linkPagina = linkPaginaAux.replaceAll("mmes", "" + date[1]);
				linkPaginaAux = linkPagina.replaceAll("aano", date[2]);
				linkPagina = linkPaginaAux.replaceAll(
						"emes",
						""
								+ DownloadDOU.getMonthName(Integer
										.parseInt(date[1])));

				System.out.println(linkPagina);

				File filePagina = new File(nomeArquivoPagina);
				try {

					FileUtils.copyURLToFile(new URL(linkPagina), filePagina);

				} catch (ConnectException ce) {
					EmailUtils.setConnectErrorMessage("Falha na conexão");
					System.out.println("Falha na conexão");

				} catch (UnknownHostException ue) {
					EmailUtils.setConnectErrorMessage("Falha na conexão");
					System.out.println("Falha na conexão");
				} catch (FileNotFoundException f) {
					// TODO: handle exception
					System.out.println("acabou pagina");

					break;
				} catch (IOException io) {
					System.out.println("pagina inexistente");
				}

				if ((filePagina.length() / 1024) < 2) {

					if (pagina == 1) {
						filePagina.delete();
						return false;
					}
					break;
				} else {

					EmailUtils.setConnectErrorMessage("");

					files.add(filePagina);

					System.out.println(nomeArquivoPagina);

					PDDocument doc = PDDocument.load(nomeArquivoPagina);

					PDFTextStripper stripper = new PDFTextStripper();

					try {
						System.out.println("Indexando " + diario.getLocal()
								+ "" + data + " - Seção " + secao + "..."
								+ " Página " + pagina);
						String content = stripper.getText(doc);
						
						System.out.println("linkPagina = "+linkPagina);

						IndexItem pdfIndexItem = new IndexItem(
								(long) nomeArquivoPagina.hashCode(),
								formatDateToFileName(data),
								String.valueOf(secao), content,
								String.valueOf(pagina) , diario.getLocal(), String.valueOf(linkPagina));
						Indexer indexer = new Indexer(Config.INDEX_DIR);
						indexer.index(pdfIndexItem);
						indexer.close();
					} catch (Exception e) {
						System.out.println("Falha na indexação "
								+ diario.getLocal() + " " + data + " - Seção "
								+ secao + " Página " + pagina);
					} finally {
						doc.close();
					}

				}
				pagina++;

			}

			// for(File file: files) file.delete();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return true;

	}

	static public String formatDateToFileName(String date) {
		String dateToFileName;

		String[] dateArray = date.split("/");
		dateToFileName = dateArray[2] + dateArray[1] + dateArray[0];

		return dateToFileName;

	}

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));

			}
			dir.delete();
		} else {
			dir.delete();
		}

	}

	static public String getMonthName(int month) {
		String monthArray[] = { "Janeiro", "Fevereiro", "Março", "Abril",
				"Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro",
				"Novembro", "Dezembro" };

		return monthArray[month - 1];

	}

	static public String getDecimalFormat(String number, int format) {
		String resp = "";

		for (int i = 0; i < format - number.length(); i++) {
			resp = resp + "0";
		}

		return resp + "" + number;
	}

}
