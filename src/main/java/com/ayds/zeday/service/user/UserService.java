package com.ayds.zeday.service.user;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.user.AddUserDto;
import com.ayds.zeday.domain.dto.user.MfaUserDto;
import com.ayds.zeday.domain.dto.user.UpdateUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.RoleEntity;
import com.ayds.zeday.domain.entity.UserEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.RoleRepository;
import com.ayds.zeday.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder encoder;

    public Optional<UserDto> findUserById(long businessId, long userId) {
        return userRepository.findByIdAndBusinessId(userId, businessId, UserDto.class);
    }

    public Optional<UserDto> findUserByEmail(long businessId, String email) {
        return userRepository.findByEmailAndBusinessId(email, businessId, UserDto.class);
    }

    public Optional<MfaUserDto> findMfaUserByEmail(long businessId, String email) {
        return userRepository.findByEmailAndBusinessId(email, businessId, MfaUserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("No se pudo encontrar al usuario");
    }

    @Transactional
    public void changeUserPassword(long userId, String password) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        String encryptedPassword = encoder.encode(password);

        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }

    @Transactional
    public void changeUserInfo(long userId, UpdateUserDto user) {
        UserEntity dbUser = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.phone()
                .filter(ObjectUtils::isNotEmpty)
                .ifPresent(dbUser::setPhone);

        user.currentPassword()
                .filter(ObjectUtils::isNotEmpty)
                .filter(passwd -> encoder.matches(passwd, dbUser.getPassword()))
                .flatMap(passwd -> user.newPassword())
                .filter(ObjectUtils::isNotEmpty)
                .map(encoder::encode)
                .ifPresent(dbUser::setPassword);

        userRepository.save(dbUser);
    }

    @Transactional
    public void toggleUserRoles(long businessId, long userId, List<Long> roleIds) {
        UserEntity user = userRepository.findByIdAndBusinessId(userId, businessId, UserEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.getRoles().removeIf(role -> !roleIds.contains(role.getId()));

        List<Long> actualRoleIds = user.getRoles()
                .stream()
                .map(RoleEntity::getId)
                .toList();

        roleIds.removeAll(actualRoleIds);

        List<RoleEntity> rolesToAdd = roleRepository.findAllByIdInAndBusinessId(roleIds, businessId);

        if (!roleIds.containsAll(rolesToAdd.stream().map(RoleEntity::getId).toList())) {
            throw new ValueNotFoundException("No se pudieron encontrar todos los roles");
        }

        user.getRoles().addAll(rolesToAdd);

        userRepository.save(user);
    }

    @Transactional
    public void registerUser(long businessId, AddUserDto user, String mfaSecret) {
        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia actual"));

        if (userRepository.existsByEmailAndBusinessId(user.email(), businessId)) {
            throw new RequestConflictException("El email que se intenta registrar ya esta en uso");
        }
        String encryptedPassword = encoder.encode(user.password());

        RoleEntity role = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el role CLIENTE"));

        UserEntity newUser = UserEntity.builder()
                .email(user.email())
                .password(encryptedPassword)
                .mfaSecret(mfaSecret)
                .name(user.name())
                .lastname(user.lastname())
                .nit(user.nit())
                .cui(user.cui())
                .phone(user.phone())
                .role(role)
                .business(business)
                .build();

        userRepository.save(newUser);
    }
}
