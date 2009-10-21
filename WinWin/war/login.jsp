<%@page import="java.net.URLEncoder"%>
<html>
	<form action="login" method="post">
	<input name="key" value="<%=request.getParameter("key")%>" type="hidden"/>
	<input name="continue" value="kv?<%=request.getQueryString() %>" type="hidden"/>
	Password: <input name="password" type="password"/><br/>
	<input type="submit" value="Submit"/>
	</form>
</html>