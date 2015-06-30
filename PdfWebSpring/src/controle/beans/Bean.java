package controle.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean
@RequestScoped
public class Bean {

	private static final long serialVersionUID = 1L;

    private StreamedContent streamedContent;

    private static String INPUTFILE = "src/resources/temp/result3.pdf" ;
    
    @PostConstruct     
    public void init() {
        try {
        //----------------------------------
            Document doc = new Document();

            OutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, out);

            doc.open();         
            
           
            //doc.add(new Paragraph("Hello World. ok........"));
            //doc.close();  
            //out.close();
            
            PdfReader reader = new PdfReader(INPUTFILE);

            int n = reader.getNumberOfPages();

            PdfImportedPage page;
            
            
            
            System.out.println(n);
            

            for (int i = 1; i <= n; i++) {

                    page = writer.getImportedPage(reader, i);
                    
                    

                    Image instance = Image.getInstance(page);
                    
                    instance.scaleAbsolute(510.0f, 800.0f);
                    
                    
                    doc.add(instance);

            }

            doc.close();
            out.close();
            reader.close();
            

            InputStream in =new ByteArrayInputStream(((ByteArrayOutputStream)out).toByteArray());

            streamedContent = new DefaultStreamedContent(in, "application/pdf");
        //-------
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        byte[] b = (byte[]) session.get("reportBytes");
        if (b != null) {
            streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
        }            
        } catch (Exception e) {
        }

    }
    //==================================================================
    public StreamedContent getStreamedContent() {
        if (FacesContext.getCurrentInstance().getRenderResponse()) {
            return new DefaultStreamedContent();
        } else {
            return streamedContent;
        }
     }
    //==================================================================
    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }
	
}
