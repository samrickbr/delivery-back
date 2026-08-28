package br.com.inova.sigin.delivery.config;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CoreClient coreClient;

    public JwtAuthenticationFilter(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || authorization.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            CoreAuthMeResponse usuario =
                    coreClient.buscarAutenticado(authorization);

            if (usuario == null || usuario.getId() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (Boolean.FALSE.equals(usuario.getAtivo())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<SimpleGrantedAuthority> authorities =
                    new ArrayList<>();

            if (usuario.getPerfis() != null) {
                usuario.getPerfis().forEach(perfil -> {

                    if (perfil != null
                            && perfil.getNome() != null
                            && !perfil.getNome().isBlank()) {

                        authorities.add(
                                new SimpleGrantedAuthority(
                                        perfil.getNome()
                                )
                        );
                    }
                });
            }

            if (usuario.getPermissoes() != null) {
                usuario.getPermissoes().forEach(permissao -> {

                    if (permissao != null
                            && permissao.getCodigo() != null
                            && !permissao.getCodigo().isBlank()) {

                        authorities.add(
                                new SimpleGrantedAuthority(
                                        permissao.getCodigo()
                                )
                        );
                    }
                });
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            authorities
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception exception) {

            SecurityContextHolder.clearContext();

            exception.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
        }

    }
}
