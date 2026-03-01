package com.aplication.rest.instruments.user;

import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    @Override
    public Result<Page<UserProfileDTO>> findAllUsers(Pageable pageable) {
        //get the current user who is requesting the users page (list)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User not found"));

        Page<User> usersPage;
        //filter user by role
        if (currentUser.getRole() == Role.ROLE_ADMIN){
            usersPage = userRepository.findAll(pageable);
        } else if (currentUser.getRole() == Role.ROLE_STAFF) {
            usersPage = userRepository.findAllByRole(Role.ROLE_CUSTOMER, pageable);
        } else {
            throw new RuntimeException("Access denied or unrecognized role");
        }
        //map users entity to user profile dto
        Page<UserProfileDTO> dtoUsersPage = usersPage.map(user-> new UserProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDni(),
                user.getPhone(),
                user.getRole().name()
        ));

        return Result.success(dtoUsersPage);
    }

    @Override
    public Result<UserProfileDTO> findById(UUID id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User not found"));

        User targetUser = userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("User not found"));

        if (currentUser.getRole() == Role.ROLE_STAFF) {
            if (targetUser.getRole() != Role.ROLE_CUSTOMER){
                throw new AccessDeniedException("Staff only can see customer profiles");
            }
        } else if (currentUser.getRole() == Role.ROLE_CUSTOMER) {
            if (!currentUser.getId().equals(targetUser.getId())){
                throw new AccessDeniedException("you don not have enough permissions");
            }
        }
        UserProfileDTO profileDTO = new UserProfileDTO(
                targetUser.getId(),
                targetUser.getFirstName(),
                targetUser.getLastName(),
                targetUser.getEmail(),
                targetUser.getDni(),
                targetUser.getPhone(),
                targetUser.getRole().name()
        );
        return Result.success(profileDTO);
    }
}
