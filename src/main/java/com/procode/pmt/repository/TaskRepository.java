package com.procode.pmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procode.pmt.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> 
{
    
}
