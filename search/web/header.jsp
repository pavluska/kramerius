<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false"%>
<%
            String fromField = "f1";
            String toField = "f2";
            String fromValue = "";
            String toValue = "";
%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <meta name="description" content="National Library of Czech Republic digitized documents (periodical, monographs) access aplication." />
    <meta name="keywords" content="periodical, monograph, library, National Library of Czech Republic, book, publication, kramerius" />
    <meta name="AUTHOR" content="INCAD, www.incad.cz" />
    
    <link rel="icon" href="img/favicon.ico">
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
    <link rel="StyleSheet" href="css/styles.css" type="text/css">
    <link type="text/css" href="css/themes/base/ui.base.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.theme.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.dialog.css" rel="stylesheet" />
    <link rel="stylesheet" href="css/dateAxis.css" type="text/css">
    <link rel="stylesheet" href="css/dtree.css" type="text/css">
    
    <script language="JavaScript" type="text/javascript" src="js/add.js"></script>
    <script language="JavaScript" type="text/javascript" src="js/ext_ontheflypdf.js"></script>
    <script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
    <script src="js/jquery-ui-1.7.2.custom.min.js" language="javascript" type="text/javascript"></script>
    <script type="text/javascript" src="js/jquery.cookie.js"></script>
    <script type="text/javascript" src="js/jquery.history.js"></script>
    <script type="text/javascript" src="js/jquery.scrollTo.js"></script>
    
    <script language="JavaScript" type="text/javascript" src="js/incad.js"></script>
    <script language="JavaScript" type="text/javascript" src="js/pageQuery.js"></script>
    <script src="js/dateAxis_format.js" language="javascript" type="text/javascript"></script>
    <script src="js/dateAxis_jquery.js" language="javascript" type="text/javascript"></script>
    <script src="js/dtree.js" language="javascript" type="text/javascript"></script>
    
    <title>Kramerius 4</title>
    <script language="JavaScript" type="text/javascript">
        var pagesTitle = "<fmt:message>Stránka</fmt:message>";
        var unitsTitle = "<fmt:message>Unit</fmt:message>";
        var volumesTitle = "<fmt:message>Volume</fmt:message>";
        var issuesTitle = "<fmt:message>Issue</fmt:message>";
        var internalPartTitle = "<fmt:message>InternalPart</fmt:message>";
        var readingPages = "<fmt:message>Načítám stránky</fmt:message>";
        var readingUnits = "<fmt:message>Načítám části</fmt:message>";
        var readingVolumes = "<fmt:message>Načítám ročníky</fmt:message>";
        var readingIssues = "<fmt:message>Načítám ročníky</fmt:message>";
        var readingIntarnalParts = "<fmt:message>Načítám kapitoly</fmt:message>";
        var language = "<c:out value="${param.language}" />";
        var searchPage = "./";
        
        var fromField = "<%=fromField%>";
        var toField = "<%=toField%>";
        var dateAxisAdditionalParams = "";
        var fromStr = "od";
        var toStr = "do";
        var selectStart = "";
        var selectEnd = "";
    </script>
    
</head>