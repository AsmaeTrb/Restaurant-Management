package org.example.userservice.Controller;


import org.example.userservice.Services.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.example.userservice.DTO.UserResponseDto;
import org.example.userservice.Entity.Role;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<UserResponseDto> all() {
        return adminService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto byId(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminService.deleteUser(id);
    }

    @PutMapping("/{id}/role")
    public UserResponseDto changeRole(@PathVariable Long id,
                                      @RequestParam Role role) {
        return adminService.changeRole(id, role);
    }
}
