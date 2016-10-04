package com.abekirev.dbd

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
open class SecurityConfiguration @Autowired constructor(private val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {
    override fun configure(auth: AuthenticationManagerBuilder?) {
        if (auth != null) {
            auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(BCryptPasswordEncoder(12))
        } else {
            throw IllegalArgumentException("auth must be not null")
        }
    }

    override fun configure(http: HttpSecurity?) {
        if (http != null) {
            http
                    .authorizeRequests()
                        .antMatchers("/", "/home").permitAll()
                        .anyRequest().authenticated()
                        .and()
                    .formLogin()
                        .loginPage("/login")
                        .permitAll()
                        .and()
                    .logout()
                        .permitAll()
        } else {
            throw IllegalArgumentException("http must be not null")
        }
    }
}
