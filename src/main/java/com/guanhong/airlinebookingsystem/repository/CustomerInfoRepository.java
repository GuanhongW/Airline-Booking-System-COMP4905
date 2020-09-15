package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.CustomerInfo;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {
    public CustomerInfo findCustomerInfoById(long id);
}
