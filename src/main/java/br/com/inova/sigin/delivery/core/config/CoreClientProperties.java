package br.com.inova.sigin.delivery.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sigin.core")
public class CoreClientProperties {

    private String url;
    private Long canalVendaId;
    private String login;
    private String senha;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCanalVendaId() {
        return canalVendaId;
    }

    public void setCanalVendaId(Long canalVendaId) {
        this.canalVendaId = canalVendaId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}