package seguranca;

import java.util.List;
import java.util.Vector;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;






import modelo.dao.UsuarioDAO;
import modelo.entidades.Usuario;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AutenticadorLogin implements UserDetailsService{

	public AutenticadorLogin()
	{
		System.out.println("AA");
	}

	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		EntityManagerFactory factory =
		Persistence.createEntityManagerFactory("buscaDB");
		EntityManager manager = factory.createEntityManager();
		
		manager.getTransaction().begin();
		 
		
		
		UsuarioDAO usrdao = new UsuarioDAO(manager);
		
		
		Usuario usuario = usrdao.findByLogin(username);
	
		if (usuario == null) throw new UsernameNotFoundException("Usuário " + username + " não encontrado.");
	
		
		 manager.getTransaction().commit();
		 manager.close();
		 factory.close();
		
		List<GrantedAuthority> roles = new Vector<GrantedAuthority>();
		String permissoesUsuario = usuario.getPermissao();
		roles.add(new SimpleGrantedAuthority (permissoesUsuario));
		
		if(permissoesUsuario.equals("ROLE_ADMIN")) roles.add(new SimpleGrantedAuthority ("ROLE_USER"));
		
		
		
		User user = new User(username, usuario.getSenha(), true, true, true, true, roles);
		
		return user;
	}
	
	 
	

}
