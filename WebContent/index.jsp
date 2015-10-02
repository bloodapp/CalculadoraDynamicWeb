<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Calc</title>
</head>
<body>

<b><font color="blue">Entre com sua fórmula:</font></b><br><br>
<form name="frm" method="get" action="CalculadoraDynamicWeb">
  <table border = "0">
  <tr align="left" valign="top">
  <td>Fórmula:</td>
  <td><input type="text" name ="formula" /></td>
  </tr>
  <tr><td>
  <input type="submit" value="Executar"/>
  </td></tr>
  </table>
</form>

</body>
</html>