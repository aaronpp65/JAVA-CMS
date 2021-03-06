import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Parser {

    public String parse(String filename) throws Exception {
        
        List<String> title =  Arrays.asList("<title>","</title>");
        List<String> subtitle =  Arrays.asList("<subtitle>","</subtitle>");
        List<String> link =  Arrays.asList("<link>","</link>");
        List<String> updated =  Arrays.asList("<updated>","</updated>");
        List<String> author =  Arrays.asList("<author>","</author>");
        List<String> name =  Arrays.asList("<name>","</name>");
        List<String> id =  Arrays.asList("<id>","</id>");
        List<String> entry =  Arrays.asList("<entry>","</entry>");
        List<String> summary =  Arrays.asList("<summary>","</summary>");

        int rejectCounter=0;

        //reading file and making objects
        File file = new File(filename);
        int flag=0;
        boolean clientIdFlag=true;
        String clientId="";
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st,atomFeed="<?xml version='1.0' encoding='iso-8859-1' ?><feed>";

        
        while ((st = br.readLine()) != null) {

            String[] words=st.split(":");

            if(words[0].equals("title")){
                atomFeed+=title.get(0);
                atomFeed+=words[1];
                atomFeed+=title.get(1);
                rejectCounter++;

            }else if(words[0].equals("subtitle")){
                atomFeed+=subtitle.get(0);
                atomFeed+=words[1];
                atomFeed+=subtitle.get(1);

            }else if(words[0].equals("link")){
                atomFeed+=link.get(0);
                atomFeed+=words[1];
                atomFeed+=link.get(1);
                rejectCounter++;


            }else if(words[0].equals("updated")){
                atomFeed+=updated.get(0);
                atomFeed+=words[1]+":"+words[2]+":"+words[3];
                atomFeed+=updated.get(1);

            }else if(words[0].equals("author")){
                atomFeed+=author.get(0);
                atomFeed+=name.get(0);
                atomFeed+=words[1];
                atomFeed+=name.get(1);
                atomFeed+=author.get(1);

            }else if(words[0].equals("id")){
                atomFeed+=id.get(0);
                atomFeed+=words[1]+":"+words[2]+":"+words[3];
                atomFeed+=id.get(1);
                rejectCounter++;
                if(clientIdFlag){
                    clientId = words[3];
                    clientIdFlag=false;
                }


            }else if(words[0].equals("summary")){
                atomFeed+=summary.get(0);
                atomFeed+=words[1];
                atomFeed+=summary.get(1);

            }else if(words[0].equals("entry")){

                if(flag==0){
                    atomFeed+=entry.get(0);
                    flag=1;
                }                 
                else{
                    atomFeed+=entry.get(1);
                    atomFeed+=entry.get(0);
                }

            }


        }
        atomFeed+=entry.get(1)+"</feed>";

        if(rejectCounter%3==0){

            return clientId+"~"+atomFeed;

        }

        else{

            return "feedError";

        }
        
    }
    public void parseXml(String xmlString) throws Exception {
        try {
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(xmlString);
            ByteArrayInputStream input = new ByteArrayInputStream(
            xmlStringBuilder.toString().getBytes("UTF-8"));

             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse(input);
             doc.getDocumentElement().normalize();
             Element root = doc.getDocumentElement();
             // System.out.println("Root element :" + root.getNodeName());
             NodeList nListRoot = root.getChildNodes();
             for(int i=0;i<nListRoot.getLength()-2;i++){
                System.out.println(nListRoot.item(i).getNodeName() +" :"+ nListRoot.item(i).getTextContent());

             }

        

             NodeList nList = doc.getElementsByTagName("entry");
             System.out.println("----------------------------");
             
             for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\n");
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                   Element eElement = (Element) nNode;
                   // System.out.println("Student roll no : " 
                   //    + eElement.getAttribute("rollno"));
                   System.out.println("title : " 
                      + eElement
                      .getElementsByTagName("title")
                      .item(0)
                      .getTextContent());
                   System.out.println("link : " 
                      + eElement
                      .getElementsByTagName("link")
                      .item(0)
                      .getTextContent());
                   System.out.println("id : " 
                      + eElement
                      .getElementsByTagName("id")
                      .item(0)
                      .getTextContent());
                   System.out.println("updated : " 
                      + eElement
                      .getElementsByTagName("updated")
                      .item(0)
                      .getTextContent());
                   System.out.println("summary : " 
                      + eElement
                      .getElementsByTagName("summary")
                      .item(0)
                      .getTextContent());
                }
             }
             System.out.println("*****************************\n");

      } catch (Exception e) {
            System.out.println("Status : 500 Internal server error");

      }

    }
}