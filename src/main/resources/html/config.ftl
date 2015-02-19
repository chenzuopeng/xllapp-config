<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
table {
	border-style: solid;
	border-collapse: collapse;
	border-width: 1px;
	border-color: black;
	width: 400px;
	margin-top: 20px;
}

table td {
	border-style: solid;
	border-collapse: collapse;
	border-width: 1px;
	border-color: black;
}

</style>
</head>
<body>
<#if throwableMessage?has_content>
<a href="${url!}">返回</a>
<h4>错误信息:</h4><div style="margin-left: 30px;">${throwableMessage}</div>
<h4>异常堆栈:</h4>
<pre style="margin-left: 30px;">
${throwableStackTrace}
</pre>
<#else>
<input style="margin-top: 10px;" type="button" value="全部重置" onclick="window.location='?act=reset_all'"/>
<#list configItems as configItem>
 <form action="" method="post">
 <input type="hidden"  name="configItem.name" value="${configItem.name}">
	<table>
		<tr>
			<td style="width: 120px; height: 25px; text-align: center;">${configItem.name}</td>
			<td style="height: 25px; text-align: center;"><input style="line-height: 20px; width: 95%" type="text" name="configItem.value" value="${configItem.value!}" ></td>
			<td style="width: 100px; height: 25px; text-align: center; padding-left: 5px;"><input type="submit" value="修改" /><input type="button" value="重置" onclick="window.location='?act=reset&configItem.name=${configItem.name}'"/></td>
		</tr>
		<#if configItem.desc?has_content>
		<tr>
			<td colspan="3" style="height: 30px; padding-left: 5px;">说明: ${configItem.desc}</td>
		</tr>
		</#if>
	</table>
</form>
</#list>
</#if>
</body>
</html>