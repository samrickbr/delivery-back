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
    public PedidoResponse aprovar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.aprovar(id, authorization);
    }

    @PutMapping("/{id}/pendente/{setor}")
    public PedidoResponse colocarPendente(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody PedidoPendenciaRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.colocarPendente(id, setor, request);
    }

    @PutMapping("/{id}/cancelar/{setor}")
    public PedidoResponse cancelar(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody CancelamentoRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.cancelar(id, setor, request);
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/status/{status}")
    public List<PedidoResponse> listarPorStatus(
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
    public List<PedidoResponse> listarFinalizados() {
        return service.listarFinalizados();
    }

    @GetMapping("/entrega")
    public List<PedidoResponse> listarEntrega() {
        return service.listarEntrega();
    }

    @PutMapping("/{id}/sair-entrega")
    public PedidoResponse sairParaEntrega(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.sairParaEntrega(id);
    }

    @PutMapping("/{id}/entregar")
    public PedidoResponse entregar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.entregar(id);
    }

    @GetMapping("/entregues")
    public List<PedidoResponse> listarEntregues() {
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
    public PedidoResponse separar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.separar(id);
    }

    @PutMapping("/{id}/liberar-entrega")
    public PedidoResponse liberarEntrega(
            @PathVariable Long id,
            @RequestBody SeparacaoRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.liberarEntrega(id, request);
    }

    @PutMapping("/{id}/iniciar-producao/{setor}")
    public PedidoResponse iniciarProducao(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.iniciarProducao(id, setor);
    }

    @PutMapping("/{id}/finalizar/{setor}")
    public PedidoResponse finalizar(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.finalizar(id, setor);
    }

    @PutMapping("/{id}/conferir")
    public PedidoResponse conferir(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
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
    public PedidoResponse cancelarPedido(
            @PathVariable Long id,
            @RequestBody CancelamentoRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.cancelarPedido(id, request);
    }

    @PutMapping("/{id}/cancelar-itens/{setor}")
    public PedidoResponse cancelarItens(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody CancelamentoItensRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.cancelarItens(id, setor, request);
    }

    @PutMapping("/{id}/cancelar-completo")
    public PedidoResponse cancelarPedidoCompleto(
            @PathVariable Long id,
            @RequestBody CancelamentoRequest request,
            @RequestHeader("Authorization") String authorization
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

    @GetMapping("/retirada")
    public List<PedidoBalcaoResponse> listarRetirada() {
        return service.listarRetirada();
    }

    @GetMapping("/meus")
    public List<PedidoResponse> listarMeus(
            @RequestHeader("Authorization") String authorization
    ) {
        return service.listarMeus(authorization);
    }
    @PutMapping("/{pedidoId}/itens/{itemId}/iniciar-producao")
    public PedidoResponse iniciarProducaoItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.iniciarProducaoItem(pedidoId, itemId);
    }

    @PutMapping("/{pedidoId}/itens/{itemId}/pendente")
    public PedidoResponse colocarPendenteItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestBody PedidoPendenciaRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.colocarPendenteItem(pedidoId, itemId, request);
    }

    @PutMapping("/{pedidoId}/itens/{itemId}/finalizar")
    public PedidoResponse finalizarItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.finalizarItem(pedidoId, itemId);
    }

}