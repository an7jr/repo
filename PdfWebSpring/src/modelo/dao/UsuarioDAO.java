package modelo.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import modelo.entidades.Usuario;

public class UsuarioDAO {
	
private EntityManager manager ;
	
	public UsuarioDAO (EntityManager manager) {
		this.manager = manager ;
	}
	
	
	public Usuario findByLogin(String email){
		Usuario usuario = null;
		
		Query query = this.manager.createQuery("select x from Usuario x WHERE lower(email) like :username");
		query.setParameter("username", email.toLowerCase());
		
		try{
			usuario = (Usuario)query.getSingleResult();
		}catch(Exception e){
			
			usuario = null;
			e.printStackTrace();
		}
		
		
		return usuario;
	}
	
	public void adiciona(Usuario user){
		this.manager.persist (user);
	}
	
}
