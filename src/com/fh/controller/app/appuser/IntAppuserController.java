package com.fh.controller.app.appuser;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fh.controller.base.BaseController;
import com.fh.entity.system.User;
import com.fh.service.system.appuser.AppuserService;
import com.fh.util.AppUtil;
import com.fh.util.Const;
import com.fh.util.DateUtil;
import com.fh.util.MD5;
import com.fh.util.PageData;
import com.fh.util.Tools;
import com.google.gson.Gson;

/**
 * 会员-接口类
 * 
 * 相关参数协议： 00 请求失败 01 请求成功 02 返回空值 03 请求协议参数不完整 04 用户名或密码错误 05 FKEY验证失败
 */
@Controller
@RequestMapping(value = "/appuser")
public class IntAppuserController extends BaseController {

	@Resource(name = "appuserService")
	private AppuserService appuserService;

	/**
	 * APP用户登录
	 */
	@RequestMapping(value = "/login")
	@ResponseBody
	public Object loginAppuser() {
		logBefore(logger, "APP用户登录");
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String result = "00";

		Gson gson = new Gson();
		logger.info("pd:" + gson.toJson(pd));
		try {

			String USERNAME = pd.getString("USERNAME");
			if (Tools.checkKey(USERNAME, pd.getString("FKEY"))) { // 检验请求key值是否合法

				if (AppUtil.checkParam("login", pd)) { // 检查参数

					String PASSWORD = pd.getString("PASSWORD");

					String passwd = MD5.md5(PASSWORD); // 密码加密
					pd.put("PASSWORD", passwd);

					System.out.println("USERNAME:" + pd.getString("USERNAME")
							+ ",PASSWORD:" + pd.getString("PASSWORD"));
					pd = appuserService.getUserByNameAndPwd(pd);

					if (pd != null) {

						// 更新登录信息
						pd.put("LAST_LOGIN", DateUtil.getTime().toString());
						appuserService.updateLastLogin(pd);

						// shiro加入身份验证
						Subject subject = SecurityUtils.getSubject();
						UsernamePasswordToken token = new UsernamePasswordToken(
								USERNAME, PASSWORD);
						try {
							subject.login(token);
						} catch (AuthenticationException e) {
							result = "02"; // 身份验证失败
						}

						pd = appuserService.findByUId(pd);
						map.put("pd", pd);
						result = (null == pd) ? "02" : "00"; // 02用户名密码校验成功，读取客户信息失败

					} else {
						result = "01"; // 用户名或者密码错
					}
				} else {
					result = "04"; // 参数缺失
				}
			} else {
				result = "05"; // FKEY值校验失败
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			map.put("result", result);
			logAfter(logger);
		}

		return AppUtil.returnObject(new PageData(), map);
	}

	/**
	 * 根据用户名获取会员信息
	 */
	@RequestMapping(value = "/getAppuserByUm")
	@ResponseBody
	public Object getAppuserByUsernmae() {
		logBefore(logger, "根据用户名获取会员信息");
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String result = "00";

		try {
			if (Tools.checkKey("USERNAME", pd.getString("FKEY"))) { // 检验请求key值是否合法
				if (AppUtil.checkParam("getAppuserByUsernmae", pd)) { // 检查参数
					pd = appuserService.findByUId(pd);

					map.put("pd", pd);
					result = (null == pd) ? "02" : "00";

				} else {
					result = "03";
				}
			} else {
				result = "05";
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			map.put("result", result);
			logAfter(logger);
		}

		return AppUtil.returnObject(new PageData(), map);
	}

}
