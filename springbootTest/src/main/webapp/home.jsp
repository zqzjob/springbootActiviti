<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script src="js/jquery.min.js" type="text/javascript"></script>
</head>
<body>
<a id="deploy" href="#">发布请假流程</a>
<form action="leaveBill/add" method="post">
<label>天数：</label><input id="days" name="days"/>
<label>事由：</label><input id="content" name="content"/>
<button type="submit">提交</button>
</form>
</body>
<script type="text/javascript">
	$(function(){
		$("#deploy").on("click",function(){
			$.ajax({
				url:"demo/deploy",
				type:"POST",
				data:{path:"E:/test/leaveBill.zip",name:"leaveBill"},
				success:function(data){
					alert(data);
				}
			});
		});
	})
</script>
</html>