plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.hibernate.orm") version "6.5.3.Final"
	// id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "com.ayds"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(platform("software.amazon.awssdk:bom:2.28.6"))
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("com.warrenstrange:googleauth:1.4.0")
	implementation("software.amazon.awssdk:s3")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("com.h2database:h2")
	testCompileOnly("org.projectlombok:lombok")
}

hibernate {
	enhancement {
		enableAssociationManagement = true
	}
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
tasks.withType<Test> {
	useJUnitPlatform()
    maxHeapSize = "8g"
}
tasks.withType<Test>().configureEach {
    maxHeapSize = "8g"
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required = true
		csv.required = false
		xml.outputLocation = layout.buildDirectory.file("reports/jacoco/test/jacoco.xml")
	}

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    setExcludes(listOf(
                        "**/entity/**/*",
                        "**/dto/**/*",
                        "**/service/util/**/*",
                        "**/controller/AuthController.class",
                        "**/service/user/UserService.class",
                        "**/service/user/MfaService.class",
                        "**/service/scheduling/AvailabilityService.class",
                        "**/service/scheduling/AppointmentService.class"))
                }
            }
        )
    )
}
tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.jacocoTestReport)

	violationRules {
		rule {
            classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
			limit {
				minimum = "0.8".toBigDecimal()
			}
		}
	}
}
