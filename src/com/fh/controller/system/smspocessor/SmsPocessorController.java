package com.fh.controller.system.smspocessor;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

import com.cmpp.util.MsgQUtil;
import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.util.AppUtil;
import com.fh.util.ObjectExcelView;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.Tools;
import com.fh.util.Jurisdiction;
import com.fh.service.system.smspocessor.SmsPocessorService;

/**
 * 类名称：SmsPocessorController 创建人：FH 创建时间：2015-11-04
 */
@Controller
@RequestMapping(value = "/smspocessor")
public class SmsPocessorController extends BaseController {

	String menuUrl = "smspocessor/list.do"; // 菜单地址(权限用)
	@Resource(name = "smspocessorService")
	private SmsPocessorService smspocessorService;

	/**
	 * 发短信
	 */
	@RequestMapping(value = "/goSend")
	public ModelAndView goSend() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		mv.setViewName("system/smspocessor/sms");

		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 发短信
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/doSend")
	public ModelAndView doSend() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		String phone = pd.getString("PHONE");
		String content = pd.getString("CONTENT");
		System.out.println("PHONE:" + phone + ", content:" + content);

		if (phone != null) {
			if (phone.contains(";")) {
				String phoneList[] = phone.split(";");
				for (int i = 0; i < phoneList.length; i++) {
					String MSISDN = phoneList[i];

					pd.put("SMSPOCESSOR_ID", this.get32UUID());
					pd.put("MSISDN", MSISDN);
					pd.put("TYPE", "发送");
					pd.put("USERID", pd.getString("USER_ID"));
					pd.put("STATUS", "0"); // 待发送
					pd.put("CREATETIME", new Date());

					smspocessorService.save(pd);

					// 待发送信息放入消息队列，等待1秒，发送不成功则更新短信为发送失败
					if (!MsgQUtil.quene.offer(pd.get("SMSPOCESSOR_ID"), 1,
							TimeUnit.SECONDS)) {
						pd.put("STATUS", "-1"); // 发送失败
						smspocessorService.editStatus(pd);
					}
					
				}
			} else {

				pd.put("SMSPOCESSOR_ID", this.get32UUID());
				pd.put("MSISDN", phone);
				pd.put("TYPE", "发送");
				pd.put("USERID", "");
				pd.put("STATUS", "0");
				pd.put("CREATETIME", new Date());

				smspocessorService.save(pd);
			}
		}

		mv.addObject("msg", "success");
		mv.setViewName("system/smspocessor/sms");

		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 新增
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, "新增SmsPocessor");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("SMSPOCESSOR_ID", this.get32UUID()); // 主键
		smspocessorService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) {
		logBefore(logger, "删除SmsPocessor");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			smspocessorService.delete(pd);
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
		logBefore(logger, "修改SmsPocessor");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		smspocessorService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) {
		logBefore(logger, "列表SmsPocessor");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			page.setPd(pd);
			List<PageData> varList = smspocessorService.list(page); // 列出SmsPocessor列表
			mv.setViewName("system/smspocessor/smspocessor_list");
			mv.addObject("varList", varList);
			mv.addObject("pd", pd);
			mv.addObject(Const.SESSION_QX, this.getHC()); // 按钮权限
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}

	/**
	 * 去新增页面
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() {
		logBefore(logger, "去新增SmsPocessor页面");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			mv.setViewName("system/smspocessor/smspocessor_edit");
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
		logBefore(logger, "去修改SmsPocessor页面");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = smspocessorService.findById(pd); // 根据ID读取
			mv.setViewName("system/smspocessor/smspocessor_edit");
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
		logBefore(logger, "批量删除SmsPocessor");
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
				smspocessorService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, "导出SmsPocessor到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();
			titles.add("手机号码"); // 1
			titles.add("短信类型"); // 2
			titles.add("短信内容"); // 3
			titles.add("用户名"); // 4
			titles.add("创建时间"); // 5
			titles.add("状态"); // 6
			dataMap.put("titles", titles);
			List<PageData> varOList = smspocessorService.listAll(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < varOList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("MSISDN")); // 1
				vpd.put("var2", varOList.get(i).getString("TYPE")); // 2
				vpd.put("var3", varOList.get(i).getString("CONTENT")); // 3
				vpd.put("var4", varOList.get(i).getString("USERID")); // 4
				vpd.put("var5", varOList.get(i).getString("CREATETIME")); // 5
				vpd.put("var6", varOList.get(i).getString("STATUS")); // 6
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
