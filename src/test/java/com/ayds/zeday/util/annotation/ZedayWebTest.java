package com.ayds.zeday.util.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ayds.zeday.util.paramresolver.PrimitivesParamsResolver;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(PrimitivesParamsResolver.class)
@AutoConfigureMockMvc
@AutoConfigureCache
public @interface ZedayWebTest {

}
