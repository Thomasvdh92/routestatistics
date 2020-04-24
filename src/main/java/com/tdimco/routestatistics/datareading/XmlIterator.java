package com.tdimco.routestatistics.datareading;

import com.tdimco.routestatistics.domain.Device;
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


public @Data class XmlIterator {

    private SpanCollection spanCollection;
    public int perc;

    public XmlIterator() {
        spanCollection = new SpanCollection();
    }

    /**
     * iterates an xml file, identified by the url.
     * @param xmlUrl file url for the xml file
     * @param secondIteration used for other methods
     */
    public void iterateXmlFile(String xmlUrl, boolean secondIteration) {
        File xmlFile = new File(xmlUrl);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            // We build a device nodelist here using some standard libraries. Then we iterate over this list
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

    /**
     * Here we iterate over the xml nodelist created in the method above
     * @param deviceNodeList
     * @param secondIteration
     */
    public void iterateXmlNodelist(NodeList deviceNodeList, boolean secondIteration) {
        System.out.println("Iterating nodelist for routes and detections");
        long startTime = System.currentTimeMillis();
        perc = 10;
        for(int i=0;i< deviceNodeList.getLength(); i++) {
            Node deviceNode = deviceNodeList.item(i);

            // Here we do some printing to give an indication of where we currently are
            if(i==deviceNodeList.getLength()-2) System.out.println("100% completed");
            if (((i * 100) / deviceNodeList.getLength()) >= perc) {
                System.out.println(perc + "% done");
                perc += 10;
            }

            if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) deviceNode;

                // Obtain the detection list for a device, which is another nodelist
                NodeList detectionNodeList = element.getElementsByTagName("rdd");
                if (detectionNodeList.getLength() > 1) { // Only continue if there is more than 1 detection
                    for (int j = 0; j < detectionNodeList.getLength()-1; j++) {
                        Node detectionNode = detectionNodeList.item(j);
                        Element detection1 = (Element) detectionNode;
                        /**
                         * Here we compare each detection with the rest of the list. So for example, if we have a list
                         * containing 5 detections, we compare 0 to 1, 2, 3 and 4. Then we compare 1 to 2, 3 and 4 etc.
                         */
                        for (int k = j + 1; k < detectionNodeList.getLength(); k++) {
                            detectionNode = detectionNodeList.item(k);
                            Element detection2 = (Element) detectionNode;
                            if (Integer.parseInt(detection1.getAttribute("d")) != Integer.parseInt(detection2.getAttribute("d"))) {
                                this.spanCollection.addRouteDetection(detection1, detection2, secondIteration);
                            }

                        }
                    }
                }
            }
        }
        this.spanCollection.determineMaximumTimeForDrd(secondIteration);
    }

    /**
     * Method to recursively iterate over a folder to read all it's xml files.
     * @param folderPath
     * @param secondIteration
     */
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

    public void compileRoutesFromFolder(String folderPath) {
        int i=0;
        File[] files = new File(folderPath).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                compileRoutesFromFolder(file.getPath());
            }
            compileRoutesFromDeviceNodeList(file.getAbsolutePath());
            i++;
            System.out.println(i + "/" + files.length +" files done");
        }
    }

    public void compileRoutesFromDeviceNodeList(String xmlUrl) {
        File xmlFile = new File(xmlUrl);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList deviceNodeList = doc.getElementsByTagName("dev");
            long startTime = System.currentTimeMillis();
            System.out.println("Compiling device routes from device nodelist");
            perc = 10;
            for (int i = 0; i < deviceNodeList.getLength(); i++) {

                Node deviceNode = deviceNodeList.item(i);
                if (i == deviceNodeList.getLength() - 2) System.out.println("100% completed");
                if (((i * 100) / deviceNodeList.getLength()) >= perc) {
                    System.out.println(perc + "% done");
                    perc += 10;
                }
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) deviceNode;
                    NodeList detectionNodeList = element.getElementsByTagName("rdd");
                    if (detectionNodeList.getLength() > 1) {
                        String vehicleType = String.valueOf(element.getAttribute("c"));
                        String devId = String.valueOf(element.getAttribute("id"));
                        Device device = new Device(devId, vehicleType);
                        spanCollection.compileRoutes(device, detectionNodeList);
                    }
                }
            }
            System.gc();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Elapsed time for method \"compileRoutesFromDeviceNodeList\": " + (elapsedTime / 1000) / 60 + "min " + (elapsedTime / 1000) % 60 + "sec");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
