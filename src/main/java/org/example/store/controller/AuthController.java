package org.example.store.controller;

import jakarta.validation.Valid;
import org.example.store.dto.AuthRequest;
import org.example.store.dto.tokens.RefreshToken;
import org.example.store.dto.tokens.Tokens;
import org.example.store.dto.transaction.TransactionDTO;
import org.example.store.dto.user.UserCreateDTO;
import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserRolesDTO;
import org.example.store.services.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<Tokens> createNewUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return new ResponseEntity<>(authServiceImpl.saveUser(userCreateDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(authServiceImpl.authenticate(authRequest), HttpStatus.OK);
    }
    @GetMapping("/validate-token")
    public ResponseEntity<UserRolesDTO> validateToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(authServiceImpl.validateToken(token), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(Principal principal) {
        return new ResponseEntity<>(authServiceImpl.getUserById(principal), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getUserRoles(Principal principal) {
        return new ResponseEntity<>(authServiceImpl.getUserRoles(principal), HttpStatus.OK);
    }

    @PostMapping("/company")
    public ResponseEntity<Void> createCompany(Principal principal, @RequestParam String companyName) {
        authServiceImpl.createCompany(principal, companyName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/access-token")
    public ResponseEntity<Tokens> getNewAccessToken(@Valid @RequestBody RefreshToken refreshToken){
        return new ResponseEntity<>(authServiceImpl.getAccessToken(refreshToken), HttpStatus.CREATED);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(Principal principal){
        return new ResponseEntity<>(authServiceImpl.getTransactions(principal), HttpStatus.OK);
    }

}
