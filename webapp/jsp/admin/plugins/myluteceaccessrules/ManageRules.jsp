<jsp:useBean id="managerulesRule" scope="session" class="fr.paris.lutece.plugins.myluteceaccessrules.web.RuleJspBean" />
<% String strContent = managerulesRule.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
