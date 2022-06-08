package zlhywlf.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.io.PrintWriter;
import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author zlhywlf
 */
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(con -> con.anyRequest().authenticated())
                .httpBasic(withDefaults())
                .formLogin(con -> con
                        .successHandler((request, response, authentication) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            PrintWriter out = response.getWriter();
                            Object principal = authentication.getPrincipal();
                            out.write(new ObjectMapper().writeValueAsString(principal));
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
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
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
