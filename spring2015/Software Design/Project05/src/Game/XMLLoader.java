package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;

public class XMLLoader {

    /**
     * Gets the children nodes from an XML file
     * @param f the XML File to parse
     * @return the NodeList of children in the XML file
     */
    public NodeList getNodes(File f) {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.parse(new FileInputStream(f));
            Node root = doc.getFirstChild();
            return root.getChildNodes();
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
