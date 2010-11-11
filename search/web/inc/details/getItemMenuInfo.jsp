<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>

<%@page import="com.google.inject.Injector"%>
<%@page import="javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
<%
            Injector inj = (Injector) application.getAttribute(Injector.class.getName());
            LocalizationContext lctx = inj.getProvider(LocalizationContext.class).get();
            pageContext.setAttribute("lctx", lctx);
%>
<%@ include file="../initVars.jsp" %>

<c:url var="url" value="${kconfig.solrHost}/select/select" >
    <c:param name="q" >
        PID:"${param.pid}"<c:if test="${param.model!=null}"> and fedora.model:${param.model}</c:if>
    </c:param>
    <c:param name="fl" value="PID,fedora.model,dc.title,details" />
    
</c:url>

<c:url var="xslPage" value="xsl/relsextDetails.jsp" />
<c:catch var="exceptions"> 
    <c:import url="${url}" var="xml" charEncoding="UTF-8" />
    <c:import url="${xslPage}" var="xsltPage" charEncoding="UTF-8"  />
</c:catch>
<c:choose>
    <c:when test="${exceptions != null}">
        <c:out value="${exceptions}" />
        <c:out value="${url}" />
        <c:out value="${xml}" />
    </c:when>
    <c:otherwise>
        <% out.clear();%>
        <c:if test="${param.debug =='true'}"><c:out value="${url}" /></c:if>
        <c:catch var="exceptions2"> 
            <x:transform doc="${xml}"  xslt="${xsltPage}"  >
                <x:param name="pid" value="${param.pid}"/>
                <x:param name="level" value="${param.level}"/>
                <x:param name="onlyinfo" value="true"/>
            </x:transform>
            <c:set var="obj" value="#tabs_${param.level}" />
            <c:set var="href" value="#{href}" />
            <c:set var="label" value="#{label}" />
            <c:set var="target" value="#tab${label}-page" />
        </c:catch>
        <c:if test="${exceptions2 != null}"><c:out value="${exceptions2}" />
        </c:if>
    </c:otherwise>
</c:choose>
