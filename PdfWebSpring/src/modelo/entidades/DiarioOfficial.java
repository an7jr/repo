package modelo.entidades;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class DiarioOfficial {
	@Id
	@GeneratedValue
	private Long id;
	
	private String local;
	
	private String url;
	
	private Integer formatoPagina;
	
	private Boolean paginado;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="Diario_ID")
	private List<Jornal> jornais;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Jornal> getJornais() {
		return jornais;
	}

	public void setJornais(List<Jornal> jornais) {
		this.jornais = jornais;
	}

	public Integer getFormatoPagina() {
		return formatoPagina;
	}

	public void setFormatoPagina(Integer formatoPagina) {
		this.formatoPagina = formatoPagina;
	}

	public Boolean getPaginado() {
		return paginado;
	}

	public void setPaginado(Boolean paginado) {
		this.paginado = paginado;
	}

	
	
	
	
}
