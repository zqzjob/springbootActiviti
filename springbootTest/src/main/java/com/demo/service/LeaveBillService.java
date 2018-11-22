package com.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.demo.entity.LeaveBill;
public interface LeaveBillService {
	void save(LeaveBill leaveBill);
	LeaveBill findById(String id);
	List<LeaveBill> findByUserId(String userId);
}
