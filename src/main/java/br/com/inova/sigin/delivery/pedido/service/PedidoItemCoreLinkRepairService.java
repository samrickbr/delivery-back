package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.PedidoItemResponse;
import br.com.inova.sigin.delivery.core.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.repository.PedidoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PedidoItemCoreLinkRepairService {

    private final PedidoItemRepository pedidoItemRepository;
    private final CoreClient coreClient;

    @Transactional
    public Resultado reparar() {
        List<PedidoItem> itensSemVinculo =
                pedidoItemRepository.findByCoreItemIdIsNull();

        int reparados = 0;
        int ambiguos = 0;
        int semCorrespondencia = 0;
        int ignorados = 0;

        List<String> detalhes = new ArrayList<>();

        Set<Long> coreItemIdsJaUtilizados = new HashSet<>();

        for (PedidoItem item : pedidoItemRepository.findAll()) {
            if (item.getCoreItemId() != null) {
                coreItemIdsJaUtilizados.add(item.getCoreItemId());
            }
        }

        Map<Long, PedidoResponse> pedidosCore = new HashMap<>();

        for (PedidoItem item : itensSemVinculo) {
            Pedido pedido = item.getPedido();

            if (pedido == null || pedido.getCorePedidoId() == null) {
                ignorados++;
                detalhes.add(
                        "Item " + item.getId()
                                + ": pedido sem core_pedido_id."
                );
                continue;
            }

            Long corePedidoId = pedido.getCorePedidoId();

            PedidoResponse corePedido = pedidosCore.computeIfAbsent(
                    corePedidoId,
                    coreClient::buscarPedido
            );

            if (corePedido == null || corePedido.itens() == null) {
                semCorrespondencia++;
                detalhes.add(
                        "Item " + item.getId()
                                + ": pedido Core " + corePedidoId
                                + " sem itens disponíveis."
                );
                continue;
            }

            List<PedidoItemResponse> candidatos = corePedido.itens()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(coreItem -> coreItem.id() != null)
                    .filter(coreItem ->
                            !coreItemIdsJaUtilizados.contains(coreItem.id()))
                    .filter(coreItem -> corresponde(item, coreItem))
                    .toList();

            if (candidatos.size() == 1) {
                Long coreItemId = candidatos.getFirst().id();

                item.setCoreItemId(coreItemId);
                coreItemIdsJaUtilizados.add(coreItemId);

                reparados++;
                continue;
            }

            if (candidatos.isEmpty()) {
                semCorrespondencia++;
                detalhes.add(
                        "Item " + item.getId()
                                + ": nenhuma correspondência inequívoca "
                                + "no pedido Core " + corePedidoId + "."
                );
            } else {
                ambiguos++;
                detalhes.add(
                        "Item " + item.getId()
                                + ": " + candidatos.size()
                                + " correspondências possíveis "
                                + "no pedido Core " + corePedidoId + "."
                );
            }
        }

        if (reparados > 0) {
            pedidoItemRepository.saveAll(
                    itensSemVinculo.stream()
                            .filter(item -> item.getCoreItemId() != null)
                            .toList()
            );
        }

        return new Resultado(
                itensSemVinculo.size(),
                reparados,
                ambiguos,
                semCorrespondencia,
                ignorados,
                detalhes
        );
    }

    private boolean corresponde(
            PedidoItem local,
            PedidoItemResponse core
    ) {
        int atributosComparados = 0;

        if (local.getCoreProdutoId() != null) {
            atributosComparados++;

            if (!Objects.equals(
                    local.getCoreProdutoId(),
                    core.produtoId()
            )) {
                return false;
            }
        }

        if (local.getQuantidade() != null
                && core.quantidade() != null) {

            atributosComparados++;

            if (BigDecimal.valueOf(local.getQuantidade())
                    .compareTo(core.quantidade()) != 0) {
                return false;
            }
        }

        if (local.getValorUnitario() != null
                && core.valorUnitario() != null) {

            atributosComparados++;

            if (local.getValorUnitario()
                    .compareTo(core.valorUnitario()) != 0) {
                return false;
            }
        }

        if (local.getValorTotal() != null
                && core.valorTotal() != null) {

            atributosComparados++;

            if (local.getValorTotal()
                    .compareTo(core.valorTotal()) != 0) {
                return false;
            }
        }

        if (local.getProdutoNome() != null
                && core.produto() != null) {

            atributosComparados++;

            if (!local.getProdutoNome()
                    .trim()
                    .equalsIgnoreCase(core.produto().trim())) {
                return false;
            }
        }

        if (local.getSetor() != null
                && core.setor() != null) {

            atributosComparados++;

            if (!local.getSetor()
                    .trim()
                    .equalsIgnoreCase(core.setor().trim())) {
                return false;
            }
        }
        System.out.println(
                "REPARO CORE | local=" + local.getId()
                        + " | core=" + core.id()
                        + " | produto=" + local.getCoreProdutoId() + "/" + core.produtoId()
                        + " | qtd=" + local.getQuantidade() + "/" + core.quantidade()
                        + " | unit=" + local.getValorUnitario() + "/" + core.valorUnitario()
                        + " | total=" + local.getValorTotal() + "/" + core.valorTotal()
                        + " | nome=" + local.getProdutoNome() + "/" + core.produto()
                        + " | setor=" + local.getSetor() + "/" + core.setor()
                        + " | atributos=" + atributosComparados
        );

        return atributosComparados >= 2;
    }

    public record Resultado(
            int encontrados,
            int reparados,
            int ambiguos,
            int semCorrespondencia,
            int ignorados,
            List<String> detalhes
    ) {
    }
}
