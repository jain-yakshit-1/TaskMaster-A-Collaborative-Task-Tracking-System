package com.airtribe.taskmaster.controller;

import com.airtribe.taskmaster.model.User;
import com.airtribe.taskmaster.payload.JwtResponse;
import com.airtribe.taskmaster.payload.LoginRequest;
import com.airtribe.taskmaster.payload.RegisterRequest;
import com.airtribe.taskmaster.security.JwtTokenUtil;
import com.airtribe.taskmaster.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Register a new user", description = "This endpoint registers a new user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Username is already taken")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());

        userService.createUser(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(summary = "Authenticate a user", description = "This endpoint allows user login.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authenticated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @Operation(summary = "Get user profile", description = "Retrieve the authenticated user's profile details.")
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user profile", description = "Update the authenticated user's profile details.")
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String token, @Valid @RequestBody User userDetails) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        User user = userService.findByUsername(username);

        user.setEmail(userDetails.getEmail());
        userService.createUser(user);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Logout user", description = "Clear the security context and log the user out.")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext(); // Clear the security context
        return ResponseEntity.ok("User logged out successfully!");
    }
}