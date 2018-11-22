package com.demo.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.ILeaveBillDao;
import com.demo.entity.LeaveBill;
import com.demo.service.LeaveBillService;
import com.demo.service.WorkFlowService;
@Service
@Transactional
public class LeaveBillServiceImpl implements LeaveBillService{
	@Autowired
	private WorkFlowService workFlowService;
	@Autowired
	private ILeaveBillDao leaveBillDao;
	@Override
	public void save(LeaveBill leaveBill) {
		leaveBillDao.saveAndFlush(leaveBill);
		workFlowService.startProcess("leaveBill", leaveBill.getUserId(), leaveBill.getId());
	}

	@Override
	public LeaveBill findById(String id) {
		LeaveBill leaveBill = leaveBillDao.findById(id);
		return leaveBill;
	}

	@Override
	public List<LeaveBill> findByUserId(String userId) {
		return leaveBillDao.findByUserId(userId);
	}

}
