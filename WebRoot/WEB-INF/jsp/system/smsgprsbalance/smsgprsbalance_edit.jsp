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
		<meta charset="utf-8" />
		<title></title>
		<meta name="description" content="overview & stats" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link href="static/css/bootstrap.min.css" rel="stylesheet" />
		<link href="static/css/bootstrap-responsive.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="static/css/font-awesome.min.css" />
		<!-- 下拉框 -->
		<link rel="stylesheet" href="static/css/chosen.css" />
		
		<link rel="stylesheet" href="static/css/ace.min.css" />
		<link rel="stylesheet" href="static/css/ace-responsive.min.css" />
		<link rel="stylesheet" href="static/css/ace-skins.min.css" />
		
		<link rel="stylesheet" href="static/css/datepicker.css" /><!-- 日期框 -->
		<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		
<script type="text/javascript">
	
	
	//保存
	function save(){
			if($("#DATE").val()==""){
			$("#DATE").tips({
				side:3,
	            msg:'请输入日期',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#DATE").focus();
			return false;
		}
		if($("#SMS").val()==""){
			$("#SMS").tips({
				side:3,
	            msg:'请输入短信',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#SMS").focus();
			return false;
		}
		if($("#GPRS").val()==""){
			$("#GPRS").tips({
				side:3,
	            msg:'请输入流量',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#GPRS").focus();
			return false;
		}
		if($("#BALANCE").val()==""){
			$("#BALANCE").tips({
				side:3,
	            msg:'请输入余额',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#BALANCE").focus();
			return false;
		}
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
		if($("#OPDATE").val()==""){
			$("#OPDATE").tips({
				side:3,
	            msg:'请输入操作时间',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#OPDATE").focus();
			return false;
		}
		$("#Form").submit();
		$("#zhongxin").hide();
		$("#zhongxin2").show();
	}
	
</script>
	</head>
<body>
	<form action="smsgprsbalance/${msg }.do" name="Form" id="Form" method="post">
		<input type="hidden" name="SMSGPRSBALANCE_ID" id="SMSGPRSBALANCE_ID" value="${pd.SMSGPRSBALANCE_ID}"/>
		<div id="zhongxin">
		<table id="table_report" class="table table-striped table-bordered table-hover">
			<tr>
				<td style="width:70px;text-align: right;padding-top: 13px;">日期:</td>
				<td><input class="span10 date-picker" name="DATE" id="DATE" value="${pd.DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="日期" title="日期"/></td>
			</tr>
			<tr>
				<td style="width:70px;text-align: right;padding-top: 13px;">短信:</td>
				<td><input type="number" name="SMS" id="SMS" value="${pd.SMS}" maxlength="32" placeholder="这里输入短信" title="短信"/></td>
			</tr>
			<tr>
				<td style="width:70px;text-align: right;padding-top: 13px;">流量:</td>
				<td><input type="number" name="GPRS" id="GPRS" value="${pd.GPRS}" maxlength="32" placeholder="这里输入流量" title="流量"/></td>
			</tr>
			<tr>
				<td style="width:70px;text-align: right;padding-top: 13px;">余额:</td>
				<td><input type="number" name="BALANCE" id="BALANCE" value="${pd.BALANCE}" maxlength="32" placeholder="这里输入余额" title="余额"/></td>
			</tr>
			<tr>
				<td style="width:70px;text-align: right;padding-top: 13px;">卡号:</td>
				<td><input type="text" name="MSISDN" id="MSISDN" value="${pd.MSISDN}" maxlength="32" placeholder="这里输入卡号" title="卡号"/></td>
			</tr>
			<tr>
				<td style="width:70px;text-align: right;padding-top: 13px;">操作时间:</td>
				<td><input class="span10 date-picker" name="OPDATE" id="OPDATE" value="${pd.OPDATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="操作时间" title="操作时间"/></td>
			</tr>
			<tr>
				<td style="text-align: center;" colspan="10">
					<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
					<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
				</td>
			</tr>
		</table>
		</div>
		
		<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
		
	</form>
	
	
		<!-- 引入 -->
		<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
		<script src="static/js/bootstrap.min.js"></script>
		<script src="static/js/ace-elements.min.js"></script>
		<script src="static/js/ace.min.js"></script>
		<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
		<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script><!-- 日期框 -->
		<script type="text/javascript">
		$(top.hangge());
		$(function() {
			
			//单选框
			$(".chzn-select").chosen(); 
			$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 
			
			//日期框
			$('.date-picker').datepicker();
			
		});
		
		</script>
</body>
</html>