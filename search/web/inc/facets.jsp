<%@ page pageEncoding="UTF-8" %>
<%@ page import="java.util.*, cz.incad.Solr.CzechComparator, cz.incad.Solr.*" %>
<%
            Iterator it = facets.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Facet currentFacet = (Facet) pairs.getValue();
                //if (currentFacet.name.contains("abeceda")) {

%>
<%@ include file="facet.jsp" %>
<%                //}
            }
%>