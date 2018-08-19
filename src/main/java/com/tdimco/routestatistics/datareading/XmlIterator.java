package com.tdimco.routestatistics.datareading;

import com.tdimco.routestatistics.domain.SpanCollection;
import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Data
public class XmlIterator {

    private SpanCollection spanCollection;
    public int perc;

    public XmlIterator() {
        spanCollection = new SpanCollection();
    }

    public void iterateXmlFile(String xmlUrl, boolean secondIteration) {
        File xmlFile = new File(xmlUrl);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList deviceNodeList = doc.getElementsByTagName("dev");
//            System.out.println(getDateFromFilePath(xmlUrl) + " --> Iterating " + deviceNodeList.getLength() + " nodes");

            // Iterate through the list of nodes
            iterateXmlNodelist(deviceNodeList, secondIteration);
//            spanCollection.determineMaximumTimeForDrd(secondIteration);

//            while(!checkDeviationOnDrds()) {
//                setDrdValuesToFirst();
//                iterateXmlNodelist(deviceNodeList, true);
//            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iterateXmlNodelist(NodeList deviceNodeList, boolean secondIteration) {
        System.out.println("Iterating nodelist for routes and detections");
        long startTime = System.currentTimeMillis();
        perc = 10;
        for(int i=0;i< deviceNodeList.getLength(); i++) {
            Node deviceNode = deviceNodeList.item(i);

            if(i==deviceNodeList.getLength()-2) System.out.println("100% completed");
            if (((i * 100) / deviceNodeList.getLength()) >= perc) {
                System.out.println(perc + "% done");
                perc += 10;
            }

            if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) deviceNode;

                NodeList detectionNodeList = element.getElementsByTagName("rdd");
                if (detectionNodeList.getLength() > 1) {
                    for (int j = 0; j < detectionNodeList.getLength()-1; j++) {
                        Node detectionNode = detectionNodeList.item(j);
                        Element detection1 = (Element) detectionNode;
                        for (int k = j + 1; k < detectionNodeList.getLength(); k++) {
                            detectionNode = detectionNodeList.item(k);
                            Element detection2 = (Element) detectionNode;
                            if (Integer.parseInt(detection1.getAttribute("d")) != Integer.parseInt(detection2.getAttribute("d"))) {
                                spanCollection.addRouteDetection(detection1, detection2, secondIteration);
                            }

                        }
                    }
                }
            }
        }
    }

    public void iterateXmlFolder(String folderPath, boolean secondIteration) {
        int i=0;
        File[] files = new File(folderPath).listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                iterateXmlFolder(file.getPath(), secondIteration);
            }
            iterateXmlFile(file.getAbsolutePath(), secondIteration);
            i++;
            System.out.println(i + "/" + files.length +" files done");
        }
    }
}
