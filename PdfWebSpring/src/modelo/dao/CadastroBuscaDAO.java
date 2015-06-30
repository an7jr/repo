package modelo.dao;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.Query;


import modelo.entidades.CadastroBusca;

public class CadastroBuscaDAO {
	private EntityManager manager ;
	
	public CadastroBuscaDAO (EntityManager manager) {
		this.manager = manager ;
	}
	
	public void adiciona (CadastroBusca cadastro) {
		this.manager.persist (cadastro);
	}
	
	public void remove (Long id) {
		CadastroBusca cadastro = this.procura(id);
		this.manager.remove(cadastro);
	}
	
	public CadastroBusca atualiza(CadastroBusca cadastro) {
		return this.manager.merge(cadastro);
	}
	
	public CadastroBusca procura(Long id) {
		return this.manager.find(CadastroBusca.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<CadastroBusca>getLista() {
		Query query = this.manager.createQuery("select x from CadastroBusca x");
		return query.getResultList ();
	}
	
		
	@SuppressWarnings("unchecked")
	public List<String>getListaDistinct() {
		Query query = this.manager.createQuery("select distinct email from CadastroBusca");
		return query.getResultList ();
	}
	
	@SuppressWarnings("unchecked")
	public List<String>getTermosByEmail(String em) {
		Query query = this.manager.createQuery("select termo from CadastroBusca x where x.email = :email ");
		query.setParameter("email", em);
		return query.getResultList ();
	}
	
	@SuppressWarnings("unchecked")
	public List<CadastroBusca>getCadastrosByEmail(String em) {
		Query query = this.manager.createQuery("select x from CadastroBusca x where x.email = :email ");
		query.setParameter("email", em);
		return query.getResultList ();
	}
}
