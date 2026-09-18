package com.gym.crm.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // No members needed: @SpringBootTest(webEnvironment = RANDOM_PORT) is enough for Spring
    // Boot to auto-configure and register a TestRestTemplate bean (see TestApiClient), which
    // every step-definition class uses to talk to the running application over real HTTP.
}
