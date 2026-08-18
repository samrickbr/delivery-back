package br.com.inova.sigin.delivery.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sigin.core")
public class CoreClientProperties {

    private String url;
    private Long canalVendaId;

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
}