package modelo.entidades;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Jornal {

	@Id
	@GeneratedValue
	
	private Long id;
	
	private String nome;	
	
	@ManyToOne(cascade=CascadeType.ALL)
	private DiarioOfficial diario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DiarioOfficial getDiario() {
		return diario;
	}

	public void setDiario(DiarioOfficial diario) {
		this.diario = diario;
	}
	
	
}
