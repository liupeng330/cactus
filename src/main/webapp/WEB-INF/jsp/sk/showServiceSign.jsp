<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
    <title>显示服务</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/multi-select.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/showTable-2-18.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.multi-select.js"></script>
    <style type="text/css">
        table tbody:last-child {
            border-bottom: 2px solid white;
        }
    </style>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <%@ include file="../include/constantUtil.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">显示服务</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <c:set var="LOADBALANCE_KEY" value="loadbalance"></c:set>
                        <c:set var="ONLINE_STATUS_KEY" value="cactusOnlineStatus"></c:set>
                        <table class="table table-responsive">
                            <tr>
                                <th class="wrapTd td-15-percent text-align-center ver-align-middle">
                                    应用
                                </th>
                                <th class="wrapTd td-15-percent text-align-center ver-align-middle">
                                    impl
                                </th>
                                <th class="wrapTd td-10-percent text-align-center ver-align-middle">
                                    version
                                </th>
                                <th class="wrapTd td-15-percent text-align-center ver-align-middle">
                                    负载均衡
                                </th>
                                <th class="wrapTd td-10-percent text-align-center ver-align-middle">
                                    <div class="btn-group btn-group-sm">
                                        <button id="stateText" type="button"
                                                class="btn btn-default btn-sm dropdown-toggle"
                                                data-toggle="dropdown">
                                            状态
                                            <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu ulMenu" role="menu">
                                            <li>
                                                <a id="btn_doubleWeight_${row.index}"
                                                   onclick="showByState('2',this)">
                                                    所有在线
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('1',this)">
                                                    所有下线
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('0',this)">
                                                    部分下线
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('all',this)">
                                                    全部
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                </th>
                                <th class="wrapTd td-35-percent text-align-center ver-align-middle">
                                    操作
                                </th>
                            </tr>

                        </table>
                        <table class="table table-bordered">
                            <c:forEach items="${list}" var="serviceSignAndParams">

                                <tr class="titleBg" state="${serviceSignAndParams.right[ONLINE_STATUS_KEY]}">
                                    <td colspan="6">
                                        <span class="badge">${serviceSignAndParams.right[ZK_NAME]}</span> <a
                                            href="<%=request.getContextPath()%>/provider/showProviders?group=${serviceSignAndParams.left.group}&ipAndPort=${ipAndPort}&serviceKey=${serviceSignAndParams.left.serviceKey}&zkId=${serviceSignAndParams.right[ZKID]}">${serviceSignAndParams.left.serviceInterface}</a>
                                    </td>
                                </tr>
                                <tr class="detailTr" state="${serviceSignAndParams.right[ONLINE_STATUS_KEY]}">
                                    <td class="wrapTd td-10-percent itemContent text-align-center ver-align-middle">

                                        <a href="<%=request.getContextPath()%>/group/showGroup?group=${serviceSignAndParams.left.group}">${serviceSignAndParams.left.group}</a>
                                    </td>

                                    <td class="wrapTd td-10-percent itemContent text-align-center ver-align-middle">
                                            ${serviceSignAndParams.left.serviceGroup}
                                    </td>
                                    <td class="wrapTd td-10-percent itemContent text-align-center ver-align-middle">
                                            ${serviceSignAndParams.left.version}
                                    </td>
                                    <td class="wrapTd td-10-percent itemContent text-align-center ver-align-middle">
                                        <c:if test="${serviceSignAndParams.right[LOADBALANCE_KEY]=='random'}">
                                            随机
                                        </c:if>
                                        <c:if test="${serviceSignAndParams.right[LOADBALANCE_KEY]=='roundrobin'}">
                                            轮循
                                        </c:if>
                                        <c:if test="${serviceSignAndParams.right[LOADBALANCE_KEY]=='leastactive'}">
                                            最少活跃调用数
                                        </c:if>
                                        <c:if test="${serviceSignAndParams.right[LOADBALANCE_KEY]=='consistenthash'}">
                                            一致性哈希
                                        </c:if>
                                        <c:if test="${serviceSignAndParams.right[LOADBALANCE_KEY]!='consistenthash' and serviceSignAndParams.right[LOADBALANCE_KEY]!='roundrobin' and serviceSignAndParams.right[LOADBALANCE_KEY]!='leastactive' and serviceSignAndParams.right[LOADBALANCE_KEY]!='random'}">
                                            ${serviceSignAndParams.right[LOADBALANCE_KEY]}
                                        </c:if>
                                    </td>
                                    <td class="wrapTd td-10-percent itemContent text-align-center ver-align-middle">
                                        <c:if test="${serviceSignAndParams.right[ONLINE_STATUS_KEY]==0}">
                                            <b style="color: blue">部分下线</b>
                                        </c:if>
                                        <c:if test="${serviceSignAndParams.right[ONLINE_STATUS_KEY]==1}">
                                            <b style="color: red">所有下线</b>
                                        </c:if>
                                        <c:if test="${serviceSignAndParams.right[ONLINE_STATUS_KEY]==2}">
                                            <b style="color: green">所有在线</b>
                                        </c:if>
                                    </td>
                                    <td class="wrapTd td-65-percent itemContent text-align-center ver-align-middle">
                                        <a class="btn btn-success btn-sm"
                                           href="<%=request.getContextPath()%>/router/showRouter?group=${serviceSignAndParams.left.group}&serviceKey=${serviceSignAndParams.left.serviceKey}&zkId=${serviceSignAndParams.right[ZKID]}">路由规则</a>
                                        <a class="btn btn-default btn-sm"
                                           href="<%=request.getContextPath()%>/configurator/showConfigurator?group=${serviceSignAndParams.left.group}&serviceGroup=${serviceSignAndParams.left.serviceGroup}&version=${serviceSignAndParams.left.version}&service=${serviceSignAndParams.left.serviceInterface}&address=0.0.0.0&zkId=${serviceSignAndParams.right[ZKID]}">动态配置</a>
                                        <a class="btn btn-primary btn-sm"
                                           href="<%=request.getContextPath()%>/configurator/loadBalancePageRender?group=${serviceSignAndParams.left.group}&ipAndPort=${ipAndPort}&serviceGroup=${serviceSignAndParams.left.serviceGroup}&version=${serviceSignAndParams.left.version}&service=${serviceSignAndParams.left.serviceInterface}&zkId=${serviceSignAndParams.right[ZKID]}">负载均衡</a>
                                        <a class="btn btn-warning btn-sm"
                                           href="<%=request.getContextPath()%>/log/showByCondition?group=${serviceSignAndParams.left.group}&service=${serviceSignAndParams.left.serviceInterface}">操作日志</a>
                                        <a class="btn btn-default btn-sm drainage-on" 
                                        style="display:<c:if test="${drainageStatusMap[serviceSignAndParams.left.serviceInterface]}">none</c:if>;"     
                                        onclick="startDrainage('<%=request.getContextPath()%>/drainage/start', '${serviceSignAndParams.left.group}', '${serviceSignAndParams.left.serviceInterface}','${serviceSignAndParams.right[ZKID]}','${serviceSignAndParams.left.serviceKey}')"
                                            <%--href="<%=request.getContextPath()%>/drainage/start?group=${serviceSignAndParams.left.group}&service=${serviceSignAndParams.left.serviceInterface}&zkId=${serviceSignAndParams.right[ZKID]}&serviceKey=${serviceSignAndParams.left.serviceKey}"--%>>服务引流</a>
                                       
                                       
                                        <a class="btn btn-default btn-sm drainage-off" style="display:<c:if
                                                test="${!drainageStatusMap[serviceSignAndParams.left.serviceInterface]}">none</c:if>;"
                                           onclick="stopDrainage('<%=request.getContextPath()%>/drainage/stop',   '${serviceSignAndParams.left.group}', '${serviceSignAndParams.left.serviceInterface}','${serviceSignAndParams.right[ZKID]}','${serviceSignAndParams.left.serviceKey}')"
                                            <%--href="<%=request.getContextPath()%>/drainage/stop?group=${serviceSignAndParams.left.group}&service=${serviceSignAndParams.left.serviceInterface}&zkId=${serviceSignAndParams.right[ZKID]}&serviceKey=${serviceSignAndParams.left.serviceKey}"--%>>关闭引流</a>

                                        <c:if test="${env!='prod'}">
                                            <a class="btn btn-info btn-sm"
                                               href="<%=request.getContextPath()%>/mockConsumerConfig?group=${serviceSignAndParams.left.group}&ipAndPort=${ipAndPort}&serviceGroup=${serviceSignAndParams.left.serviceGroup}&version=${serviceSignAndParams.left.version}&service=${serviceSignAndParams.left.serviceInterface}&zkId=${serviceSignAndParams.right[ZKID]}&pef=0">
                                                &nbsp;调&nbsp;&nbsp;&nbsp;&nbsp;试&nbsp;</a>
                                        </c:if>
                                        <c:if test="${env!='prod'}">
                                            <a class="btn btn-danger btn-sm"
                                               href="<%=request.getContextPath()%>/mockConsumerConfig?group=${serviceSignAndParams.left.group}&ipAndPort=${ipAndPort}&serviceGroup=${serviceSignAndParams.left.serviceGroup}&version=${serviceSignAndParams.left.version}&service=${serviceSignAndParams.left.serviceInterface}&zkId=${serviceSignAndParams.right[ZKID]}&pef=1">性能测试</a>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr state="${serviceSignAndParams.right[ONLINE_STATUS_KEY]}"
                                    style="border-left: 2px solid white;border-right: 2px solid white;">
                                    <td colspan="6">

                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    </div>


    <div id="drainage_modal" class="modal fade">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">服务引流
                        <span id="is_processing_text" style="display: none; color: red">---引流中</span>
                    </h4>
                </div>

               
                <div class="modal-body">
                    <h4>选择服务provider</h4>                                     
                    <div id="provider_list">
                        <select multiple="multiple" id="provider_list_select">
                        </select>
                    </div>
                    <h4>选择服务method</h4>
                    <div id="method_list">
                        <input type="checkbox" name="methods"  value="all"  checked/><span>全选</span><br/>
                    </div>
                    <h4>输入beta机器地址和端口</h4>
                                                          引流倍数  <input type="text" name="n"   id ="n" maxlength='4' style='width:50px' value='1'/> 默认1倍流量                            
                    <div id="beta_address_list"></div>

                    <input type="hidden" id="drainageUrlParam">
                    <input type="hidden" id="drainageGroupParam">
                    <input type="hidden" id="drainageServiceParam">
                    <input type="hidden" id="drainageZkidParam">
                    <input type="hidden" id="drainageServiceKeyParam">

                    <div id="targetSettingDiv">
                        <div>
                            <textarea style="width: 98%" id="beta_address_textarea" cols="30" rows="5" placeholder=""></textarea>
                        </div>
                                                                         每行输入一个ip和端口号，如:<br/>
                        192.168.1.1:30000<br/>
                        192.168.1.2:30000<br/>
                                                                         或者分组<br/>
                        A=192.168.1.1:30000<br/>
                        A=192.168.1.2:30000<br/>
                        B=192.168.1.3:30000<br/>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="confirmDrainage()">确定</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>


    <div id="drainage_close_modal" class="modal fade">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">关闭引流</h4>
                </div>
                <div class="modal-body">
                    <h4>以下引流任务将被关闭</h4>

                    <input type="hidden" id="drainageUrlParam_close">
                    <input type="hidden" id="drainageGroupParam_close">
                    <input type="hidden" id="drainageServiceParam_close">
                    <input type="hidden" id="drainageZkidParam_close">
                    <input type="hidden" id="drainageServiceKeyParam_close">
                    <input type="hidden" id="drainageServiceNameArray_close">
                    <div>
                        <textarea style="width: 98%" id="close_textarea" cols="30" rows="5"></textarea>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="confirmCloseDrainage()">确定</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>


