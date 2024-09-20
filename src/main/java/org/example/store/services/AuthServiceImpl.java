package org.example.store.services;

import org.example.store.dto.AuthRequest;
import org.example.store.dto.tokens.RefreshToken;
import org.example.store.dto.tokens.Tokens;
import org.example.store.dto.transaction.TransactionDTO;
import org.example.store.dto.user.UserCreateDTO;
import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserRolesDTO;
import org.example.store.exception.user.UserNotFoundException;
import org.example.store.jwt.JwtService;
import org.example.store.mapper.ProductMapper;
import org.example.store.model.*;
import org.example.store.repository.CompanyRepository;
import org.example.store.repository.RoleRepository;
import org.example.store.repository.TransactionRepository;
import org.example.store.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    private final RoleRepository roleRepository;
    private final ProductMapper productMapper;

    public AuthServiceImpl(JwtService jwtService, UserRepository userRepository, AuthenticationManager authenticationManager, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, TransactionRepository transactionRepository, RoleRepository roleRepository, ProductMapper productMapper) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository = transactionRepository;
        this.roleRepository = roleRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Tokens saveUser(UserCreateDTO userCreateDTO) {
        User user = new User(userCreateDTO.userName(), userCreateDTO.firstName(), userCreateDTO.lastName(), passwordEncoder.encode(userCreateDTO.password()));
        Transaction transaction = new Transaction(BigDecimal.valueOf(1000), Type.RECEIVED);
        transactionRepository.save(transaction);
        user.setTransactions(List.of(transaction));
        Role role = roleRepository.findRoleByName("client");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        User user1 = userRepository.save(user);
        return new Tokens(jwtService.generateRefreshToken(user1.getUser_name()),  jwtService.generateAccessToken(user1.getUser_name()));
    }

    @Override
    public Tokens authenticate(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        if (authenticate.isAuthenticated()) {
            return new Tokens(jwtService.generateRefreshToken(authRequest.username()), jwtService.generateAccessToken(authRequest.username()));
        } else {
            throw new IllegalArgumentException("Wrong username or password");
        }
    }

    @Override
    public UserRolesDTO validateToken(String token) {
        User user = userRepository.findUserByUser_name(jwtService.getSubjectAccessToken(token)).orElseThrow(() -> new UserNotFoundException(jwtService.getSubjectAccessToken(token)));
        return new UserRolesDTO(user.getId(), user.getRoles().stream().map(Role::getName).toList());
    }

    @Override
    public UserDTO getUserById(Principal principal) {
        return userRepository.findUserByName(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
    }

    @Override
    public List<String> getUserRoles(Principal principal) {
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return user.getRoles().stream().map(Role::getName).toList();
    }

    @Override
    public void createCompany(Principal principal, String brandName) {
        Company company = new Company(brandName);
        companyRepository.save(company);
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        user.setCompany(company);
        Role role = roleRepository.findRoleByName("company");
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public Tokens getAccessToken(RefreshToken refreshToken) {
        if(jwtService.validateRefreshToken(refreshToken.value())){
            return new Tokens(jwtService.generateRefreshToken(jwtService.getSubjectRefreshToken(refreshToken.value())), jwtService.generateAccessToken(jwtService.getSubjectRefreshToken(refreshToken.value())));
        }
        else {
            throw new IllegalArgumentException("Wrong refresh token");
        }
    }

    @Override
    public List<TransactionDTO> getTransactions(Principal principal) {
        List<Transaction> transactions = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName())).getTransactions();
        transactions.sort((a, b) -> a.getDate().isAfter(b.getDate()) ? -1 : a.getDate().isBefore(b.getDate()) ? 1 : 0);
        return transactions.stream().map(productMapper::mapTransactionToTransactionDto).toList();
    }

}
