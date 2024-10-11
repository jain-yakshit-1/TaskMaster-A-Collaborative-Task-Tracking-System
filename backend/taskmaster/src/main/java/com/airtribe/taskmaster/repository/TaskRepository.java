package com.airtribe.taskmaster.repository;

import com.airtribe.taskmaster.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByStatus(String status);
    List<Task> findByTitleContainingOrDescriptionContaining(String title, String description);
    List<Task> findByAssignedToId(Long userId);
}