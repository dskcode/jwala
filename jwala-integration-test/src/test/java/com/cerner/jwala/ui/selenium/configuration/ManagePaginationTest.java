package com.cerner.jwala.ui.selenium.configuration;

import com.cerner.jwala.ui.selenium.Test;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by SB053052 on 7/10/2017.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:com/cerner/jwala/ui/selenium/configuration/pagination.feature"},
        glue = {"com.cerner.jwala.ui.selenium.steps"})
public class ManagePaginationTest extends Test {}

