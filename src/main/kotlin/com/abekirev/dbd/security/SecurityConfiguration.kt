package com.abekirev.dbd.security

import com.abekirev.dbd.entity.Authority.ROLE_ADMIN
import com.abekirev.dbd.entity.Authority.ROLE_STAFF
import com.abekirev.dbd.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
open class SecurityConfiguration() {
    @Bean open fun userDetailsService(userService: UserService): UserDetailsService {
        return ChessUserDetailsService(userService)
    }

    @Bean open fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean open fun webSecurityConfigurer(
            userDetailsService: UserDetailsService,
            passwordEncoder: PasswordEncoder
    ): WebSecurityConfigurer<WebSecurity> {
        return WarehouseWebSecurityConfigurer(userDetailsService, passwordEncoder)
    }

    private class WarehouseWebSecurityConfigurer(private val userDetailsService: UserDetailsService,
                                                 private val passwordEncoder: PasswordEncoder) : WebSecurityConfigurerAdapter() {
        override fun configure(auth: AuthenticationManagerBuilder?) {
            if (auth != null) {
                auth
                        .userDetailsService(userDetailsService)
                        .passwordEncoder(passwordEncoder)
            } else {
                throw IllegalArgumentException("auth must be not null")
            }
        }

        override fun configure(http: HttpSecurity?) {
            if (http != null) {
                http
                        .authorizeRequests()
                            .antMatchers("/static/**", "/css/**", "/js/**", "/fonts/**").permitAll()
                            .antMatchers("/", "/home/", "/tournament/**", "/players/", "/error/**").permitAll()
                            .antMatchers("/admin/**").hasAuthority(ROLE_ADMIN.name)
                            .antMatchers("/staff/**").hasAuthority(ROLE_STAFF.name)
                            .anyRequest().authenticated()
                            .and()
                        .anonymous()
                            .principal(AnonymousPrincipal)
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
}
