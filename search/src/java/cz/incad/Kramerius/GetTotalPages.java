/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.Kramerius;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class GetTotalPages extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    private static final String fedoraUrl = "http://194.108.215.227:8080/fedora";
    
    Document contentDom;
    String command;
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath;
    XPathExpression expr;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
            String pid = request.getParameter("pid");
            int numPages = getRdfPids(pid);
            out.print(numPages);
        //writeBiblioModsInfo(pids, out);
        } catch (Exception e) {
            out.println(e.toString());
        } finally {
            out.close();
        }
    }
    
    private int getRdfPids(String pid) {
        int num = 0;
        ArrayList<String> pids = new ArrayList<String>();
        try {
            command = fedoraUrl + "/get/" + pid + "/RELS-EXT";
            contentDom = getDocument(command);
            expr = xpath.compile("/RDF/Description/*");
            NodeList nodes = (NodeList) expr.evaluate(contentDom, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node childnode = nodes.item(i);
                String nodeName = childnode.getNodeName();
                if (nodeName.contains("hasPage")) {
                    num++;
                } else if (nodeName.contains("hasVolume") ||
                        nodeName.contains("hasItem")||
                        nodeName.contains("hasUnit")){
                    pids.add(childnode.getAttributes().getNamedItem("rdf:resource").getNodeValue().split("/")[1]);
                }
            }
            for (String relpid : pids) {
                num += getRdfPids(relpid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    private Document getDocument(String urlStr) {
        try {
            StringBuffer result = new StringBuffer();
            java.net.URL url = new java.net.URL(urlStr);

            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream(),
                    java.nio.charset.Charset.forName("UTF-8")));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(result.toString()));
            return builder.parse(source);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
