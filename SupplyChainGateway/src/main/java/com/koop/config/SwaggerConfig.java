package com.koop.config;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<String>(
			Arrays.asList("application/json"));

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.koop.controller")).paths(regex("/koop.*")).build()
				.produces(DEFAULT_PRODUCES_AND_CONSUMES).consumes(DEFAULT_PRODUCES_AND_CONSUMES).apiInfo(metaData())

				.useDefaultResponseMessages(false).globalResponseMessage(RequestMethod.GET,
						newArrayList(new ResponseMessageBuilder().code(500).message("500 message").build(),
								new ResponseMessageBuilder().code(401)
										.message("You are not authorized to view the resource").build(),
								new ResponseMessageBuilder().code(403).message("Forbidden!").build()));

	}

	// will use this in future
	// .responseModel(new ModelRef("Address"))
	private ApiInfo metaData() {
		return new ApiInfoBuilder().title("KOOP REST API").description("\"KOOP REST API for Kroger\"").version("1.0.0")
				.license("Kroger License Version 1.0").licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
				.contact(new Contact("Deloitte", "https://www2.deloitte.com/us/en.html", "kroger help email")).build();
	}
}