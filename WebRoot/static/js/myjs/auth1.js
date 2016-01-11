var locat = (window.location+'').split('/'); 
$(function(){if('tool'== locat[3]){locat =  locat[0]+'//'+locat[2];}else{locat =  locat[0]+'//'+locat[2]+'/'+locat[3];};});


$(top.hangge());

//重置
function gReload(){
	top.jzts();
	$("#authStart").val('');
	$("#authEnd").val('');
	$("#username").val('');
	$("#authDate").val('');
	$("#SMS_CNT").val('0');
	self.location.reload();
}


function sendSever(){
	
    
	if($("#authStart").val()==""){
		$("#authStart").tips({
			side:3,
            msg:'请输入号段起始值',
            bg:'#AE81FF',
            time:2
        });
		$("#authStart").focus();
		return false;
	}
	var reg = /^\d{13}$/;
    if (!$("#authStart").val().match(reg)){
    	$("#authStart").tips({
			side:3,
            msg:'号段起始值只能为13位数字',
            bg:'#AE81FF',
            time:2
        });
		$("#authStart").focus();
		return false;
    }
	
	if($("#authEnd").val()==""){
		$("#authEnd").tips({
			side:3,
            msg:'请输入号段结束值',
            bg:'#AE81FF',
            time:2
        });
		$("#authEnd").focus();
		return false;
	}
	reg = /^\d{13}$/;
    if (!$("#authEnd").val().match(reg)){
    	$("#authEnd").tips({
			side:3,
            msg:'号段结束值只能为13位数字',
            bg:'#AE81FF',
            time:2
        });
		$("#authEnd").focus();
		return false;
    }
    
	if(parseInt($("#authStart").val()) > $("#authEnd").val()){
		$("#authEnd").tips({
			side:3,
            msg:'号段结束值不能小于起始值',
            bg:'#AE81FF',
            time:2
        });
		$("#authEnd").focus();
		return false;
	}
	
	
	if(parseInt($("#authStart").val()) > $("#authEnd").val()){
		$("#authEnd").tips({
			side:3,
            msg:'号段结束值不能小于起始值',
            bg:'#AE81FF',
            time:2
        });
		$("#authEnd").focus();
		return false;
	}
	
	if($("#username").val()==""){
		$("#username").tips({
			side:3,
            msg:'请输入用户名',
            bg:'#AE81FF',
            time:2
        });
		$("#username").focus();
		return false;
	}
	
	if($("#authDate").val()==""){
		$("#authDate").tips({
			side:3,
            msg:'请输入生效日期',
            bg:'#AE81FF',
            time:2
        });
		$("#authDate").focus();
		return false;
	}
	
	if($("#SMS_CNT").val()==""){
		$("#SMS_CNT").tips({
			side:3,
            msg:'请输入预授权短信数',
            bg:'#AE81FF',
            time:2
        });
		$("#SMS_CNT").focus();
		return false;
	}
	
	top.jzts();
	$.ajax({
		type: "POST",
		url: locat+'/auth.do',
    	data: {authStart:$("#authStart").val(),authEnd:$("#authEnd").val(),username:$("#username").val(),authDate:$("#authDate").val(),SMS_CNT:$("#SMS_CNT").val(),taocan:$("#taocan").val(), tm:new Date().getTime()},
		dataType:'json',
		cache: false,
		success: function(data){
			 $(top.hangge());
			 
			 if("success" == data.errInfo){
				 alert("设置成功！");
			 }else{
				alert("操作失败，请重试！");
				 return;
			 }
		}
	});
}


//js  日期格式
function date2str(x,y) {
     var z ={y:x.getFullYear(),M:x.getMonth()+1,d:x.getDate(),h:x.getHours(),m:x.getMinutes(),s:x.getSeconds()};
     return y.replace(/(y+|M+|d+|h+|m+|s+)/g,function(v) {return ((v.length>1?"0":"")+eval('z.'+v.slice(-1))).slice(-(v.length>2?v.length:2))});
 	};