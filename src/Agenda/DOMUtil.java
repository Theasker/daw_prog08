package Agenda;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
 
/**
 * Utilidades para pasar árboles DOM a documentos XML y viceversa.
 * @author Salvador Romero Villegas
 */
public class DOMUtil {
 
    /**
     * Carga un archivo con un documento XML a un árbol DOM.      
     * @param CaminoAArchivoXml puede ser un archivo local de tu disco duro
     * o una URI de Internet (http://...).
     * @return el documento DOM o null si no se ha podido cargar el documento.
     */    
    public static Document XML2DOM (String CaminoAArchivoXml) {
        Document doc=null;
        try {
          // 1º Creamos una nueva instancia de un fabrica de constructores de documentos.
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          // 2º A partir de la instancia anterior, fabricamos un constructor de documentos, que procesará el XML.
          DocumentBuilder db = dbf.newDocumentBuilder();
          // 3º Procesamos el documento (almacenado en un archivo) y lo convertimos en un árbol DOM.
          doc=db.parse(CaminoAArchivoXml);            
 
        } catch (Exception ex) {
          Logger.getLogger(DOMUtil.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return doc;
    }
 
    /**
     * Convierte una cadena que contiene un documento XML a un árbol DOM.
     * @param documentoXML cadena que contiene el documento XML.
     * @return El árbol DOM o null si no se ha podido convertir.
     */
    public static Document String2DOM (String documentoXML){
        ByteArrayInputStream bais=new ByteArrayInputStream(documentoXML.getBytes());
        Document doc=null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc=db.parse(bais);            
 
        } catch (Exception ex) {
            Logger.getLogger(DOMUtil.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return doc;
    }
 
    /**
     * Convierte un árbol DOM a una cadena que contiene un documento XML.
     * @param doc Árbol DOM.
     * @return null si no se ha podido convertir o la cadena con el documento
     * en XML si se ha podido convertir.
     */
    public static String DOM2XML (Document doc){
        String xmlString=null;
        try {
          //2º Creamos una nueva instancia del transformador a través de la fábrica de transformadores.
          Transformer transformer = TransformerFactory.newInstance().newTransformer();
          //3º Establecemos algunas opciones de salida, como por ejemplo, la codificación de salida.
          transformer.setOutputProperty(OutputKeys.INDENT, "yes");
          transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
          //4º Creamos el StreamResult, intermediria entre el transformador y el archivo de destino.
          StreamResult result = new StreamResult(new StringWriter());
          //5º Creamos el DOMSource, intermediaria entre el transformador y el árbol DOM.
          DOMSource source = new DOMSource(doc);
          //6º Realizamos la transformación.
          transformer.transform(source, result);
          xmlString = result.getWriter().toString();
        } catch (TransformerException ex) {
            Logger.getLogger(DOMUtil.class.getName()).log(Level.SEVERE, null, ex);
            xmlString=null;
        }
        return xmlString;
    }
 
    /**
     * Convierte un árbol DOM a XML y lo guarda en un archivo.
     * @param doc Documento a convertir en XML.
     * @param CaminoAlArchivoXML Camino o path para llegar al archivo en el
     * disco.
     * @return true si se ha podido convertir y false en cualquier otra situación.
     */
    public static boolean DOM2XML (Document doc, String CaminoAlArchivoXML) {
        try {
            File f=new File(CaminoAlArchivoXML);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");            
            StreamResult result = new StreamResult(f);            
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);         
            return true;
        } catch (TransformerException ex) {
            Logger.getLogger(DOMUtil.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return false;
    }
 
    /**
     * Crea un árbol DOM vacío.
     * @param etiquetaRaiz Nombre de la etiqueta raíz del árbol DOM, donce
     * estará contenida el resto del documento.
     * @return Retornará el documento creado o null si se ha producido algún 
     * error.
     */    
    public static Document crearDOMVacio(String etiquetaRaiz) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document d=db.newDocument();
            d.appendChild(d.createElement(etiquetaRaiz));
            return d;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DOMUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}