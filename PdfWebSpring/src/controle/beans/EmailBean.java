package controle.beans;

import java.util.List;
import javax.faces.bean.ManagedBean;
import Indexacao.classes.EmailUtils;



@ManagedBean
public class EmailBean {
	
	
	//envia uma mensagem diferente para cada entrada na tabela de Cadastro de buscas (varios emails por usuario)
    public String mailToList(){
    	
    	EmailUtils.mailToList();
    	
    	return "emailList.xhtml";
    }
    
 	
    //agrupa os termos de cada email distinto e envia uma mensagem com todos os termos para cada, se houver resultado
    public String groupSearchMailToList(){
    	
    	EmailUtils.groupSearchMailToList();
    	
    	return "emailList.xhtml";
    }
    
   
	
    //envia email se houver resultados de busca
	public void doSearchAndEmail(String email, String termo){
		  
		   EmailUtils.doSearchAndEmail(email, termo);
	   }
	
	//junta todos os resultados de todos os termos e envia email se ao menos um dos termos tiver resultado
	public void doMultipleSearchAndEmail(String email, List<String> termo){
		  
		 EmailUtils.doMultipleSearchAndEmail(email, termo);
			   
   }


}
