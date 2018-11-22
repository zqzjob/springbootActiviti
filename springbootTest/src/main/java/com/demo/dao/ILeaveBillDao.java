package com.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.entity.LeaveBill;
@Repository
public interface ILeaveBillDao extends JpaRepository<LeaveBill, String>,JpaSpecificationExecutor<LeaveBill>{

	List<LeaveBill> findByUserId(String userId);

	LeaveBill findById(String id);


}
