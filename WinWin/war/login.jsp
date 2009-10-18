<html>
	<form action="login" method="post">
	<input name="key" value="<%=request.getParameter("key")%>" type="hidden"/>
	Password: <input name="password" type="password"/><br/>
	<input type="submit" value="Sign in"/>
	</form>
</html>