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
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- jsp文件头和头部 -->
<%@ include file="../admin/top.jsp"%>
</head>
<body>

	<div class="container-fluid" id="main-container">



		<div id="page-content" class="clearfix">

			<div class="row-fluid">


				<div class="span12">
					<div class="widget-box">
						<div
							class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
							<h4 class="lighter">号段权限管理</h4>
						</div>
						<div class="widget-body">


							<div class="widget-main">



								<div class="step-content row-fluid position-relative">

									<label style="float:left;padding-left: 35px;"><span
										class="lbl">号段:</span> </label> <label
										style="float:left;padding-left: 5px;margin-top: -5px;"><input
										type="text" id="authStart" title="号段起始值" placeholder="输入号段起始值"
										style="width:140px;"> </label> <label
										style="float:left;padding-left: 15px;"><span
										class="lbl">至:</span> </label> <label
										style="float:left;padding-left: 5px;margin-top: -5px;"><input
										type="text" id="authEnd" title="号段结束值" placeholder="输入结束值"
										style="width:140px;"> </label> <label
										style="float:left;padding-left: 35px;"><span
										class="lbl">授权给:</span> </label> <label
										style="float:left;padding-left: 5px;margin-top: -5px;"><input
										type="text" id="username" name="username" title="用户名" placeholder="用户名"
										style="width:140px;"> </label>
								</div>

								<div class="step-content row-fluid position-relative">

									<label style="float:left;padding-left: 35px;"><span
										class="lbl">流量套餐:</span> </label> 
										<select  name="taocan" id="taocan" data-placeholder="流量套餐" style="vertical-align:top; width: 150px;">
											<option value="1">3元10M套餐</option>
											<option value="2">5元30M套餐</option>
											<option value="3">10元70M套餐</option>
											<option value="4">20元150M套餐</option>
											<option value="5">30元500M套餐</option>
											<option value="6">40元700M套餐</option>
											<option value="7">50元1G套餐</option>
											<option value="8">70元2G套餐</option>
											<option value="9">100元3G套餐</option>
											<option value="10">130元4G套餐</option>
											<option value="11">180元6G套餐</option>
											<option value="12">280元11G套餐</option>
											<option value="13">460元20G套餐</option>
											<option value="14">650元30G套餐</option>
											<option value="15">1000元50G套餐</option>
					  					</select>
										<!-- 
										<label
										style="float:left;padding-left: 5px;"><input
										name="taocan" id="taocan3" onclick="setType('POST');"
										type="radio" value="icon-edit" checked="checked">&nbsp;
										<span class="lbl">3元10M套餐</span> </label> <label
										style="float:left;padding-left: 25px;"><input
										name="taocan" id="taocan5" onclick="setType('GET');"
										type="radio" value="icon-edit"><span class="lbl">5元30M套餐</span>
										</label>
										 -->
								</div>

								<div class="step-content row-fluid position-relative">

									<label style="float:left;padding-left: 35px;"><span
										class="lbl">预授权短信条数:</span> </label> <label
										style="float:left;padding-left: 5px;"><input
										name="SMS_CNT" id="SMS_CNT" style="width:79px;" type="text"
										value="0" title=""> </label>
								</div>


								<div class="step-content row-fluid position-relative">

									<label style="float:left;padding-left: 35px;"><span
										class="lbl">授权生效日期:</span> </label> <label
										style="float:left;padding-left: 5px;"><input
										class="span10 date-picker" name="authDate" id="authDate"
										value="${pd.authDate}" type="text"
										data-date-format="yyyy-mm-dd" readonly="readonly"
										style="width:88px;" placeholder="生效日期" title="生效日期" /> </label>
								</div>

								<div class="step-content row-fluid position-relative">
									
									<div style="margin-top: -5px;margin-left:305px;">
										&nbsp;&nbsp;<a class="btn btn-small btn-success"
											onclick="sendSever();">请求</a> &nbsp;&nbsp;<a
											class="btn btn-small btn-info" onclick="gReload();">重置</a>
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
		$(top.hangge());
		$(function() {
			//日期框
			$('.date-picker').datepicker();
		});
	</script>
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
	<!--MD5-->
	<script type="text/javascript" src="static/js/jQuery.md5.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<!-- 日期框 -->
	<script type="text/javascript"
		src="static/js/bootstrap-datepicker.min.js"></script>
	<!--引入属于此页面的js -->
	<script type="text/javascript" src="static/js/myjs/auth1.js"></script>

</body>
</html>

