package com.codingvibe.users.config


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.jetbrains.exposed.sql.Database
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
open class UserPrefsConfig : WebMvcConfigurer {

    @Autowired
    private val env: Environment? = null

    @Bean
    open fun getDatabase(): Database {
        val connectionString = env!!.getRequiredProperty("spring.datasource.url")
        val driverName = env!!.getRequiredProperty("spring.datasource.driver-class-name")
        val username = env!!.getRequiredProperty("spring.datasource.username")
        val password = env!!.getRequiredProperty("spring.datasource.password")
        return Database.connect(url = connectionString, driver = driverName, user = username, password = password)
    }

    @Bean
    @Primary
    open fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
            .registerModule(ParameterNamesModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean
    open fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setMaxPayloadLength(64000)
        return loggingFilter
    }
}