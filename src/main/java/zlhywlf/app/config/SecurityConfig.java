package zlhywlf.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author zlhywlf
 */
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(con -> con.anyRequest().authenticated())
                .httpBasic(withDefaults())
                .formLogin(con -> con
                        .successHandler((request, response, authentication) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            PrintWriter out = response.getWriter();
                            Object principal = authentication.getPrincipal();
                            out.write(new ObjectMapper().writeValueAsString(ResponseEntity.ok(principal)));
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
                        })
                        .loginProcessingUrl("/doLogin")
                        .permitAll())
                .logout(con -> con.logoutSuccessHandler((request, response, authentication) -> {

                        })
                        .permitAll());

        //        http.sessionManagement(AbstractHttpConfigurer::disable).addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
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


    @Bean
    public Filter jwtFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//                String username=null;
//                String jwt=null;
//                if (authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
//                    jwt =  authorizationHeader.substring(7);
//                    username=jwtUtil.extractUsername(jwt);
//                }
//                if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//                    UserDetails userDetails = userDetailsService().loadUserByUsername(username);
//                    if (jwtUtil.validateToken(jwt,userDetails.getUsername())){
//                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                    }
//                }
                System.out.println("===============");
                filterChain.doFilter(request, response);
            }
        };
    }

}