</div>

<script>


    $('#drainage_modal').on('hide.bs.modal', function (e) {
        $("#is_processing_text").hide();
        $('#provider_list_select').multiSelect('deselect_all');
        
        $('#beta_address_textarea').val('');
        $(".betaIpAndPort_class").prop("checked", false);
        $(".betaIpAndPort_all_class").prop("checked", false);        
    });

    $('#drainage_close_modal').on('hide.bs.modal', function (e) {
        $('#close_textarea').val('');
    });
    

    function confirmCloseDrainage() {
        var url = $('#drainageUrlParam_close').val();
        $.ajax(url, {
            type: "post",
            data: {
                group: $('#drainageGroupParam_close').val(),
                service: $('#drainageServiceParam_close').val(),
                zkId: $('#drainageZkidParam_close').val(),
                serviceKey: $('#drainageServiceKeyParam_close').val(),
                serviceNameListString: $('#drainageServiceNameArray_close').val()
            }
        }).done(function (ret) {
            if (ret.status == 0) {
                alert("引流已关闭");
                window.location.reload();
            } else {
                alert(ret.message);
            }
        })
    }

    function confirmDrainage() { 
        var betaAddrList = [];
        var checkedBox = $('.betaIpAndPort_class:checked');
        for (var i = 0; i < checkedBox.length; i++) {
            betaAddrList.push(checkedBox.val());
        }
        var url = $('#drainageUrlParam').val();
        var betaAddress = $('#beta_address_textarea').val();

        var finalBetaAddrList = [];
        if (betaAddress) {
            var list = betaAddress.split("\n");
            for (var i = 0; i < list.length; i++) {
            	var addr = $.trim(list[i]);
            	finalBetaAddrList.push(addr); 	
            }
        }
        for (var i = 0; i < betaAddrList.length; i++) {
            betaAddrList[i] = $.trim(betaAddrList[i]);
            if(betaAddrList.length > 0){
            	finalBetaAddrList.push(betaAddrList[i]);
            }
        }

        if (finalBetaAddrList.length == 0) {
            alert("请至少选中一台beta机器");
            return;
        }
        var betaAddressString = finalBetaAddrList.join("|");

        var providers = $('#provider_list_select').val();
        if (!providers || providers.length == 0) {
            alert("请至少选中一个provider");
            return;
        }

        $("#is_processing_text").show();
        
        var methods ='';
        if(!$("input[name=methods][value=all]").prop("checked")){
        	$("input[name=methods][value!=all]:checked").each(function(){
        	     methods += ($(this).val()+",");
        	});
        	if(methods.length>1){
        		methods=methods.substring(0,methods.length-1);
        	}
        }

        $.ajax(url, {
            type: "post",
            data: {
                group: $('#drainageGroupParam').val(),
                service: $('#drainageServiceParam').val(),
                zkId: $('#drainageZkidParam').val(),
                serviceKey: $('#drainageServiceKeyParam').val(),
                groupAddressString: betaAddressString,
                n:$("#n").val(),
                providerAddressString: providers.join("|"),
                methodName:methods
            }
        }).done(function (ret) {
            if (ret.status == 0) {
                alert("引流成功");
                $("#is_processing_text").hide();
                $('#drainage_modal').modal('hide');
                window.location.reload();
            } else {
                alert(ret.message);
            }
        })

    }

    function startDrainage(url, group, service, zkId, serviceKey) {
        $('#drainage_modal').modal('show');
        $('#drainageUrlParam').val(url);
        $('#drainageGroupParam').val(group);
        $('#drainageServiceParam').val(service);
        $('#drainageZkidParam').val(zkId);
        $('#drainageServiceKeyParam').val(serviceKey);
    
    
        $.ajax("/drainage/getMethods",{
        	  type: "post",
        	    data: {
                group: $('#drainageGroupParam').val(),
                zkId: $('#drainageZkidParam').val(),
                serviceKey: $('#drainageServiceKeyParam').val()
            }
        }).done(function(ret){
            var methodList = $("#method_list");
            methodList.empty();
            var allCk = $("<input/>").attr("type","checkbox").attr("name","methods").val("all");
            var allLabel = $("<span/>").html("全选");
            methodList.append(allCk).append(allLabel).append("<br/>");
            allCk.change(function(){
            	 var flag = $(this).prop("checked");
            	 $(this).parent().find("input[name=methods]").prop("checked",flag);
            });
         	if (ret.status == 0) {
                var data = ret.data;
                var methodList = $("#method_list");
                $.each(data,function(i,val){
                   var ck = $("<input/>").attr("type","checkbox").attr("name","methods").val(val).change(function(){
                   		 var flag = $(this).prop("checked");
                   		 if(!flag){
                   		    $("input[value=all]").prop("checked",flag);
                   		 }
                   });
                   var label = $("<span/>").html(val);
                   methodList.append(ck).append(label);
                });
            }else {
                alert("获取method时出现错误，reason:" + ret.message);
            }
        });

        $.ajax("/drainage/getProviderAddress", {
            type: "post",
            data: {
                group: $('#drainageGroupParam').val(),
                service: $('#drainageServiceParam').val(),
                zkId: $('#drainageZkidParam').val(),
                serviceKey: $('#drainageServiceKeyParam').val()
            }
        }).done(function (ret) {
            var providerListSelect = $('#provider_list_select');
            providerListSelect.multiSelect();
            if (ret.status == 0) {
                var data = ret.data;
                if (!data || data.length == 0) {
                    alert("该服务没有在线的provider");
                    return;
                }
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var ip = item.ip;
                    var port = item.port;
                    var ipAndPort = ip + ":" + port;
                    providerListSelect.multiSelect('addOption', {value: ipAndPort, text: ipAndPort});
                }
                providerListSelect.multiSelect('refresh');
                $('.ms-container').css('width', '520px');
            } else {
                alert("获取provider时出现错误，reason:" + ret.message);
            }
        });


        $.ajax("/drainage/getBetaAddress", {
            type: "post",
            data: {
                group: $('#drainageGroupParam').val(),
                service: $('#drainageServiceParam').val(),
                zkId: $('#drainageZkidParam').val(),
                serviceKey: $('#drainageServiceKeyParam').val()
            }
        }).done(function (ret) {
            if (ret.status == 0) {
                var container = $('#beta_address_list');
                container.empty();
                var betaAddrArray = ret.data;

                if (betaAddrArray.length > 0) {
                    $('<input />', {
                        type: 'checkbox',
                        class: 'betaIpAndPort_all_class',
                        name: 'betaIpAndPort_group',
                        id: "betaIpAndPort_all"
                    }).appendTo(container);
                    $('<label />', {'for': 'betaIpAndPort_', text: "全选"}).appendTo(container);
                    $('<br/>').appendTo(container);

                    $("#betaIpAndPort_all").change(function () {
                        var attr = $("#betaIpAndPort_all").prop("checked");
                        $(".betaIpAndPort_class").prop("checked", attr);
                        $.each($(".betaIpAndPort_class"),function(){
                         	var targetList = $("#beta_address_textarea").val();  
					       if($(this).prop("checked")){
						   		$("#beta_address_textarea").val(targetList+$(this).val()+'\n');
						   }else{
						        $("#beta_address_textarea").val(targetList.replace($(this).val()+'\n',''));
						   }
                        });
                    });
                }

                for (var i = 0; i < betaAddrArray.length; i++) {
                    var item = betaAddrArray[i];
                    var ip = item.ip;
                    var port = item.port;
                    var ipAndPort = ip + ":" + port;
                    $('<input />', {
                        type: 'checkbox',
                        class: 'betaIpAndPort_class',
                        name: 'betaIpAndPort_group',
                        id: "betaIpAndPort_" + i,
                        value: ipAndPort
                    }).appendTo(container);
                    $('<label />', {'for': 'betaIpAndPort_' + i, text: ipAndPort}).appendTo(container);
                    if (i % 2 == 1) {
                        $('<br/>').appendTo(container);
                    } else {
                        $('<span>&nbsp;&nbsp;&nbsp;</span>').appendTo(container);
                    }
                }
                
				$('.betaIpAndPort_class').change(function(){ 
				       var targetList = $("#beta_address_textarea").val();  
				       if($(this).prop("checked")){
					   		$("#beta_address_textarea").val(targetList+$(this).val()+'\n');
					   }else{
					        $("#beta_address_textarea").val(targetList.replace($(this).val()+'\n',''));
					   }
				});
            }
        });

    }

    function stopDrainage(url, group, service, zkId, serviceKey) {
        $('#drainage_close_modal').modal('show');
        $('#drainageUrlParam_close').val(url);
        $('#drainageGroupParam_close').val(group);
        $('#drainageServiceParam_close').val(service);
        $('#drainageZkidParam_close').val(zkId);
        $('#drainageServiceKeyParam_close').val(serviceKey);

        $.ajax("/drainage/getStopInfo", {
            type: "post",
            data: {
                service: service
            }
        }).done(function (ret) {
            if (ret.status == 0) {
                var data = ret.data;
                if (!data) {
                    return;
                }
                var serviceNameArraySum = [];
                var value = "";
                for(var providerAddr in data) {
                    var serviceNameArray = data[providerAddr];
                    value += providerAddr + "---" + serviceNameArray.join(",") + "\n";
                    for(var i=0; i<serviceNameArray.length; i++) {
                        serviceNameArraySum.push(serviceNameArray[i]);
                    }
                }
                $("#close_textarea").val(value);
                $('#drainageServiceNameArray_close').val(serviceNameArraySum.join("|"));

            } else {
                $("#close_textarea").val("获取引流信息时出现错误");
            }
        });


    }

</script>

</body>
</html>