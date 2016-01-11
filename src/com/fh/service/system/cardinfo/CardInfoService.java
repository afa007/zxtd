package com.fh.service.system.cardinfo;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;


@Service("cardinfoService")
public class CardInfoService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	* 新增
	*/
	public void save(PageData pd)throws Exception{
		dao.save("CardInfoMapper.save", pd);
	}
	
	/*
	* 删除
	*/
	public void delete(PageData pd)throws Exception{
		dao.delete("CardInfoMapper.delete", pd);
	}
	
	/*
	* 修改
	*/
	public void edit(PageData pd)throws Exception{
		dao.update("CardInfoMapper.edit", pd);
	}
	

	/*
	* 守护程序修改
	*/
	public void editByDeamon(PageData pd)throws Exception{
		dao.update("CardInfoMapper.editByDeamon", pd);
	}
	
	
	/*
	*列表
	*/
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("CardInfoMapper.datalistPage", page);
	}
	
	/*
	*列表(全部)
	*/
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("CardInfoMapper.listAll", pd);
	}

	/*
	*列表(全部)
	*/
	public List<PageData> listAllWithEmpty(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("CardInfoMapper.listAllWithEmpty", pd);
	}
	
	/*
	* 通过id获取数据
	*/
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("CardInfoMapper.findById", pd);
	}

	/*
	* 通过id获取数据
	*/
	public PageData findByIdMSISDN(PageData pd)throws Exception{
		return (PageData)dao.findForObject("CardInfoMapper.findByIdMSISDN", pd);
	}
	
	/*
	* 通过id和owner获取数据
	*/
	public PageData findByIdAndOwner(PageData pd)throws Exception{
		return (PageData)dao.findForObject("CardInfoMapper.findByIdAndOwner", pd);
	}
	
	/*
	* 批量删除
	*/
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("CardInfoMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/*
	 * 更新权限
	 * 
	 * */
	public int updateAuthByCardList(PageData pd) throws Exception{
		return (Integer) dao.update("CardInfoMapper.updateAuthByCardList", pd);
	}
	
	/*
	* 更新权限前查询，根据号段，查询条数
	*/
	public int getToUpdateAuthCardNum(PageData pd)throws Exception{
		return (Integer)dao.findForObject("CardInfoMapper.getToUpdateAuthCardNum", pd);
	}
}

