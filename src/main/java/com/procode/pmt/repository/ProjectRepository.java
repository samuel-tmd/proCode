package com.procode.pmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procode.pmt.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> 
{
    
}
