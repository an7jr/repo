package modelo.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;


@Entity
public class CadastroBusca {

	@Id
	@GeneratedValue
	private Long id;
	
	private String email;
	
	private String termo;
	
	private Date dataCadastro;
	private Date dataValidade;
	
	@Transient
	private String statusAtividade;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTermo() {
		return termo;
	}

	public void setTermo(String termo) {
		this.termo = termo;
	}

	public Long getId() {
		return id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public String getStatusAtividade() {
		Date hoje = new Date();
				
		if(dataValidade == null || hoje.before(dataValidade)){
			this.statusAtividade = "Ativo";
		}
		else{
			this.statusAtividade = "Inativo";
		}
		
		return statusAtividade;
	}


}
