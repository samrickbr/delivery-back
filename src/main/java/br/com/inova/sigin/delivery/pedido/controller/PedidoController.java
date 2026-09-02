package br.com.inova.sigin.delivery.pedido.controller;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.service.PedidoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
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
        return service.colocarPendente(
                id,
                setor,
                request,
                authorization
        );
    }

    @PutMapping("/{id}/cancelar/{setor}")
    public PedidoResponse cancelar(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody CancelamentoRequest request
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
            @PathVariable Long id
    ) {
        return service.sairParaEntrega(id);
    }

    @PutMapping("/{id}/entregar")
    public PedidoResponse entregar(
            @PathVariable Long id
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

    @GetMapping("/{id}")
    public PedidoConsultaResponse buscarPorId(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return service.buscarPorId(id, authorization);
    }

    @PutMapping("/{id}/separar")
    public PedidoResponse separar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.separar(id, authorization);
    }

    @PutMapping("/{id}/liberar-entrega")
    public PedidoResponse liberarEntrega(
            @PathVariable Long id,
            @RequestBody SeparacaoRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.liberarEntrega(id, request, authorization);
    }

    @PutMapping("/{id}/iniciar-producao/{setor}")
    public PedidoResponse iniciarProducao(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.iniciarProducao(id, setor, authorization);
    }

    @PutMapping("/{id}/finalizar/{setor}")
    public PedidoResponse finalizar(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.finalizar(id, setor, authorization);
    }

    @PutMapping("/{id}/conferir")
    public PedidoResponse conferir(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.conferir(id, authorization);
    }

    @GetMapping("/{id}/historico")
    public List<PedidoHistoricoResponse> listarHistorico(
            @PathVariable Long id
    ) {
        return service.listarHistorico(id);
    }
    @GetMapping("/{id}/situacao-financeira")
    public PedidoSituacaoFinanceiraResponse consultarSituacaoFinanceira(
            @PathVariable Long id
    ) {
        return service.consultarSituacaoFinanceira(id);
    }

    @PutMapping("/{id}/cancelar-pedido")
    public PedidoResponse cancelarPedido(
            @PathVariable Long id,
            @RequestBody CancelamentoRequest request
    ) {
        return service.cancelarPedido(id, request);
    }

    @PutMapping("/{id}/cancelar-itens/{setor}")
    public PedidoResponse cancelarItens(
            @PathVariable Long id,
            @PathVariable String setor,
            @RequestBody CancelamentoItensRequest request
    ) {
        return service.cancelarItens(id, setor, request);
    }

    @PutMapping("/{id}/cancelar-completo")
    public PedidoResponse cancelarPedidoCompleto(
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
            @PathVariable Long itemId
    ) {
        return service.iniciarProducaoItem(pedidoId, itemId);
    }

    @PutMapping("/{pedidoId}/itens/{itemId}/pendente")
    public PedidoResponse colocarPendenteItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestBody PedidoPendenciaRequest request
    ) {
        return service.colocarPendenteItem(
                pedidoId,
                itemId,
                request
        );
    }

    @PutMapping("/{pedidoId}/itens/{itemId}/finalizar")
    public PedidoResponse finalizarItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId
    ) {
        return service.finalizarItem(pedidoId, itemId);
    }
    @PostMapping("/{pedidoId}/itens")
    @ResponseStatus(HttpStatus.OK)
    public PedidoResponse adicionarItem(
            @PathVariable Long pedidoId,
            @RequestBody PedidoItemRequest request
    ) {
        return service.adicionarItem(pedidoId, request);
    }

    @PutMapping("/{pedidoId}/itens/{itemId}/quantidade")
    public PedidoResponse alterarQuantidadeItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestBody PedidoItemRequest request
    ) {
        return service.alterarQuantidadeItem(
                pedidoId,
                itemId,
                request
        );
    }

    @DeleteMapping("/{pedidoId}/itens/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId
    ) {
        service.removerItem(pedidoId, itemId);
    }
    @PostMapping("/{id}/pagamentos")
    public PedidoResponse adicionarPagamento(
            @PathVariable Long id,
            @RequestBody PedidoPagamentoRequest request
    ) {
        return service.adicionarPagamento(id, request);
    }

    @PostMapping("/{id}/faturar")
    public PedidoResponse faturar(
            @PathVariable Long id
    ) {
        return service.faturar(id);
    }
}
