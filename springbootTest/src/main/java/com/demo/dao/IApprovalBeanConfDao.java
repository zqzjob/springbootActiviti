package com.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.entity.ApprovalBeanConf;
@Repository
public interface IApprovalBeanConfDao extends JpaRepository<ApprovalBeanConf, String>,JpaSpecificationExecutor<ApprovalBeanConf>{

	ApprovalBeanConf getApprovalSerBeanById(String formKey);

}
