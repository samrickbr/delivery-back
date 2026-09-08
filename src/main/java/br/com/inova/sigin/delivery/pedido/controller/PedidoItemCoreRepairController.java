package br.com.inova.sigin.delivery.pedido.controller;

import br.com.inova.sigin.delivery.pedido.service.PedidoItemCoreLinkRepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/pedido-itens")
@RequiredArgsConstructor
public class PedidoItemCoreRepairController {

    private final PedidoItemCoreLinkRepairService repairService;

    @PostMapping("/reparar-core")
    public ResponseEntity<?> repararCore() {
        return ResponseEntity.ok(
                repairService.reparar()
        );
    }
}