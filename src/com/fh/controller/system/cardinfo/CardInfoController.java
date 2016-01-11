package com.fh.controller.system.cardinfo;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cmeb.util.CmebUtil;
import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.entity.system.User;
import com.fh.util.AppUtil;
import com.fh.util.ObjectExcelView;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Jurisdiction;
import com.fh.service.system.cardinfo.CardInfoService;
import com.google.gson.Gson;

/**
 * 类名称：CardInfoController 创建人：FH 创建时间：2015-11-04
 */
@Controller
@RequestMapping(value = "/cardinfo")
public class CardInfoController extends BaseController {

	String menuUrl = "cardinfo/list.do"; // 菜单地址(权限用)
	@Resource(name = "cardinfoService")
	private CardInfoService cardinfoService;

	private CmebUtil cmeb = new CmebUtil();

	private Gson gson = new Gson();

	/**
	 * 去分配卡号
	 */
	@RequestMapping(value = "/goAuth")
	public ModelAndView goAuth() throws Exception {
		ModelAndView mv = this.getModelAndView();

		PageData pd = new PageData();
		pd = this.getPageData();
		logger.info("pd:" + gson.toJson(pd));

		// 从session获取用户信息
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User) session.getAttribute(Const.SESSION_USER);
		if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
			if (!"2".equals(user.getROLE_ID())
					&& !"1".equals(user.getROLE_ID())) {
				pd.put("USERID", user.getUSERNAME());
			}
		}

		mv.setViewName("system/cardinfo/auth1");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 分配卡号前，查询要授权的卡的数目
	 */
	@RequestMapping(value = "/checkAuthNum")
	@ResponseBody
	public Object checkAuthNum() {
		Map<String, String> map = new HashMap<String, String>();

		PageData pd = new PageData();
		pd = this.getPageData();
		logger.info("pd:" + gson.toJson(pd));

		map.put("errInfo", "success"); // 状态信息

		// 从session获取用户信息
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User) session.getAttribute(Const.SESSION_USER);
		if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
			if (!"2".equals(user.getROLE_ID())
					&& !"1".equals(user.getROLE_ID())) {
				pd.put("USERID", user.getUSERNAME());
			}
		}

		try {
			int toUpdateAuthNum = cardinfoService.getToUpdateAuthCardNum(pd);
			pd.put("toupdate", toUpdateAuthNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return AppUtil.returnObject(new PageData(), map);
	}

	/**
	 * 分配卡号
	 */
	@RequestMapping(value = "/auth")
	@ResponseBody
	public Object auth() {
		Map<String, String> map = new HashMap<String, String>();

		PageData pd = new PageData();
		pd = this.getPageData();
		logger.info("pd:" + gson.toJson(pd));

		map.put("errInfo", "success"); // 状态信息

		// 从session获取用户信息
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User) session.getAttribute(Const.SESSION_USER);
		if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
			if (!"2".equals(user.getROLE_ID())
					&& !"1".equals(user.getROLE_ID())) {
				pd.put("USERID", user.getUSERNAME());
			}
		}

		try {
			// 更新失败
			if (cardinfoService.updateAuthByCardList(pd) <= 0) {
				map.put("errInfo", "error");
			}
		} catch (Exception e) {
			map.put("errInfo", "error");
			e.printStackTrace();
		}

		return AppUtil.returnObject(new PageData(), map);
	}

	/**
	 * 实时信息查询
	 */
	@RequestMapping(value = "/now")
	public ModelAndView now() throws Exception {
		ModelAndView mv = this.getModelAndView();

		PageData pd = new PageData();
		pd = this.getPageData();
		logger.info("pd:" + gson.toJson(pd));

		String resultInfo = "";

		// 从session获取用户信息
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User) session.getAttribute(Const.SESSION_USER);
		if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
			if (!"2".equals(user.getROLE_ID())
					&& !"1".equals(user.getROLE_ID())) {
				pd.put("USERID", user.getUSERNAME());
			}
		}

		String msisdn = pd.getString("msisdn");
		pd.put("MSISDN", msisdn);
		if (msisdn != null && !"".equals(msisdn)
				&& cardinfoService.findByIdMSISDN(pd) == null) {
			resultInfo = "该卡在号码库中不存在！";
		}

		if (msisdn != null && !"".equals(msisdn)) {
			if (pd.get("USERID") != null && !"".equals(pd.get("USERID"))
					&& cardinfoService.findByIdAndOwner(pd) == null) {
				logger.info("该卡未在您的名下，不能操作！");
				pd.put("msg", "该卡未在您的名下，不能操作！");
			} else {

				// GPRS在线信息
				HashMap<String, Object> gprs = cmeb.gprsrealsingle(msisdn);
				if (gprs != null) {
					String gprsstatus = "";
					if ("00".equals(gprs.get("gprsstatus"))) {
						gprsstatus = "在线";

						pd.put("ip", gprs.get("ip"));
						pd.put("apn", gprs.get("apn"));
						if (gprs.get("rat") != null
								&& !"".equals(gprs.get("rat"))) {
							if ("1".equals(gprs.get("rat"))) {
								pd.put("rat", gprs.get("3G"));
							} else if ("2".equals(gprs.get("rat"))) {
								pd.put("rat", gprs.get("2G"));
							} else {
								pd.put("rat", "未知");
							}
						} else {
							pd.put("rat", "未知");
						}
					} else {
						gprsstatus = "离线";

						pd.put("ip", gprs.get("ip"));
						pd.put("apn", gprs.get("apn"));
						if (gprs.get("rat") != null
								&& !"".equals(gprs.get("rat"))) {
							if ("1".equals(gprs.get("rat"))) {
								pd.put("rat", gprs.get("3G"));
							} else if ("2".equals(gprs.get("rat"))) {
								pd.put("rat", gprs.get("2G"));
							} else {
								pd.put("rat", "未知");
							}
						} else {
							pd.put("rat", "未知");
						}
					}
					pd.put("gprsstatus", gprsstatus);
				}

				// 用户卡状态
				String userstatus = cmeb.userstatusrealsingle(msisdn);
				pd.put("userstatus", userstatus);

				// 开关机状态
				String onoff = cmeb.onandoffrealsingle(msisdn);
				pd.put("onoff", onoff);

				// 当月GPRS使用量
				long gprsused = cmeb.gprsusedinfosingle(msisdn);
				if (gprsused < 0) {
					pd.put("gprsused", "未知");
				} else {
					pd.put("gprsused", gprsused);
				}
				// 用户当月短信查询
				long smsused = cmeb.smsusedinfosingle(msisdn);
				if (smsused < 0) {
					pd.put("smsused", "未知");
				} else {
					pd.put("smsused", smsused);
				}
				// 用户余额
				double balance = cmeb.balancerealsingle(msisdn);
				if (balance < 0) {
					pd.put("balance", "未知");
				} else {
					pd.put("balance", balance);
				}
			}

		}

		pd.put("msg", resultInfo);

		mv.setViewName("system/cardinfo/now");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) {
		logBefore(logger, "列表CardInfo");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();

		try {
			pd = this.getPageData();

			// 从session获取用户信息
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();
			User user = (User) session.getAttribute(Const.SESSION_USER);
			if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
				if (!"2".equals(user.getROLE_ID())
						&& !"1".equals(user.getROLE_ID())) {
					pd.put("USERID", user.getUSERNAME());
				}
			}

			logger.info("pd:" + gson.toJson(pd));

			page.setPd(pd);
			List<PageData> varList = cardinfoService.list(page); // 列出CardInfo列表
			mv.setViewName("system/cardinfo/cardinfo_list");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC()); // 按钮权限
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/**
	 * 新增
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, "新增CardInfo");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		logger.info("pd:" + gson.toJson(pd));

		pd.put("CARDINFO_ID", this.get32UUID()); // 主键
		cardinfoService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) {
		logBefore(logger, "删除CardInfo");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			cardinfoService.delete(pd);
			out.write("success");
			out.close();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}

	}

	/**
	 * 修改
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView edit() throws Exception {
		logBefore(logger, "修改CardInfo");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		cardinfoService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 去新增页面
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() {
		logBefore(logger, "去新增CardInfo页面");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			mv.setViewName("system/cardinfo/cardinfo_edit");
			mv.addObject("msg", "save");
			mv.addObject("pd", pd);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/**
	 * 去修改页面
	 */
	@RequestMapping(value = "/goEdit")
	public ModelAndView goEdit() {
		logBefore(logger, "去修改CardInfo页面");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = cardinfoService.findById(pd); // 根据ID读取
			mv.setViewName("system/cardinfo/cardinfo_edit");
			mv.addObject("msg", "edit");
			mv.addObject("pd", pd);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/**
	 * 批量删除
	 */
	@RequestMapping(value = "/deleteAll")
	@ResponseBody
	public Object deleteAll() {
		logBefore(logger, "批量删除CardInfo");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "dell")) {
			return null;
		} // 校验权限
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			pd = this.getPageData();
			List<PageData> pdList = new ArrayList<PageData>();
			String DATA_IDS = pd.getString("DATA_IDS");
			if (null != DATA_IDS && !"".equals(DATA_IDS)) {
				String ArrayDATA_IDS[] = DATA_IDS.split(",");
				cardinfoService.deleteAll(ArrayDATA_IDS);
				pd.put("msg", "ok");
			} else {
				pd.put("msg", "no");
			}
			pdList.add(pd);
			map.put("list", pdList);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			logAfter(logger);
		}
		return AppUtil.returnObject(pd, map);
	}

	/*
	 * 导出到excel
	 * 
	 * @return
	 */
	@RequestMapping(value = "/excel")
	public ModelAndView exportExcel() {
		logBefore(logger, "导出CardInfo到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		// 从session获取用户信息
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User) session.getAttribute(Const.SESSION_USER);
		// 1和2是系统管理员
		if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
			if (!"2".equals(user.getROLE_ID())
					&& !"1".equals(user.getROLE_ID())) {
				pd.put("USERID", user.getUSERNAME());
			}
		}

		logger.info("pd:" + gson.toJson(pd));

		try {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();
			titles.add("卡号"); // 1
			titles.add("imsi"); // 2
			titles.add("iccid"); // 3
			titles.add("状态"); // 4
			titles.add("所有者"); // 5
			dataMap.put("titles", titles);
			List<PageData> varOList = cardinfoService.listAll(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < varOList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("MSISDN")); // 1
				vpd.put("var2", varOList.get(i).getString("IMSI")); // 2
				vpd.put("var3", varOList.get(i).getString("ICCID")); // 3
				vpd.put("var4", varOList.get(i).getString("STATUS")); // 4
				vpd.put("var5", varOList.get(i).getString("OWNER")); // 5
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView();
			mv = new ModelAndView(erv, dataMap);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/* ===============================权限================================== */
	public Map<String, String> getHC() {
		Subject currentUser = SecurityUtils.getSubject(); // shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>) session.getAttribute(Const.SESSION_QX);
	}

	/* ===============================权限================================== */

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,
				true));
	}
}
