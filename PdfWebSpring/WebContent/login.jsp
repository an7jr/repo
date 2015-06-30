<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
 
<html>
  <head>
    <title>Login</title>
  </head>
 
  <body>
    <h1>Login</h1>
 
    <c:if test="${not empty param.login_error}">
      <font color="red">
        Login não concluido.<br/><br/>
        Motivo: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      </font>
    </c:if>
 
    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
      <table>
        <tr><td>Email:</td><td><input type='text' name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/></td></tr>
        <tr><td>Senha:</td><td><input type='password' name='j_password'></td></tr>
        
 
        <tr><td colspan='2'><input name="Entrar" type="submit"></td></tr>
   
      </table>
    </form>
  </body>
</html>