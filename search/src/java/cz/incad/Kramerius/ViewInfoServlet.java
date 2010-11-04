package cz.incad.Kramerius;

import static cz.incad.utils.IKeys.UUID_PARAMETER;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.antlr.stringtemplate.StringTemplate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.incad.Kramerius.HandleServlet.HandleType;
import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.imaging.DeepZoomCacheService;
import cz.incad.kramerius.utils.RESTHelper;
import cz.incad.kramerius.utils.XMLUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class ViewInfoServlet extends AbstractSolrProcessServlet {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MimeTypeServlet.class.getName());
    
    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;
    
    @Inject
    DeepZoomCacheService deepZoomCacheService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uuid = req.getParameter(UUID_PARAMETER);
    
            if ((uuid != null) && (!uuid.equals(""))) {
                String mimeType = this.fedoraAccess.getImageFULLMimeType(uuid);
                boolean generated = resolutionFilePresent(uuid);
                boolean conf = deepZoomConfigurationEnabled(uuid);
                
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("deepZoomCacheGenerated", ""+generated);
                map.put("deepZoomCofigurationEnabled", ""+conf);
                map.put("mimeType", mimeType);

                resp.setContentType("application/xml");
                resp.getWriter().println(getResponseXML(map));
            }
        } catch (XPathExpressionException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    private String getResponseXML(Map<String, String> map) {
        String response = "{" +
            "deepZoomGenerated:$data.deepZoomCacheGenerated$,"+
            "deepZoomCofigurationEnabled:$data.deepZoomCofigurationEnabled$,"+
            "mimeType:'$data.mimeType$'," +
            "isContentPDF:function() {return viewerOptions.mimeType=='application/pdf'},"+
            "isContentDJVU:function() {return viewerOptions.mimeType.indexOf('djvu')> 0 }"+
		"}";
        StringTemplate template = new StringTemplate(response);
        template.setAttribute("data", map);
        return template.toString();
    }

    private boolean resolutionFilePresent(String uuid) throws IOException, ParserConfigurationException, SAXException {
        boolean resFile = deepZoomCacheService.isResolutionFilePresent(uuid);
        return resFile;
    }
    
    
    private boolean deepZoomConfigurationEnabled(String uuid) {
        try {
            String solrHost = KConfiguration.getInstance().getSolrHost();
            String uri = solrHost +"/select/?q=PID:"+uuid;

            InputStream inputStream = RESTHelper.inputStream(uri, "<no_user>", "<no_pass>");
            Document parseDocument = XMLUtils.parseDocument(inputStream);
            String pidPath = disectPidPath(parseDocument);
            
            return KConfiguration.getInstance().isDeepZoomEnabled() || KConfiguration.getInstance().isDeepZoomForPathEnabled(pidPath.split("/"));
        } catch (XPathExpressionException e) {
            LOGGER.severe(e.getMessage());
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        } catch (ParserConfigurationException e) {
            LOGGER.severe(e.getMessage());
        } catch (SAXException e) {
            LOGGER.severe(e.getMessage());
        }
        return false;
    }
}