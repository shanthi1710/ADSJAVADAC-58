package com.cdac.acts.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.acts.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
