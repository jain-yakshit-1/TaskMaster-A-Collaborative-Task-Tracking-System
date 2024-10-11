package com.airtribe.taskmaster.controller;

import com.airtribe.taskmaster.model.Team;
import com.airtribe.taskmaster.model.User;
import com.airtribe.taskmaster.service.TeamService;
import com.airtribe.taskmaster.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@Validated
public class TeamController {
    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

     @Operation(summary = "Create a new team", description = "Create a new team.")
    @PostMapping
    public ResponseEntity<Team> createTeam(@Valid @RequestBody Team team) {
        Team createdTeam = teamService.createTeam(team);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a team by ID", description = "Retrieve details for a specific team by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id);
        if (team == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @Operation(summary = "Delete a team", description = "Delete a team by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Invite a user to a team", description = "Invite a user to join a specific team.")
    @PostMapping("/{teamId}/invite")
    public ResponseEntity<Team> inviteMember(@PathVariable Long teamId, @RequestBody Long userId) {
        Team team = teamService.getTeamById(teamId);
        if (team == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        team.getMembers().add(user);
        Team updatedTeam = teamService.updateTeam(team);
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }
}