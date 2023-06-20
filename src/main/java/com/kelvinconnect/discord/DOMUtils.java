package com.kelvinconnect.discord;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtils {
    private static final Logger logger = LogManager.getLogger(DOMUtils.class);

    private DOMUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static Iterable<Node> iterable(final NodeList nodeList) {
        return () ->
                new Iterator<Node>() {

                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < nodeList.getLength();
                    }

                    @Override
                    public Node next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        return nodeList.item(index++);
                    }
                };
    }

    public static Stream<Node> toStream(final NodeList nodeList) {
        return StreamSupport.stream(iterable(nodeList).spliterator(), false);
    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return dbf.newDocumentBuilder();
    }

    public static Transformer newTransformer() throws TransformerConfigurationException {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return tf.newTransformer();
    }

    public static String toString(Node n) {
        try {
            StringWriter sw = new StringWriter();
            newTransformer().transform(new DOMSource(n), new StreamResult(sw));
            return sw.toString();
        } catch (Exception e) {
            logger.error("Error parsing DOM Node to String", e);
            return "{error parsing xml}";
        }
    }
}
