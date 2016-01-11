<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- jsp文件头和头部 -->
<%@ include file="../admin/top.jsp"%>

<script type="text/javascript">
	var GetLength = function(str) {
		///<summary>获得字符串实际长度，中文2，英文1</summary>
		///<param name="str">要获得长度的字符串</param>
		var realLength = 0, len = str.length, charCode = -1;
		for ( var i = 0; i < len; i++) {
			charCode = str.charCodeAt(i);
			if (charCode >= 0 && charCode <= 128)
				realLength += 1;
			else
				realLength += 2;
		}
		return realLength;
	};

	//保存
	function sendSMS() {
		if ($("#PHONE").val() == "") {
			$("#PHONE").tips({
				side : 3,
				msg : '请输入卡号',
				bg : '#AE81FF',
				time : 2
			});
			$("#PHONE").focus();
			return false;
		}

		if ($("#CONTENT").val() == "") {
			$("#CONTENT").tips({
				side : 3,
				msg : '请输入短信内容',
				bg : '#AE81FF',
				time : 2
			});
			$("#PHONE").focus();
			return false;
		}
		if(GetLength($("#CONTENT").val()) > 140){
			$("#CONTENT").tips({
				side : 3,
				msg : '短信内容不能超过140个字符',
				bg : '#AE81FF',
				time : 2
			});
			$("#PHONE").focus();
			return false;
		}

		$("#Form").submit();
	}
</script>
</head>
<body>

	<div class="container-fluid" id="main-container">



		<div id="page-content" class="clearfix">

			<div class="row-fluid">

				<div class="span12">
					<div class="widget-box">
						<div
							class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
							<h4 class="lighter">发送短信</h4>
						</div>
						<div class="widget-body">


							<div class="widget-main">
								<div class="step-content row-fluid position-relative">

									<form action="smspocessor/doSend.do" name="Form" id="Form"
										method="post">
										<input type="hidden" name="TYPE" id="TYPE" value="1" />
										<table style="width:100%;" id="xtable">
											<tr>
												<td style="margin-top:0px;">
													<div style="float: left;width:86%">
														<textarea name="PHONE" id="PHONE" rows="1" cols="50"
															style="width:100%;height:20px;"
															placeholder="请选输入对方卡号,多个请用(;)分号隔开"
															title="请选输入对方卡号,多个请用(;)分号隔开">${pd.PHONE}</textarea>
													</div> <!-- 
							 <div style="float: right;width:10%"><a class='btn btn-mini btn-info' title="编辑卡号" onclick="dialog_open();"><i class='icon-edit'></i></a></div>
							 --></td>
											</tr>
											<tr>
												<td>
													<div style="float: left;width:86%">
														<textarea name="CONTENT" id="CONTENT"  placeholder="请选输入短信内容"
															title="请选输入短信内容" class="autosize-transition span12" style="width:690px;">${pd.CONTENT}</textarea>
													</div></td>
											</tr>
											<tr>
												<td style="text-align: center;"><a
													class="btn btn-mini btn-primary" onclick="sendSMS();">发送</a>
													<label style="float:left;padding-left: 32px;"><input
														name="form-field-radio" id="form-field-radio1"
														onclick="setType('1');" checked="checked" type="radio"
														value="icon-edit"><span class="lbl">纯文本</span> </label> <!--
							<label style="float:left;padding-left: 5px;"><input name="form-field-radio" id="form-field-radio2" onclick="setType('2');" type="radio" value="icon-edit"><span class="lbl">带标签</span></label>
							 --></td>
											</tr>
										</table>

									</form>
									<div id="zhongxin2" class="center" style="display:none">
										<br /> <img src="static/images/jzx.gif" id='msg' /><br />
										<h4 class="lighter block green" id='msg'>正在发送...</h4>
									</div>


								</div>
							</div>
							<!--/widget-main-->
						</div>
						<!--/widget-body-->
					</div>

				</div>


				<!-- PAGE CONTENT ENDS HERE -->
			</div>
			<!--/row-->

		</div>
		<!--/#page-content-->
	</div>
	<!--/.fluid-container#main-container-->

	<!-- 返回顶部  -->
	<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse"> <i
		class="icon-double-angle-up icon-only"></i> </a>

	<!-- 引入 -->
	<script type="text/javascript">
		window.jQuery
				|| document
						.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");
	</script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/ace-elements.min.js"></script>
	<script src="static/js/ace.min.js"></script>
	<!-- 引入 -->

	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<!--引入属于此页面的js -->
	<script type="text/javascript" src="static/js/myjs/toolEmail.js"></script>
	<script type="text/javascript">
		$(function() {
			if ("" != "${pd.msg}")
				alert("${pd.msg}");
		});
	</script>
</body>
</html>