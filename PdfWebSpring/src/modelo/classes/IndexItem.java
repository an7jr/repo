package modelo.classes;

public class IndexItem {
 	private Long id;
    private String data;
    private String secao;
    private String conteudo;
    private String pagina;
    private String local;
    private String link;

    public static final String ID = "id";
    public static final String DATA= "data";
    public static final String CONTEUDO = "conteudo";
    public static final String PAGINA = "pagina";
    public static final String SECAO = "secao";
    public static final String LOCAL = "local";
    public static final String LINK = "link";

    public IndexItem(Long id, String data,String secao, String conteudo,String pagina, String local, String link) {
        this.id = id;
        this.data = data;
        this.secao = secao;
        this.conteudo = conteudo;
        this.pagina = pagina;
        this.local = local;
        this.link = link;
    }   

	public Long getId() {
		return id;
	}



	public String getData() {
		return data;
	}



	public String getSecao() {
		return secao;
	}



	public String getConteudo() {
		return conteudo;
	}

	public String getPagina() {
		return pagina;
	}

	public String getLocal() {
		return local;
	}	

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
    public String toString() {
        return "IndexItem{" +
                "id=" + id +                
                ", data='" + data + '\'' +
                ", local='"+ local +'\''+
                ", secao='" + secao + '\'' +
                ", conteudo='" + conteudo + '\'' +
                ", link='" + link + '\'' +
                ", pagina='" + pagina + '\'' +
                '}';
    }
	
}
