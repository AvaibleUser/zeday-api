package com.ayds.zeday.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.ayds.zeday.domain.dto.user.UserDto;

@Target({ ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@userRepository.findUserById(#this.getSubject(), #value).get()")
public @interface CurrentUserDto {

    Class<?> value() default UserDto.class;

}
