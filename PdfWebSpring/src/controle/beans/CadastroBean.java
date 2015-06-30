package controle.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import modelo.dao.CadastroBuscaDAO;
import modelo.dao.UsuarioDAO;
import modelo.entidades.CadastroBusca;
import modelo.entidades.Usuario;

@ManagedBean
@ViewScoped
public class CadastroBean {
	 private Usuario usuario = null;
	 private boolean cadastrou = false;
	 private CadastroBusca novoCadastro = new CadastroBusca();
	 private CadastroBusca cadastro = new CadastroBusca();
	 private List<CadastroBusca> termos= new ArrayList<CadastroBusca>();
	 private Long tid; //id de termo a excluir ou alterar
	
	   public String storeSearchAndEmail(){		  
		  	   
			EntityManager manager = this.getManager();
			
			CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
			
			this.novoCadastro.setEmail(this.getUsuario().getEmail());
			Date data = new Date();
			this.novoCadastro.setDataCadastro(data);
						
			repo.adiciona(this.novoCadastro);
			
			this.novoCadastro = new CadastroBusca();
			
			this.setCadastrou(true);
			
		   return "termos-cadastrados.xhtml";
		        
		}
	   
	   private EntityManager getManager() {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			HttpServletRequest request = ( HttpServletRequest ) ec.getRequest ();
			return (EntityManager) request.getAttribute("EntityManager");
		}

	public boolean isCadastrou() {
		return cadastrou;
	}

	public void setCadastrou(boolean cadastrou) {
		this.cadastrou = cadastrou;
	}
	
	public CadastroBusca getNovoCadastro() {
		return novoCadastro;
	}

	public void setNovoCadastro(CadastroBusca cadastro) {
		this.novoCadastro = cadastro;
	}

	public Usuario getUsuario() {
		if(usuario == null){
			User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String usr = user.getUsername();
	    
			UsuarioDAO usdao = new UsuarioDAO(getManager());
			this.usuario = usdao.findByLogin(usr);
		}	    
	    return this.usuario;
	}

	public List<CadastroBusca> getTermos() {
		if(termos.isEmpty()){
			EntityManager manager = this.getManager();
			CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
			
			this.termos = repo.getCadastrosByEmail(this.getUsuario().getEmail());
			 
		}
		
		return termos;
	}

	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
		EntityManager manager = this.getManager();
		CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
		this.cadastro = repo.procura(tid);
	}

	public CadastroBusca getCadastro() {
		return cadastro;
	}

	public void setCadastro(CadastroBusca cadastro) {
		this.cadastro = cadastro;
	}
	
	public String remove(){
		System.out.println(this.tid);
		
		EntityManager manager = this.getManager();
		
		CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
		
		repo.remove(this.tid);
		
		return "termos-cadastrados.xhtml";
	}
	
	public String atualiza(){
		EntityManager manager = this.getManager();
		
		CadastroBuscaDAO repo = new CadastroBuscaDAO(manager);
		
		repo.atualiza(this.cadastro);
		
		return "termos-cadastrados.xhtml";
	}
	
	public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

}
