package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {

}
