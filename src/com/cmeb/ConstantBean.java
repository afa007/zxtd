package com.cmeb;

import com.cmeb.util.ConfigUtil;

public class ConstantBean {

	public static final String MAIN = "/WEB-INF/views/index.jsp";

	public static final boolean DEBUG = true;

	public static final String JSON_URI = "http://183.230.96.66:8087/v2/";

	public static final String IP = ConfigUtil.getProp("cmeb_ip");
	public static final String PORT = ConfigUtil.getProp("cmeb_port");
	public static final String VERSION = ConfigUtil.getProp("cmeb_version");
	// 第三方应用平台唯一标示，由全网管理员在运营平台应用上线时分配
	public static final String APPID = ConfigUtil.getProp("cmeb_appid");
	public static final String PASSWD = ConfigUtil.getProp("cmeb_passwd");

	public static final int STATUS_FAIL = -1;
	public static final int STATUS_SUCCESS = 0;

	public static final String driver = ConfigUtil.getProp("ds_driver");
	public static final String url = ConfigUtil.getProp("ds_url");
	public static final String user = ConfigUtil.getProp("ds_user");
	public static final String password = ConfigUtil.getProp("ds_password");

}
