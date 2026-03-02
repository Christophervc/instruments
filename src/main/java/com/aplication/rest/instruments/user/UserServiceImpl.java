package com.aplication.rest.instruments.user;

import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.core.exceptions.ValidationException;
import com.aplication.rest.instruments.user.dto.ChangeRoleRequest;
import com.aplication.rest.instruments.user.dto.ChangeStatusRequest;
import com.aplication.rest.instruments.user.dto.UpdateProfileRequest;
import com.aplication.rest.instruments.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    @Transactional
    public Result<UserProfileDTO> changeRole(UUID id, ChangeRoleRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(()-> new NotFoundException("User not found"));

        if (currentUser.getRole() != Role.ROLE_ADMIN){
            throw new AccessDeniedException("Only admin can change roles");
        }

        User targetUser = userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("User not found"));

        if (currentUser.getId().equals(targetUser.getId())){
            throw new AccessDeniedException("You can't change your own role");
        }

        targetUser.setRole(request.role());
        User savedUser =userRepository.save(targetUser);

        UserProfileDTO profileDTO = new UserProfileDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getDni(),
                savedUser.getPhone(),
                savedUser.getRole().name()
        );

        return Result.success(profileDTO);
    }

    @Override
    @Transactional
    public Result<UserProfileDTO> changeStatus(UUID id, ChangeStatusRequest request) {
        // get who is trying to change the status
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        // check if the user is admin
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Rejected, only admin can change status");
        }
        // target user whose status (active) is going to be changed
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found, ID: " + id));
        // prevent self status (active) change
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new ValidationException("You can't change your own status");
        }
        //prevent if admin tries to change another admin status
        if (targetUser.getRole() == Role.ROLE_ADMIN) {
            throw new ValidationException("Operation not allowed, admin can't change another admin status");
        }
        // apply and save
        targetUser.setActive(request.active());
        User savedUser = userRepository.save(targetUser);

        UserProfileDTO profileDTO = new UserProfileDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getDni(),
                savedUser.getPhone(),
                savedUser.getRole().name()
        );

        return Result.success(profileDTO);
    }

    @Override
    @Transactional
    public Result<UserProfileDTO> updateMe(UpdateProfileRequest request) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        //Update only allowed fields
        currentUser.setFirstName(request.firstName());
        currentUser.setLastName(request.lastName());
        currentUser.setPhone(request.phone());

        User savedUser = userRepository.save(currentUser);

        UserProfileDTO profileDTO = new UserProfileDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getDni(),
                savedUser.getPhone(),
                savedUser.getRole().name()
        );
        return Result.success(profileDTO);
    }
}
