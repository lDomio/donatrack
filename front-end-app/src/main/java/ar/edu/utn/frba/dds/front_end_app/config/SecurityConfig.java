package ar.edu.utn.frba.dds.front_end_app.config;

import ar.edu.utn.frba.dds.front_end_app.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

  @Bean
  public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider provider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(provider)
        .build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.ignoringRequestMatchers(
            "/personaAdministradora/subcategorias",
            "/api/**",
            "/ws-rutas/**",
            "/entidadBeneficiaria/confirmarRecepcion/**"
        ))
        .authorizeHttpRequests(auth -> auth
            // Recursos estáticos y páginas públicas
            .requestMatchers(
                "/",
                "/acceso/main",
                "/quienesSomos",
                "/programas",
                "/empresas",
                "/proyectos",
                "/login",
                "/403",
                "/error",
                "/css/**",
                "/js/**",
                "/images/**",
                "/img/**",
                "/uploads/**",
                "/ws-rutas/**",
                "/api/**"
            ).permitAll()
            // Rutas protegidas por Rol
            .requestMatchers("/personaAdministradora/**").hasRole("ADMINISTRADOR")
            .requestMatchers("/personaDonante/**").hasRole("DONANTE")
            .requestMatchers("/entidadBeneficiaria/**").hasRole("ENTIDAD_BENEFICIARIA")
            .requestMatchers("/personaChofer/**").hasRole("CAMIONERO")
            // Cualquier otra ruta requiere autenticación
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .permitAll()
            // Redirección inteligente al portal correspondiente según el rol del usuario
            .successHandler((request, response, authentication) -> {
              var roles = authentication.getAuthorities().stream()
                  .map(GrantedAuthority::getAuthority)
                  .toList();

              if (roles.contains("ROLE_ADMINISTRADOR")) {
                response.sendRedirect("/personaAdministradora/registrarDonante");
              } else if (roles.contains("ROLE_DONANTE")) {
                response.sendRedirect("/personaDonante/misDonaciones");
              } else if (roles.contains("ROLE_ENTIDAD_BENEFICIARIA")) {
                response.sendRedirect("/entidadBeneficiaria/misNecesidades");
              } else if (roles.contains("ROLE_CAMIONERO")) {
                response.sendRedirect("/personaChofer/miRuta");
              } else {
                response.sendRedirect("/");
              }
            })
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        )
        .exceptionHandling(ex -> ex
            // Usuario no autenticado → redirigir a login
            .authenticationEntryPoint((request, response, authException) ->
                response.sendRedirect("/login?unauthorized")
            )
            // Usuario autenticado pero sin rol/permiso suficiente → redirigir a página de error
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.sendRedirect("/403")
            )
        );

    return http.build();
  }
}
