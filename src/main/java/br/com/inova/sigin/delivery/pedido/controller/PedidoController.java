package br.com.inova.sigin.delivery.pedido.controller;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;

import java.util.List;
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService service;

    @PostMapping
    public PedidoResponse criar(
            @RequestBody PedidoRequest request) {
        return service.criar(request);
    }
    @PutMapping("/{id}/aprovar")
    public PedidoResponse aprovar(@PathVariable Long id) {
        return service.aprovar(id);
    }
    @PutMapping("/{id}/pendente")
    public PedidoResponse colocarPendente(
            @PathVariable Long id,
            @RequestBody PedidoPendenciaRequest request) {

        return service.colocarPendente(id, request);
    }
    @PutMapping("/{id}/producao")
    public PedidoResponse iniciarProducao(@PathVariable Long id) {
        return service.iniciarProducao(id);
    }

    @PutMapping("/{id}/finalizar")
    public PedidoResponse finalizar(@PathVariable Long id) {
        return service.finalizar(id);
    }

    @PutMapping("/{id}/cancelar")
    public PedidoResponse cancelar(
            @PathVariable Long id,
            @RequestBody CancelamentoRequest request
    ) {
        return service.cancelar(id, request);
    }
    @GetMapping
    public List<PedidoResponse> listar() {

        return service.listar();
    }
    @GetMapping("/status/{status}")
    public List<PedidoResponse> listarPorStatus(
            @PathVariable StatusPedido status) {

        return service.listarPorStatus(status);
    }
    @GetMapping("/cozinha")
    public ResponseEntity<List<PedidoOperacaoResponse>> cozinha(
            @RequestParam String setor
    ) {
        return ResponseEntity.ok(
                service.pedidosCozinha(setor)
        );
    }
    @GetMapping("/finalizados")
    public ResponseEntity<List<PedidoResponse>> listarFinalizados() {
        return ResponseEntity.ok(
                service.listarFinalizados()
        );
    }
    @GetMapping("/entrega")
    public ResponseEntity<List<PedidoResponse>> listarEntrega() {
        return ResponseEntity.ok(
                service.listarEntrega()
        );
    }
    @PutMapping("/{id}/sair-entrega")
    public ResponseEntity<PedidoResponse> sairEntrega(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.sairParaEntrega(id)
        );
    }
    @PutMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponse> entregar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.entregar(id)
        );
    }
    @GetMapping("/entregues")
    public ResponseEntity<List<PedidoResponse>> listarEntregues() {
        return ResponseEntity.ok(
                service.listarEntregues()
        );
    }
    @GetMapping("/cozinha-operacao")
    public List<PedidoOperacaoResponse> cozinhaOperacao() {
        return service.pedidosOperacaoCozinha();
    }
    @GetMapping("/entrega-operacao")
    public ResponseEntity<List<PedidoOperacaoResponse>> listarEntregaOperacao() {
        return ResponseEntity.ok(
                service.listarEntregaOperacao()
        );
    }
    @GetMapping("/balcao")
    public ResponseEntity<List<PedidoBalcaoResponse>> balcao() {
        return ResponseEntity.ok(
                service.listarBalcao()
        );
    }
}