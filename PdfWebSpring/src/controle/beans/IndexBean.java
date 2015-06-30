package controle.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import Indexacao.classes.EmailUtils;

@ManagedBean
@ViewScoped
public class IndexBean {
	
	private Date dataInicio=null;
	private Date dataFim=null;
	private Integer progress;
	public static Float status = (float) 0;
	
	
    public Integer getProgress() {
        if(progress == null) {
            progress =  0;
        }
        else {
        	
            progress = (int) Math.round(status);            

        }
         
        return progress;
    }
 
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
     
    public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Tarefa terminada"));
    }
     
    public void cancel() {
        progress = null;
    }
	
	public String downloadIndex(){
		if(this.dataInicio != null && this.dataFim != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			EmailUtils.downloadAndIndex(sdf.format(dataInicio), sdf.format(dataFim));
		}
		
		return "downloadIndex.xhtml";
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }
	
}
