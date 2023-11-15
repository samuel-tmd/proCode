package com.procode.pmt.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.procode.pmt.model.Project;
import com.procode.pmt.model.Task;
import com.procode.pmt.repository.TaskRepository;
import com.procode.pmt.repository.ProjectRepository;

@RestController
@RequestMapping("/tasks")
public class TaskController 
{
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity listAll()
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            returnData.put("data", taskRepository.findAll());
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(returnData, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity create(@RequestBody Task newTask)
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            if(!hasProjectId(newTask))
            {
                returnData.put("message", "ProjectId cannot be null");
                return new ResponseEntity(returnData, HttpStatus.PRECONDITION_FAILED);
            }
            Optional<Project> projectOptional = projectRepository.findById(newTask.getProjectId());
            if(!projectOptional.isPresent())
            {
                returnData.put("message", "Project not found");
                return new ResponseEntity(returnData, HttpStatus.NOT_FOUND);
            }
            Project parentProject = projectOptional.get();
            newTask.setParentProject(parentProject);
            newTask = taskRepository.save(newTask);
            parentProject.addTask(newTask);
            projectRepository.save(parentProject);
            List<Task> tasks = new ArrayList<Task>();
            tasks.add(newTask);
            returnData.put("data", tasks);
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(returnData, HttpStatus.CREATED);
    }
    @PatchMapping
    public ResponseEntity update(@RequestBody Task taskToUpdate) 
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            if(!hasId(taskToUpdate))
            {
                returnData.put("message", "Id cannot be null");
                return new ResponseEntity(returnData, HttpStatus.PRECONDITION_FAILED);
            }
            Optional<Task> taskOptional = taskRepository.findById(taskToUpdate.getId());
            if(!taskOptional.isPresent())
            {
                returnData.put("message", "Task not found");
                return new ResponseEntity(returnData, HttpStatus.NOT_FOUND);
            }
            Task existingTask = taskOptional.get();
            if(!hasProjectId(taskToUpdate))
            {
                returnData.put("message", "ProjectId cannot be null");
                return new ResponseEntity(returnData, HttpStatus.PRECONDITION_FAILED);
            }
            if(existingTask.getProjectId() != taskToUpdate.getProjectId())
            {
                returnData.put("message", "It's not possible to change the Task's Project.");
                return new ResponseEntity(returnData, HttpStatus.BAD_REQUEST);
            }
            taskToUpdate = taskRepository.save(taskToUpdate);
            List<Task> tasks = new ArrayList<Task>();
            tasks.add(taskToUpdate);
            returnData.put("data", tasks);
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(returnData, HttpStatus.OK);
    }
    @DeleteMapping
    public ResponseEntity delete(@RequestBody Task taskToDelete) 
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            if(!hasId(taskToDelete))
            {
                returnData.put("message", "Id cannot be null");
                return new ResponseEntity(returnData, HttpStatus.PRECONDITION_FAILED);
            }
            Optional<Project> projectOptional = projectRepository.findById(taskToDelete.getProjectId());
            if(!projectOptional.isPresent())
            {
                returnData.put("message", "Project not found");
                return new ResponseEntity(returnData, HttpStatus.NOT_FOUND);
            }
            Project parentProject = projectOptional.get();
            parentProject.removeTask(taskToDelete);
            projectRepository.save(parentProject);
            taskRepository.delete(taskToDelete);
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    public static Boolean hasId(Task taskToVerify)
    {
        if(taskToVerify.getId() != null)
        {
            return true;
        }
        return false;
    }
    public static Boolean hasProjectId(Task taskToVerify)
    {
        if(taskToVerify.getProjectId() != null)
        {
            return true;
        }
        return false;
    }
}
