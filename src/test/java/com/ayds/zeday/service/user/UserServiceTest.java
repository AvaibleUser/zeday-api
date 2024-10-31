package com.ayds.zeday.service.user;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.dto.user.UserDto.UserDtoImpl;
import com.ayds.zeday.domain.entity.UserEntity;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.UserRepository;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.UserParamsResolver;

@ZedayTest
@ExtendWith({ UserParamsResolver.class })
public class UserServiceTest {

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void canFindUserById(long businessId, long userId, UserDtoImpl expectedUser) {
        given(userRepository.findByIdAndBusinessId(userId, businessId, UserDto.class))
                .willReturn(Optional.of(expectedUser.toBuilder().build()));

        Optional<UserDto> actualUser = userService.findUserById(businessId, userId);

        then(actualUser).contains(expectedUser);
    }

    @Test
    public void canFindUserByEmail(long businessId, String email, UserDtoImpl expectedUser) {
        given(userRepository.findByEmailAndBusinessId(email, businessId, UserDto.class))
                .willReturn(Optional.of(expectedUser.toBuilder().build()));

        Optional<UserDto> actualUser = userService.findUserByEmail(businessId, email);

        then(actualUser).contains(expectedUser);
    }

}
