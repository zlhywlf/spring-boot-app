package zlhywlf.jwtapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import zlhywlf.jwtapp.config.jwt.IJwtProvider;
import zlhywlf.jwtapp.config.jwt.JwtFilter;


import java.io.PrintWriter;
import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author zlhywlf
 */
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private IJwtProvider jwt;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(con -> con.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/",
                                "/*.html",
                                "/favicon.ico",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .formLogin(con -> con
                        .successHandler((request, response, authentication) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            PrintWriter out = response.getWriter();
                            Object principal = authentication.getPrincipal();
                            String token = jwt.createToken(((UserDetails) principal).getUsername());
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                            out.write(new ObjectMapper().writeValueAsString(new ResponseEntity<>("token", httpHeaders, HttpStatus.OK)));
                            out.flush();
                            out.close();
                        })
                        .failureHandler((request, response, exception) -> {
                            PrintWriter out = response.getWriter();
                            StackTraceElement[] stackTrace = exception.getStackTrace();
                            StringBuilder builder = new StringBuilder();
                            for (StackTraceElement e : stackTrace) {
                                builder.append(e.toString()).append("\n");
                            }
                            out.write(builder.toString());
                            out.flush();
                            out.close();
                        }))
                .logout(con -> con.logoutSuccessHandler((request, response, authentication) -> {

                        })
                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(con -> con.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(con -> con.frameOptions().sameOrigin())
                .cors(con -> con.configurationSource(corsFilter()))
                .addFilterBefore(new JwtFilter(userDetailsService(), jwt), UsernamePasswordAuthenticationFilter.class)
        ;
//        http.exceptionHandling(con -> con.authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
//                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage())));
        return http.build();
    }

    public UrlBasedCorsConfigurationSource corsFilter() {
        // 跨域测试 打开任意网页在该网页控制台中执行
        // var xhr = new XMLHttpRequest();xhr.open('GET','http://localhost:8080/login');xhr.send(null);xhr.onload=function(e){console.log(e.target.responseText);}
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/login/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                // password
                return "{bcrypt}$2a$10$h/AJueu7Xt9yh3qYuAXtk.WZJ544Uc2kdOKlHu2qQzCh/A3rq46qm";
            }

            @Override
            public String getUsername() {
                return "user";
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

}
