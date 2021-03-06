package com.fh.controller.system.smsgprsbalance;

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

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.entity.system.User;
import com.fh.service.system.smsgprsbalance.SmsgprsbalanceService;
import com.fh.util.AppUtil;
import com.fh.util.ObjectExcelView;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Jurisdiction;
import com.google.gson.Gson;

/**
 * 类名称：SmsgprsbalanceController 创建人：FH 创建时间：2015-11-04
 */
@Controller
@RequestMapping(value = "/smsgprsbalance")
public class SmsgprsbalanceController extends BaseController {

	String menuUrl = "smsgprsbalance/list.do"; // 菜单地址(权限用)
	@Resource(name = "smsgprsbalanceService")
	private SmsgprsbalanceService smsgprsbalanceService;

	private Gson gson = new Gson();

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) {
		logBefore(logger, "列表Smsgprsbalance");
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
			// 1和2是系统管理员
			if (user.getROLE_ID() != null && !"".equals(user.getROLE_ID())) {
				if (!"2".equals(user.getROLE_ID())
						&& !"1".equals(user.getROLE_ID())) {
					pd.put("USERID", user.getUSERNAME());
				}
			}

			logger.info("pd:" + gson.toJson(pd));

			page.setPd(pd);
			List<PageData> varList = smsgprsbalanceService.list(page); // 列出Smsgprsbalance列表
			mv.setViewName("system/smsgprsbalance/smsgprsbalance_list");
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
		logBefore(logger, "新增Smsgprsbalance");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("SMSGPRSBALANCE_ID", this.get32UUID()); // 主键
		smsgprsbalanceService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) {
		logBefore(logger, "删除Smsgprsbalance");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			smsgprsbalanceService.delete(pd);
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
		logBefore(logger, "修改Smsgprsbalance");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		smsgprsbalanceService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 去新增页面
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() {
		logBefore(logger, "去新增Smsgprsbalance页面");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			mv.setViewName("system/smsgprsbalance/smsgprsbalance_edit");
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
		logBefore(logger, "去修改Smsgprsbalance页面");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = smsgprsbalanceService.findById(pd); // 根据ID读取
			mv.setViewName("system/smsgprsbalance/smsgprsbalance_edit");
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
		logBefore(logger, "批量删除Smsgprsbalance");
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
				smsgprsbalanceService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, "导出Smsgprsbalance到excel");
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
			titles.add("日期"); // 1
			titles.add("短信"); // 2
			titles.add("流量"); // 3
			titles.add("余额"); // 4
			titles.add("卡号"); // 5
			titles.add("操作时间"); // 6
			dataMap.put("titles", titles);
			List<PageData> varOList = smsgprsbalanceService.listAll(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < varOList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("DATE")); // 1
				vpd.put("var2", varOList.get(i).get("SMS").toString()); // 2
				vpd.put("var3", varOList.get(i).get("GPRS").toString()); // 3
				vpd.put("var4", varOList.get(i).get("BALANCE").toString()); // 4
				vpd.put("var5", varOList.get(i).getString("MSISDN")); // 5
				vpd.put("var6", varOList.get(i).getString("OPDATE")); // 6
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
