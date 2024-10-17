package org.grupouno.parking.it4.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;
    private static final String MESSAGE = "message";
    private static final String ERROR = "Error";

    @RolesAllowed("DETAILROLEPROFILE")
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        Map<String, Object> response = new HashMap<>();
        try{
            List<Rol> roles = roleService.getAllRol();
            response.put(MESSAGE, roles);
            logger.info("Get all rolles");
            return ResponseEntity.ok(response);
        }catch(Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error get Roles " + e.getMessage());
            logger.error("Fail get rols, {}", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRolesId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try{
            Optional<Rol> roles = roleService.findRolById(id);
            response.put(MESSAGE, roles);
            logger.info("Find roles, {}", id);
            return ResponseEntity.ok(response);
        }catch(IllegalArgumentException e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error get Roles " + e.getMessage());
            logger.error("Fail find role {}", id);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @PostMapping("/saveRol")
    public ResponseEntity<Map<String, String>> addRole(@RequestBody RoleDto role) {
        Map<String, String> response = new HashMap<>();
        try {
            roleService.saveRole(role);
            response.put(MESSAGE,  role.getRole() +"Saved");
            logger.info("Role saved {}", role.getRole());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error save Role " + e.getMessage());
            logger.error("Fail add role {}", role.getRole());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try{
            roleService.delete(id);
            response.put(MESSAGE, "Role: "+ id +" deleted");
            logger.info("Delete rol {}", id);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException|DataAccessException e){
            response.put(MESSAGE, ERROR);
            logger.error("Role not found, {}", e.getMessage());
            response.put(ERROR, e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed("DETAILROLEPROFILE")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRole(@PathVariable Long id, @RequestBody RoleDto role) {
        Map<String, String> response = new HashMap<>();
        try {
            roleService.updateRol(role, id);
            response.put(MESSAGE, "Role Updated");
            logger.info("Role {} updated", role.getRole());
            return ResponseEntity.ok(response);
        }catch (EntityNotFoundException e){
            response.put(MESSAGE, ERROR);
            response.put("err", "An error update Role " + e.getMessage());
            logger.error("Role not found {}, {}", role.getRole(), id);
            return ResponseEntity.internalServerError().body(response);
        }
    }




}