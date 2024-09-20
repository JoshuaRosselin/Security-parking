package org.grupouno.parking.it4.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.RoleService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/roles")
@RestController
public class RoleController {

    private final RoleService roleService;
    private static final String MESSAGE = "message";
    private static final String ERROR = "Error";

    @RolesAllowed({"ADMIN"})
    @GetMapping("")
    public ResponseEntity<Map<String, String>> getAllRoles() {
        Map<String, String> response = new HashMap<>();
        try{
            List<Rol> roles = roleService.getAllRol();
            response.put(MESSAGE, roles.toString());
            return ResponseEntity.ok(response);
        }catch(Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error get Roles " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getRolesId(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try{
            Optional<Rol> roles = roleService.findRolById(id);
            response.put(MESSAGE, roles.toString());
            return ResponseEntity.ok(response);
        }catch(IllegalArgumentException e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error get Roles " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed({"ADMIN"})
    @PostMapping("")
    public ResponseEntity<Map<String, String>> addRole(@RequestBody RoleDto role) {
        Map<String, String> response = new HashMap<>();
        try {
            roleService.saveRole(role);
            response.put(MESSAGE,  role.getRole() +"Saved");
        }catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error save Role " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try{
            roleService.delete(id);
            response.put(MESSAGE, "Role: "+ id +" deleted");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException|DataAccessException e){
            response.put(MESSAGE, ERROR);
            response.put(ERROR, e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @RolesAllowed({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRole(@PathVariable Long id, @RequestBody RoleDto role) {
        Map<String, String> response = new HashMap<>();
        try {
            roleService.updateRol(role, id);
            response.put(MESSAGE, "Role Updated");
            return ResponseEntity.ok(response);
        }catch (EntityNotFoundException e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error update Role " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }




}
