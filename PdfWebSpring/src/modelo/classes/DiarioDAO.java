package modelo.classes;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

public class DiarioDAO {
	
	private EntityManager manager;
	
	public DiarioDAO(EntityManager manager){
		this.manager = manager;
	}
	
	
	
	
}
