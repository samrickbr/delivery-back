package br.com.inova.sigin.delivery.pedido.controller;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.service.PedidoService;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PedidoRequest request
    ) {
        return service.criar(request, authorization);
    }

    @PutMapping("/{id}/aprovar")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse aprovar(
            @PathVariable Long id
    ) {
        return service.aprovar(id);
    }

    @PutMapping("/{id}/pendente/{setor}")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse colocarPendente(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody PedidoPendenciaRequest request
    ) {
        return service.colocarPendente(id, setor, request);
    }

    @PutMapping("/{id}/cancelar/{setor}")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse cancelar(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody CancelamentoRequest request
    ) {
        return service.cancelar(id, setor, request);
    }

    @GetMapping
    public List<br.com.inova.sigin.delivery.pedido.dto.PedidoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/status/{status}")
    public List<br.com.inova.sigin.delivery.pedido.dto.PedidoResponse> listarPorStatus(
            @PathVariable StatusPedido status
    ) {
        return service.listarPorStatus(status);
    }

    @GetMapping("/cozinha")
    public List<PedidoOperacaoResponse> pedidosCozinha(
            @RequestParam String setor
    ) {
        return service.pedidosCozinha(setor);
    }

    @GetMapping("/finalizados")
    public List<br.com.inova.sigin.delivery.pedido.dto.PedidoResponse> listarFinalizados() {
        return service.listarFinalizados();
    }

    @GetMapping("/entrega")
    public List<br.com.inova.sigin.delivery.pedido.dto.PedidoResponse> listarEntrega() {
        return service.listarEntrega();
    }

    @PutMapping("/{id}/sair-entrega")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse sairParaEntrega(
            @PathVariable Long id
    ) {
        return service.sairParaEntrega(id);
    }

    @PutMapping("/{id}/entregar")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse entregar(
            @PathVariable Long id
    ) {
        return service.entregar(id);
    }

    @GetMapping("/entregues")
    public List<br.com.inova.sigin.delivery.pedido.dto.PedidoResponse> listarEntregues() {
        return service.listarEntregues();
    }

    @GetMapping("/operacao/cozinha")
    public List<PedidoOperacaoResponse> pedidosOperacaoCozinha() {
        return service.pedidosOperacaoCozinha();
    }

    @GetMapping("/operacao/entrega")
    public List<PedidoOperacaoResponse> listarEntregaOperacao() {
        return service.listarEntregaOperacao();
    }

    @GetMapping("/balcao")
    public List<PedidoBalcaoResponse> listarBalcao() {
        return service.listarBalcao();
    }

    @PutMapping("/{id}/separar")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse separar(
            @PathVariable Long id
    ) {
        return service.separar(id);
    }

    @PutMapping("/{id}/liberar-entrega")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse liberarEntrega(
            @PathVariable Long id,
            @RequestBody SeparacaoRequest request
    ) {
        return service.liberarEntrega(id, request);
    }

    @PutMapping("/{id}/iniciar-producao/{setor}")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse iniciarProducao(
            @PathVariable Long id,
            @PathVariable String setor
    ) {
        return service.iniciarProducao(id, setor);
    }

    @PutMapping("/{id}/finalizar/{setor}")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse finalizar(
            @PathVariable Long id,
            @PathVariable String setor
    ) {
        return service.finalizar(id, setor);
    }

    @PutMapping("/{id}/conferir")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse conferir(
            @PathVariable Long id
    ) {
        return service.conferir(id);
    }

    @GetMapping("/{id}/historico")
    public List<PedidoHistoricoResponse> listarHistorico(
            @PathVariable Long id
    ) {
        return service.listarHistorico(id);
    }

    @PutMapping("/{id}/cancelar-pedido")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse cancelarPedido(
            @PathVariable Long id,
            @RequestBody CancelamentoRequest request
    ) {
        return service.cancelarPedido(id, request);
    }

    @PutMapping("/{id}/cancelar-itens/{setor}")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse cancelarItens(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody CancelamentoItensRequest request
    ) {
        return service.cancelarItens(id, setor, request);
    }

    @PutMapping("/{id}/cancelar-completo")
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse cancelarPedidoCompleto(
            @PathVariable Long id,
            @RequestBody CancelamentoRequest request
    ) {
        return service.cancelarPedidoCompleto(
                id,
                request.getJustificativa()
        );
    }

    @GetMapping("/separacao")
    public List<PedidoBalcaoResponse> listarSeparacao() {
        return service.listarSeparacao();
    }
}