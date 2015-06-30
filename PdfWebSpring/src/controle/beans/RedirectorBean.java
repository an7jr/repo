package controle.beans;

import java.util.Locale;

import javax.faces.bean.ManagedBean;




import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import modelo.classes.SearchResult;
import modelo.dao.UsuarioDAO;
import modelo.entidades.Usuario;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;



@ManagedBean
public class RedirectorBean {
	
	private Usuario usuario = null;
	private boolean logado = true;
	
	
	public Locale getLocale(){
		return new Locale("tr", "TR");
	}
	
	public String simples(){
		
	  return "/user/search.xhtml?faces-redirect=true";
		 
	    
	}
	
	public String pdf(SearchResult result, String search){
		
		/*
		<f:viewParam name="local" value="#{pdfBean.local}"/>
		<f:viewParam name="data" value="#{pdfBean.data}"/>
		<f:viewParam name="secao" value="#{pdfBean.secao}"/>
		<f:viewParam name="pg"  value="#{pdfBean.pg}"/>
		<f:viewParam name="search" value="#{pdfBean.searchValue}"/>
		*/			
		
		//System.out.println(result.getLink());
		
		return "/user/pdfResult.xhtml?faces-redirect=truelocal="+result.getLocal()+"&data="+result.getData()+"&secao="+result.getSecao()+"&pg="+result.getPagina()+"&update=true&titulos=EXTRATO DE TERMO DE EXECU플O#GABINETE DO MINISTRO#AVISO DE LICITA플O&search="+search;
//		 titles.add("EXTRATO DE TERMO DE EXECU플O");
//		   titles.add("GABINETE DO MINISTRO");
//		   titles.add("AVISO DE LICITA플O");
		    
	}
	
	public String unico(){
		return "/user/sendEmail.xhtml?faces-redirect=true";
	}

	public String massa(){
		return "/admin/emailList.xhtml?faces-redirect=true";
	}

	public String cadastro(){
		return "/user/termos-cadastrados.xhtml?faces-redirect=true";
	}
	
	public String index(){
		return "/index.xhtml?faces-redirect=true";
	}
	
	public String downloadIndex(){
		return "/admin/downloadIndex.xhtml?faces-redirect=true";
	}
	
	 public String logout() {
		 SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
		 SecurityContextHolder.clearContext();
		 this.logado = false;
	        //session.setUser(null);
	       return "/login.jsp?faces-redirect=true";
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

	
	public boolean isLogado() {
		return logado;
	}

	public void setLogado(boolean logado) {
		this.logado = logado;
	}

	private EntityManager getManager() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		HttpServletRequest request = ( HttpServletRequest ) ec.getRequest ();
		return (EntityManager) request.getAttribute("EntityManager");
	}
}
