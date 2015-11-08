<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/admin/top.jsp"%> 
	</head> 
<body>
		
<div class="container-fluid" id="main-container">

<div id="page-content" class="clearfix">
						
  <div class="row-fluid">

 	<div class="span12">
		<div class="widget-box">
			<div class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
				<h4 class="lighter">实时查询</h4>
			</div>
			<div class="widget-body">
			 <div class="widget-main">
			 
			 
			 
			 <div class="step-content row-fluid position-relative">
						<div style="width:715px;padding-left: 20px;">
							<div style="width:285px;height:399px;float: left;">
							
					<form action="cardinfo/now.do"  name="Form" id="Form" method="post">
					<table style="width:100%;" id="xtable">
					<tr>
						<td>
							 <input type="text" name="msisdn" id="msisdn" value="" placeholder="请选输入卡号" value="${msisdn} }" style="width:95%"/>
						</td>
						<td>&nbsp;</td>
						<td style="vertical-align:top;"><button class="btn btn-mini btn-light" onclick="searchByCard();"  title="检索"><i id="nav-search-icon" class="icon-search"></i></button></td>
					</tr>
					</table>
					</form>
								<div class="widget-box">
									<div class="widget-header widget-header-flat widget-header-small">
										<h5><i class="icon-credit-card"></i> 
											使用量
										</h5>
										<div class="widget-toolbar no-border">
										</div>
									</div>
									<div class="widget-body">
									 <div class="widget-main">
									 	短信${pd.smsused}条，流量${pd.gprsused}
									 </div><!--/widget-main-->
									</div><!--/widget-body-->
								</div><!--/widget-box-->
								<p>
								<p>
								<div class="widget-box">
									<div class="widget-header widget-header-flat widget-header-small">
										<h5><i class="icon-credit-card"></i> 
											开关机状态
										</h5>
										<div class="widget-toolbar no-border">
										</div>
									</div>
									<div class="widget-body">
									 <div class="widget-main">
									 	${pd.online}
									 </div><!--/widget-main-->
									</div><!--/widget-body-->
								</div><!--/widget-box-->
								
								<p>
								<p>
									<div class="widget-box">
									<div class="widget-header widget-header-flat widget-header-small">
										<h5><i class="icon-credit-card"></i> 
											GPRS在线信息
										</h5>
										<div class="widget-toolbar no-border">
										</div>
									</div>
									<div class="widget-body">
									 <div class="widget-main">
									 	GPRS状态：${pd.gprsstatus}, IP: ${pd.ip}, apn: ${pd.apn}, rat: ${pd.rat}
									 </div><!--/widget-main-->
									</div><!--/widget-body-->
								</div><!--/widget-box-->
								
							 </div><!--/span-->	
						</div>
						
			</div>
			 <div class="step-content row-fluid position-relative">
				 <input type="hidden" value="no" id="hasTp1" />
			 </div> 
			 
			 
			 
			 
			 </div><!--/widget-main-->
			</div><!--/widget-body-->
		</div>
	</div>
 
 
 
	<!-- PAGE CONTENT ENDS HERE -->
  </div><!--/row-->
	
</div><!--/#page-content-->
</div><!--/.fluid-container#main-container-->
		
		<!-- 引入 -->
		<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
		<script src="static/js/bootstrap.min.js"></script>
		<script src="static/js/ace-elements.min.js"></script>
		<script src="static/js/ace.min.js"></script>
		<!-- 引入 -->
		<script type="text/javascript">
		$(top.hangge());
	//保存
	function searchByCard(){
			if($("#MSISDN").val()==""){
			$("#MSISDN").tips({
				side:3,
	            msg:'请输入卡号',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#MSISDN").focus();
			return false;
		}
		$("#Form").submit();
	}
	
		</script>
		
		
	</body>
</html>

