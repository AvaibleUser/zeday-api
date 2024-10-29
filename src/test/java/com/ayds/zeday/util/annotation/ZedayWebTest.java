package com.ayds.zeday.util.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
@ZedayTest
@AutoConfigureMockMvc
public @interface ZedayWebTest {

}
