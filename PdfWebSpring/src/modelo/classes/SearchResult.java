package modelo.classes;

public class SearchResult {
	private String data;
	private String secao;
	private String pagina;
	private String fragmentos;
	private String local;
	private String link;
	
	
	public SearchResult(String d, String s, String p, String f, String local, String link){
		this.data = d;
		this.secao = s;
		this.fragmentos = f;
		this.pagina = p;
		this.local = local;
		this.link = link;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public String getSecao() {
		return secao;
	}


	public void setSecao(String secao) {
		this.secao = secao;
	}


	public String getPagina() {
		return pagina;
	}


	public void setPagina(String pagina) {
		this.pagina = pagina;
	}


	public String getFragmentos() {
		return fragmentos;
	}


	public void setFragmentos(String fragmentos) {
		this.fragmentos = fragmentos;
	}


	public String getLocal() {
		return local;
	}


	public void setLocal(String local) {
		this.local = local;
	}


	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}	
	
}
