<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">   
	<!-- 引入ckeditor组件(给用户输入提供方便) --> 
	<script src="${pageContext.request.contextPath }/ckeditor/ckeditor.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath }/ckeditor/samples/sample.css">
	
  </head>
  
  <body>
  	${requestScope.content}
  	<form name="frmDis" action="disServlet" method="post">
  		发表评论：<textarea class="ckeditor" rows="6" cols="30" name="content"></textarea>
  		<br/>
  		<input type="submit" value="评论">
  	</form>
  </body>
   
</html>
