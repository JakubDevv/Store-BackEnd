package org.example.store.services;

import org.example.store.dto.AuthRequest;
import org.example.store.dto.tokens.RefreshToken;
import org.example.store.dto.tokens.Tokens;
import org.example.store.dto.transaction.TransactionDTO;
import org.example.store.dto.user.UserCreateDTO;
import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserRolesDTO;

import java.security.Principal;
import java.util.List;

public interface AuthService {

    Tokens saveUser(UserCreateDTO userCreateDTO);

    Tokens authenticate(AuthRequest authRequest);

    UserRolesDTO validateToken(String token);

    UserDTO getUserById(Principal principal);

    List<String> getUserRoles(Principal principal);

    void createCompany(Principal principal, String brandName);

    Tokens getAccessToken(RefreshToken refreshToken);

    List<TransactionDTO> getTransactions(Principal principal);

}
