package com.procode.pmt.model;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore; 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Project 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "parentProject")
    private List<Task> tasks = new ArrayList<Task>();

    public void addTask(Task newTask)
    {
        this.tasks.add(newTask);
    }
    public void removeTask(Task taskToRemove)
    {
        for(int i = 0; i < tasks.size(); i++)
        {
            Task existingTask = tasks.get(i);
            if(taskToRemove.getId() == existingTask.getId())
            {
                tasks.remove(i);
            }
        }
    }
}
