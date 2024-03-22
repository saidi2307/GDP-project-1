package com.team01.wholeSaleSystem.repository;

import com.team01.wholeSaleSystem.entity.Assistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistanceRepository extends JpaRepository<Assistance, Long> {

}
