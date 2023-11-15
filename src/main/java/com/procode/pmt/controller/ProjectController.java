package com.procode.pmt.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.procode.pmt.repository.ProjectRepository;
import com.procode.pmt.repository.TaskRepository;

@RestController
@RequestMapping("/projects")
public class ProjectController 
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
            returnData.put("data", projectRepository.findAll());
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(returnData, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity create(@RequestBody Project newProject)
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            returnData.put("data", projectRepository.save(newProject));
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(returnData, HttpStatus.CREATED);
    }
    @PatchMapping
    public ResponseEntity update(@RequestBody Project newProject) 
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            if(!hasId(newProject))
            {
                returnData.put("message", "Id cannot be null");
                return new ResponseEntity(returnData, HttpStatus.PRECONDITION_FAILED);
            }
            returnData.put("data", projectRepository.save(newProject));
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(returnData, HttpStatus.OK);
    }
    @DeleteMapping
    public ResponseEntity delete(@RequestBody Project projectToDelete) 
    {
        Map returnData = new HashMap<String, Object>();
        try 
        {
            if(!hasId(projectToDelete))
            {
                returnData.put("message", "Id cannot be null");
                return new ResponseEntity(returnData, HttpStatus.PRECONDITION_FAILED);
            }
            Optional<Project> projectOptional = projectRepository.findById(projectToDelete.getId());
            if(!projectOptional.isPresent())
            {
                returnData.put("message", "Project not found");
                return new ResponseEntity(returnData, HttpStatus.NOT_FOUND);
            }
            Project parentProject = projectOptional.get();
            for(Task existingTask : parentProject.getTasks())
            {
                taskRepository.delete(existingTask);
            }
            projectRepository.delete(projectToDelete);
        } 
        catch (Exception e) 
        {
            returnData.put("message", e.getMessage());
            return new ResponseEntity(returnData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    public static Boolean hasId(Project taskToVerify)
    {
        if(taskToVerify.getId() != null)
        {
            return true;
        }
        return false;
    }
}
