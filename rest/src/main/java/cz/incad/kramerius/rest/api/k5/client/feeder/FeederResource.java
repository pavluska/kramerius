package cz.incad.kramerius.rest.api.k5.client.feeder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.MostDesirable;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.processes.annotations.DefaultParameterValue;
import cz.incad.kramerius.rest.api.exceptions.GenericApplicationException;
import cz.incad.kramerius.rest.api.k5.client.JSONDecoratorsAggregate;
import cz.incad.kramerius.rest.api.k5.client.utils.JSONUtils;
import cz.incad.kramerius.rest.api.k5.client.utils.SOLRUtils;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.users.UserProfile;
import cz.incad.kramerius.users.UserProfileManager;
import cz.incad.kramerius.utils.ApplicationURL;
import cz.incad.kramerius.utils.XMLUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v5.0/feed")
public class FeederResource {

    private static final int LIMIT = 18;

    public static final Logger LOGGER = Logger.getLogger(FeederResource.class
            .getName());

    @Inject
    MostDesirable mostDesirable;

    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;

    @Inject
    Provider<HttpServletRequest> requestProvider;

    @Inject
    JSONDecoratorsAggregate decoratorsAggregate;

    @Inject
    SolrAccess solrAccess;

    @Inject
    Provider<User> userProvider;

    @Inject
    UserProfileManager userProfileManager;

    @GET
    @Path("newest")
    @Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
    public Response newest(
            @QueryParam("vc") @DefaultValue("") String virtualCollection,
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset,
            @QueryParam("type") String documentType) {
        try {
            if (limit == null) {
                limit = LIMIT;
            }
            int start = (offset == null) ? 0 : offset * limit;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rss",
                    ApplicationURL.applicationURL(requestProvider.get())
                            + "/inc/home/newest-rss.jsp");

            StringBuilder req = new StringBuilder("q=level%3a0");
            if (documentType != null) {
                req.append("&fq=document_type:" + documentType);
            }
            req.append("&rows=").append(limit).append("&start=").append(start)
                    .append("&sort=level+asc%2c+created_date+desc");

            Document document = this.solrAccess.request(req.toString());
            Element result = XMLUtils.findElement(
                    document.getDocumentElement(), "result");
            JSONArray jsonArray = new JSONArray();
            List<Element> docs = XMLUtils.getElements(result,
                    new XMLUtils.ElementsFilter() {
                        @Override
                        public boolean acceptElement(Element element) {
                            return (element.getNodeName().equals("doc"));
                        }
                    });

            for (Element doc : docs) {
                String pid = SOLRUtils.value(doc, "PID", String.class);
                if (pid != null) {
                    try {
                        String uriString = UriBuilder
                                .fromResource(FeederResource.class)
                                .path("newest").build(pid).toString();
                        JSONObject mdis = JSONUtils.pidAndModelDesc(pid,
                                fedoraAccess, uriString,
                                this.decoratorsAggregate, uriString);
                        jsonArray.add(mdis);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
            jsonObject.put("data", jsonArray);
            return Response.ok().entity(jsonObject.toString()).build();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            throw new GenericApplicationException(ex.getMessage());
        }
    }

    @GET
    @Path("mostdesirable")

    @Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
    public Response mostdesirable(
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset,
            @QueryParam("type") String documentType) {
        // "http://localhost:8080/search/inc/home/mostDesirables-rss.jsp"
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rss",
                ApplicationURL.applicationURL(requestProvider.get())
                        + "/inc/home/mostDesirables-rss.jsp");
        if (limit == null) {
            limit = LIMIT;
        }
        if (offset == null) {
            offset = 0;
        }
        List<String> mostDesirable = this.mostDesirable.getMostDesirable(limit, offset, documentType);
        JSONArray jsonArray = new JSONArray();
        for (String pid : mostDesirable) {
            try {
                String uriString = UriBuilder
                        .fromResource(FeederResource.class)
                        .path("mostdesirable").build(pid).toString();
                JSONObject mdis = JSONUtils.pidAndModelDesc(pid, fedoraAccess,
                        uriString, this.decoratorsAggregate, uriString);
                jsonArray.add(mdis);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        jsonObject.put("data", jsonArray);

        return Response.ok().entity(jsonObject.toString()).build();
    }
}
