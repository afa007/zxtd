package com.fh.service.system.smspocessor;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

@Service("smspocessorService")
public class SmsPocessorService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/*
	 * 新增
	 */
	public void save(PageData pd) throws Exception {
		dao.save("SmsPocessorMapper.save", pd);
	}

	/*
	 * 删除
	 */
	public void delete(PageData pd) throws Exception {
		dao.delete("SmsPocessorMapper.delete", pd);
	}

	/*
	 * 修改
	 */
	public void edit(PageData pd) throws Exception {
		dao.update("SmsPocessorMapper.edit", pd);
	}

	/*
	 * 更新短信状态
	 */
	public void editStatus(PageData pd) throws Exception {
		dao.update("SmsPocessorMapper.editStatus", pd);
	}

	/*
	 * 列表
	 */
	public List<PageData> list(Page page) throws Exception {
		return (List<PageData>) dao.findForList(
				"SmsPocessorMapper.datalistPage", page);
	}

	/*
	 * 列表(全部)
	 */
	public List<PageData> listAll(PageData pd) throws Exception {
		return (List<PageData>) dao
				.findForList("SmsPocessorMapper.listAll", pd);
	}

	/*
	 * 分页查询短信列表，查询数目
	 */
	public Long listAllCntByPage(PageData pd) throws Exception {
		return (Long) dao.findForObject("SmsPocessorMapper.listAllCntByPage", pd);
	}

	/*
	 * 分页查询短信列表
	 */
	public List<PageData> listAllByPage(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"SmsPocessorMapper.listAllByPage", pd);
	}

	/*
	 * 通过id获取数据
	 */
	public PageData findById(PageData pd) throws Exception {
		return (PageData) dao.findForObject("SmsPocessorMapper.findById", pd);
	}

	/*
	 * 批量删除
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		dao.delete("SmsPocessorMapper.deleteAll", ArrayDATA_IDS);
	}

}
