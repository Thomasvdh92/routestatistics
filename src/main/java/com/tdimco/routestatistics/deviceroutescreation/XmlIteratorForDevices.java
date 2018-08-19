package com.tdimco.routestatistics.deviceroutescreation;

import com.tdimco.routestatistics.domain.Device;
import com.tdimco.routestatistics.domain.SpanCollection;
import lombok.Getter;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlIteratorForDevices {

    @Getter
    List<Device> devices;
    @Getter
    SpanCollection spanCollection;

    public XmlIteratorForDevices() {
        devices = new ArrayList<>();
        spanCollection = new SpanCollection();
    }

    public void iterateXmlFolder(String folderPath) {
        int i = 0;
        File[] files = new File(folderPath).listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                iterateXmlFolder(file.getPath());
            }
            getRoutesFromXmlFile(file.getAbsolutePath());
            i++;
            System.out.println(i + "/" + files.length + " files done");
        }
    }

    private void getRoutesFromXmlFile(String xmlUrl) {
        List<Device> todoDevices = new ArrayList<>();
        for(Device d : devices) {
            todoDevices.add(d);
        }
        File xmlFile = new File(xmlUrl);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList deviceNodeList = doc.getElementsByTagName("dev");
            for (int i = 0; i < deviceNodeList.getLength(); i++) {
                Node deviceNode = deviceNodeList.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) deviceNode;
                    Iterator<Device> iter = todoDevices.iterator();
                    while(iter.hasNext()) {
                        Device d = iter.next();
                        if(String.valueOf(element.getAttribute("id")).equals(d.getDevId())) {
                            NodeList detectionNodeList = element.getElementsByTagName("rdd");
                            if (detectionNodeList.getLength() > 1) {
                                spanCollection.compileRoutes(d, detectionNodeList);
                            }
                            iter.remove();
                        }
                    }

                }
                if (todoDevices.size() == 0) break;
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void getDeviceFromXml(String xmlUrl) {
        File xmlFile = new File(xmlUrl);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList deviceNodeList = doc.getElementsByTagName("dev");
            for (int i = 0; i < deviceNodeList.getLength(); i++) {
                Node deviceNode = deviceNodeList.item(i);

                if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) deviceNode;
                    NodeList detectionNodeList = element.getElementsByTagName("rdd");
                    if (detectionNodeList.getLength() > 5) {
                        String vehicleType = String.valueOf(element.getAttribute("c"));
                        String devId = String.valueOf(element.getAttribute("id"));
                        Device device = new Device(devId, vehicleType);
                        devices.add(device);
                    }


                }
                if (devices.size() == 10) break;
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        System.out.println(devices.size());
    }
}
