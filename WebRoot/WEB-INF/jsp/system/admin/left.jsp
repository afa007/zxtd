
<%@ page language="java" import="org.apache.shiro.SecurityUtils"%>
<%@ page language="java" import="org.apache.shiro.session.Session"%>
<%@ page language="java" import="org.apache.shiro.subject.Subject"%>
<%@ page language="java" import="com.fh.entity.system.User"%>
<%@ page language="java" import="com.fh.util.Const"%>
<%
	String pathl = request.getContextPath();
	String basePathl = request.getScheme() + "://"
	+ request.getServerName() + ":" + request.getServerPort()
	+ pathl + "/";

	User user = (User) session.getAttribute(Const.SESSION_USER);
%>
<!-- 本页面涉及的js函数，都在head.jsp页面中     -->
<div id="sidebar" class="menu-min">

	<div id="sidebar-shortcuts">

		<div id="sidebar-shortcuts-large">

			<%
			// 1和2是系统管理员
			if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
				if (!"2".equals(user.getROLE_ID()) && !"1".equals(user.getROLE_ID())) {
			%>
			<button class="btn btn-small btn-success" title="">
			<i class="icon-pencil"></i>
			</button>
			<button class="btn btn-small btn-info" title="">
				<i class="icon-eye-open"></i>
			</button>
			<button class="btn btn-small btn-info" title="">
				<i class="icon-eye-open"></i>
			</button>
			<button class="btn btn-small btn-warning" title="">
				<i class="icon-book"></i>
			</button>
			<button class="btn btn-small btn-danger" title="" id="adminmenu">
				<i class="icon-folder-open"></i>
			</button>
			<%
				} else {
			%>
			<button class="btn btn-small btn-success" onclick="changeMenu();"
				title="切换菜单">
				<i class="icon-pencil"></i>
			</button>
			
			<button class="btn btn-small btn-info" title="UI实例"
				onclick="window.open('<%=basePathl%>static/UI_new');">
				<i class="icon-eye-open"></i>
			</button>

			<button class="btn btn-small btn-warning" title="数据字典"
				id="adminzidian" onclick="zidian();">
				<i class="icon-book"></i>
			</button>

			<button class="btn btn-small btn-danger" title="菜单管理" id="adminmenu"
				onclick="menu();">
				<i class="icon-folder-open"></i>
			</button>
			<%
				}
			}
			%>
		</div>

		<div id="sidebar-shortcuts-mini">
			<span class="btn btn-success"></span> <span class="btn btn-info"></span>

			<span class="btn btn-warning"></span> <span class="btn btn-danger"></span>
		</div>

	</div>
	<!-- #sidebar-shortcuts -->


	<ul class="nav nav-list">

		<li class="active" id="fhindex"><a href="main/index"><i
				class="icon-dashboard"></i><span>后台首页</span> </a></li>



		<c:forEach items="${menuList}" var="menu">
			<c:if test="${menu.hasMenu}">
				<li id="lm${menu.MENU_ID }"><a style="cursor:pointer;"
					class="dropdown-toggle"> <i
						class="${menu.MENU_ICON == null ? 'icon-desktop' : menu.MENU_ICON}"></i>
						<span>${menu.MENU_NAME }</span> <b class="arrow icon-angle-down"></b>
				</a>
					<ul class="submenu">
						<c:forEach items="${menu.subMenu}" var="sub">
							<c:if test="${sub.hasMenu}">
								<c:choose>
									<c:when test="${not empty sub.MENU_URL}">
										<li id="z${sub.MENU_ID }"><a style="cursor:pointer;"
											target="mainFrame"
											onclick="siMenu('z${sub.MENU_ID }','lm${menu.MENU_ID }','${sub.MENU_NAME }','${sub.MENU_URL }')"><i
												class="icon-double-angle-right"></i>${sub.MENU_NAME }</a>
										</li>
									</c:when>
									<c:otherwise>
										<li><a href="javascript:void(0);"><i
												class="icon-double-angle-right"></i>${sub.MENU_NAME }</a>
										</li>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</ul></li>
			</c:if>
		</c:forEach>

	</ul>
	<!--/.nav-list-->

	<div id="sidebar-collapse">
		<i class="icon-double-angle-left"></i>
	</div>

</div>
<!--/#sidebar-->

