package Agenda;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Programa que convierte un String en un documento XML
 * @author Mauricio Segura Ariño
 */
public class Ventana extends JFrame implements ActionListener{
  
  File fichXML;
  Document doc;
  
  ArrayList<String> datos;
  TreeSet<String> telefonos;
  TreeSet<String> emails; 
  
  JPanel panel;
  JButton btnParse;
  JTextArea campoTexto, cajaTexto;

  public Ventana(String titulo){
    this.setTitle(titulo);
    iniciarLookAndFeel();
    iniciarContenedores();
    iniciarControles();
    iniciarListeners();
    setVisible(true);
  }
  private void iniciarLookAndFeel(){
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }catch (Exception ex) {
      System.err.println("Error de Look and Feel");
    }
  }
  private void iniciarContenedores() {
    // Elimina la aplicación de memoria y de CPU
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(640, 480);
    panel = new JPanel();
    setContentPane(panel);// Establecemos panelPrincipal como panel por defecto.  
    panel.setLayout(null);
  }
  private void iniciarControles() {
    // Creamos el campo de texto
    campoTexto = new JTextArea();
    //campoTexto.setText("12345678Z,\"nombre\",\"apellidos\", prueba@prueba.com,(952)333333c,test@test.com ,952333333,test@TEST.com,test@test");
    campoTexto.setSize(620, 45);
    campoTexto.setLocation(5,5);
    campoTexto.setLineWrap(true);
    panel.add(campoTexto);
    // Creamos el boton que hará el parse
    btnParse = new JButton("Convertir en XML");
    btnParse.setSize(150, 40);//tamaño del botón
    //punto de inserción con respecto a la esquina superior izquierda de su componente padre, en este caso el panel
    btnParse.setLocation(5,50);
    panel.add(btnParse);
    // Creamos la caja de texto
    cajaTexto = new JTextArea();
    cajaTexto.setSize(620, 360);
    cajaTexto.setLocation(5, 90);
    cajaTexto.setLineWrap(true);
    panel.add(cajaTexto);
  }
  private void iniciarListeners() {
    btnParse.addActionListener(this);
  }
  @Override
  public void actionPerformed(ActionEvent ae) {
    String2XML();
  }
  private void String2XML() {
    // Dividir el String en partes dividiendo por la ","
    String texto = campoTexto.getText();
    String[] partes = texto.split(","); // Array general
    // Usamos TreeSet ya que guarda los datos ordenados y no crea duplicados
    datos = new ArrayList();
    telefonos = new TreeSet<>(java.util.Collections.reverseOrder()); // TreeSet de los teléfonos
    emails = new TreeSet<>(); // TreeSet de los emails
    // Quitar las comillas de los campos del nombre y apellidos
    partes[1] = partes[1].replaceAll("\"", "");
    partes[2] = partes[2].replaceAll("\"", "");
    // Agregamos los datos al ArrayList de datos
    datos.add(partes[0]); // añadimos el DNI/NIE
    datos.add(partes[1]); // añadimos el nombre
    datos.add(partes[2]); // añadimos el apellido
    // recorremos y "reparamos" los elementos restantes del array
    for(int cont = 3;cont<partes.length;cont++){
      // Si contiene el símbolo @ es un email
      if(partes[cont].contains("@")){
        partes[cont] = partes[cont].toLowerCase();
        partes[cont] = partes[cont].trim();
        emails.add(partes[cont]);
      }else{ // es un número de teléfono
        partes[cont] = partes[cont].replaceAll("\"", "");
        partes[cont] = partes[cont].replaceAll("[(]", "");
        partes[cont] = partes[cont].replaceAll("[)]", "");
        telefonos.add(partes[cont]);
      }
    }   
    // Visualizo los ArrayList
    cajaTexto.setText("");
    // Visualizar estructuras ArrayList y TreeSet
    visualizarEstructura(datos);
    visualizarEstructura(telefonos);
    visualizarEstructura(emails);
    parsearXML();
    visualizarXML();
  }
  private void visualizarEstructura(Collection<String> lista) {
    String texto;
    texto = cajaTexto.getText();
    Iterator<String> it = lista.iterator();
    while(it.hasNext()){
      texto = texto + it.next() + "\n";
    }
    cajaTexto.setText(texto);
  }
  private void parsearXML() {
    String curDir = System.getProperty("user.dir"); // Directorio de trabajo actual
    JFileChooser sel = new JFileChooser(curDir); // Instanciamos la ventana de selección de archivos.
    int seleccion = sel.showDialog(this, "Abrir Fichero");
    if(seleccion == JFileChooser.APPROVE_OPTION){//si la opcion que ha escogido el usuario es Abrir Fichero
      fichXML = sel.getSelectedFile();//obtengo el archivo que ha seleccionado el usuario
      try{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        
        // elemento raiz ======================================================
        Element raiz = doc.createElement("datos_cliente");
        doc.appendChild(raiz);
        
        // elemento id (DNI/NIE) ==============================================
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(datos.get(0)));
        raiz.appendChild(id);
        // Compruebo si el DNI/NIE es correcto y si no es así añado un comentario
        if(!comprobarPattern("([XY]?)([0-9]{1,9})([A-Za-z])",datos.get(0))){
          Comment comentario = doc.createComment("Este DNI/NIE no es correcto");
          id.appendChild(comentario);
        }
        
        // elemento nombre ====================================================
        Element nombre = doc.createElement("nombre");
        nombre.appendChild(doc.createTextNode(datos.get(1)));
        raiz.appendChild(nombre);
        // Comprobamos que son todo letras
        if(!comprobarPattern("[A-Za-z]+",datos.get(1))){
          Comment comentario = doc.createComment("Este nombre no es correcto");
          nombre.appendChild(comentario);
        }
        
        // elemento apellidos =================================================
        Element apellidos = doc.createElement("apellidos");
        apellidos.appendChild(doc.createTextNode(datos.get(2)));
        raiz.appendChild(apellidos);
        // Comprobamos que son todo letras
        if(!comprobarPattern("[A-Za-z]+",datos.get(2))){
          Comment comentario = doc.createComment("Este nombre no es correcto");
          apellidos.appendChild(comentario);
        }
        
        // elemento telefonos =================================================
        Element telefonosItem = doc.createElement("telefonos");
        raiz.appendChild(telefonosItem);
        // atributo total de telefonos ========================================
        Attr telefonosAtr = doc.createAttribute("total");
        telefonosAtr.setValue(Integer.toString(telefonos.size()));
        telefonosItem.setAttributeNode(telefonosAtr);
        // elementos telefono =================================================
        String temp;
        Iterator<String> it = telefonos.iterator();
        while(it.hasNext()){
          Element telefono = doc.createElement("telefono");
          temp = it.next();
          telefono.appendChild(doc.createTextNode(temp));
          telefonosItem.appendChild(telefono);
          if(!comprobarPattern("\\+?[0-9]{9}[0-9]*",temp)){
            Comment comentario = doc.createComment("Este telefono no es correcto");
            telefono.appendChild(comentario);
          }
        }
        
        // elemento mails =====================================================
        Element mailsItem = doc.createElement("mails");
        raiz.appendChild(mailsItem);
        // elementos mail =====================================================
        Iterator<String> it2 = emails.iterator();
        while(it2.hasNext()){
          Element mail = doc.createElement("mail");
          temp = it2.next();
          mail.appendChild(doc.createTextNode(temp));
          mailsItem.appendChild(mail);
          if(!comprobarPattern("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})$",temp)){
            Comment comentario = doc.createComment("Este email no es correcto");
            mail.appendChild(comentario);
          }
        }
        // Creamos una nueva instancia del transformador a través de la fábrica de transformadores.
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // Creamos el DOMSource, intermediaria entre el transformador y el árbol DOM.
        DOMSource source = new DOMSource(doc);
        // Creamos el StreamResult, intermediria entre el transformador y el archivo de destino.
        StreamResult result = new StreamResult(fichXML);
        // Realizamos la transformación.
        transformer.transform(source, result);
        // Aviso de la creación del fichero
        System.out.println("El fichero ha sido grabado en:"+fichXML);
      } catch (ParserConfigurationException pce) {
        pce.printStackTrace();
      } catch (TransformerException tfe) {
        tfe.printStackTrace();
      }
    }    
  }
  private boolean comprobarPattern(String patron, String dato) {
    Pattern p=Pattern.compile(patron);
    Matcher m=p.matcher(dato);
    if (m.matches()) return true;
    else return false;
  }  
  private void visualizarXML() {
    try {
      FileReader fr = new FileReader (fichXML);
      BufferedReader br = new BufferedReader(fr);
      String linea, texto = "";
      while((linea = br.readLine()) != null){
        texto = texto + linea;
      }
      cajaTexto.setText(cajaTexto.getText() + texto);
      br.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } 
  }
}
