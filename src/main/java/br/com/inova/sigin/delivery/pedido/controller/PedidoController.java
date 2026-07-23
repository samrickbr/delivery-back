package br.com.inova.sigin.delivery.pedido.controller;

import br.com.inova.sigin.delivery.pedido.dto.PedidoPendenciaRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
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

    @PutMapping("/{id}/entrega")
    public PedidoResponse sairEntrega(@PathVariable Long id) {
        return service.sairEntrega(id);
    }

    @PutMapping("/{id}/finalizar")
    public PedidoResponse finalizar(@PathVariable Long id) {
        return service.finalizar(id);
    }

    @PutMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
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
    public List<PedidoResponse> cozinha() {

        return service.pedidosCozinha();
    }
    @GetMapping("/finalizados")
    public ResponseEntity<List<PedidoResponse>> listarFinalizados() {
        return ResponseEntity.ok(
                service.listarFinalizados()
        );
    }
}