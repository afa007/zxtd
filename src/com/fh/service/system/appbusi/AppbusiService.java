package com.fh.service.system.appbusi;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.util.PageData;

@Service("appbusiService")
public class AppbusiService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	// ======================================================================================

	/*
	 * 按日期查询SMS短信和GPRS历史使用情况
	 */
	public List<PageData> qryusedbydate(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("AppbusiMapper.qryusedbydate",
				pd);
	}

	/*
	 * 查询码号信息
	 */
	public PageData qrycardinfo(PageData pd) throws Exception {
		return (PageData) dao.findForObject("AppbusiMapper.qrycardinfo", pd);
	}

	// ======================================================================================

}
