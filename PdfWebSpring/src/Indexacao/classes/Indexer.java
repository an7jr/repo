package Indexacao.classes;

import modelo.classes.IndexItem;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.google.protobuf.WireFormat.FieldType;

import java.io.File;
import java.io.IOException;

public class Indexer {
 private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        // create the index
        if(writer == null) {
        writer = new IndexWriter(FSDirectory.open(
                new File(indexDir)), new IndexWriterConfig(Version.LATEST, new StandardAnalyzer()));
        }
    }

   
    public void index(IndexItem indexItem) throws IOException {

        // deleting the item, if already exists
        writer.deleteDocuments(new Term(IndexItem.ID, indexItem.getId().toString()));
     	
        

        Document doc = new Document();
        
        doc.add(new StringField(IndexItem.ID,indexItem.getId().toString(), Field.Store.YES));
        doc.add(new StringField(IndexItem.DATA,indexItem.getData(), Field.Store.YES));
        doc.add(new TextField(IndexItem.LOCAL, indexItem.getLocal().toString(), Field.Store.YES));
        doc.add(new TextField(IndexItem.CONTEUDO,indexItem.getConteudo(), Field.Store.YES));
        doc.add(new StringField(IndexItem.PAGINA,String.valueOf(indexItem.getPagina()), Field.Store.YES));
        doc.add(new StringField(IndexItem.SECAO,String.valueOf(indexItem.getSecao()), Field.Store.YES));
        doc.add(new StringField(IndexItem.LINK,String.valueOf(indexItem.getLink()), Field.Store.YES));

        // add the document to the index
        writer.addDocument(doc);
       
       
        
        
    }

   
    public void close() throws IOException {
    	writer.commit();
        writer.close();
    }
}