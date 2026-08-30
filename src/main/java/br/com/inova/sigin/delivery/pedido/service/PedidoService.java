package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoCriacaoService criacaoService;
    private final PedidoConsultaService consultaService;
    private final PedidoOperacaoService operacaoService;
    private final PedidoCancelamentoService cancelamentoService;
    private final PedidoEntregaService entregaService;
    private final PedidoItemOperacaoService itemOperacaoService;

    // Criacao

    public PedidoResponse criar(
            PedidoRequest request,
            String authorization
    ) {
        return criacaoService.criar(request, authorization);
    }

    // Consulta

    public List<PedidoResponse> listar() {
        return consultaService.listar();
    }

    public List<PedidoResponse> listarPorStatus(StatusPedido status) {
        return consultaService.listarPorStatus(status);
    }

    public List<PedidoResponse> listarFinalizados() {
        return consultaService.listarFinalizados();
    }

    public List<PedidoResponse> listarEntrega() {
        return consultaService.listarEntrega();
    }

    public List<PedidoResponse> listarEntregues() {
        return consultaService.listarEntregues();
    }

    public List<PedidoBalcaoResponse> listarBalcao() {
        return consultaService.listarBalcao();
    }

    public List<PedidoBalcaoResponse> listarSeparacao() {
        return consultaService.listarSeparacao();
    }

    public List<PedidoBalcaoResponse> listarRetirada() {
        return consultaService.listarRetirada();
    }

    public List<PedidoResponse> listarMeus(String authorization) {
        return consultaService.listarMeus(authorization);
    }

    public List<PedidoOperacaoResponse> pedidosCozinha(String setor) {
        return consultaService.pedidosCozinha(setor);
    }

    public List<PedidoOperacaoResponse> pedidosOperacaoCozinha() {
        return consultaService.pedidosOperacaoCozinha();
    }

    public List<PedidoOperacaoResponse> listarEntregaOperacao() {
        return consultaService.listarEntregaOperacao();
    }

    public List<PedidoHistoricoResponse> listarHistorico(Long pedidoId) {
        return consultaService.listarHistorico(pedidoId);
    }

    public PedidoSituacaoFinanceiraResponse consultarSituacaoFinanceira(
            Long pedidoId
    ) {
        return consultaService.consultarSituacaoFinanceira(
                pedidoId
        );
    }

    // Operacao

    public PedidoResponse aprovar(
            Long id,
            String authorization
    ) {
        return operacaoService.aprovar(id, authorization);
    }

    public PedidoResponse colocarPendente(
            Long id,
            String setor,
            PedidoPendenciaRequest request,
            String authorization
    ) {
        return operacaoService.colocarPendente(
                id,
                setor,
                request,
                authorization
        );
    }

    public PedidoResponse iniciarProducao(
            Long id,
            String setor,
            String authorization
    ) {
        return operacaoService.iniciarProducao(
                id,
                setor,
                authorization
        );
    }

    public PedidoResponse finalizar(
            Long id,
            String setor,
            String authorization
    ) {
        return operacaoService.finalizar(
                id,
                setor,
                authorization
        );
    }

    public PedidoResponse conferir(
            Long id,
            String authorization
    ) {
        return operacaoService.conferir(id, authorization);
    }

    public PedidoResponse separar(
            Long id,
            String authorization
    ) {
        return operacaoService.separar(id, authorization);
    }

    public PedidoResponse liberarEntrega(
            Long id,
            SeparacaoRequest request,
            String authorization
    ) {
        return operacaoService.liberarEntrega(
                id,
                request,
                authorization
        );
    }

    // Cancelamento

    public PedidoResponse cancelar(
            Long id,
            String setor,
            CancelamentoRequest request
    ) {
        return cancelamentoService.cancelar(
                id,
                setor,
                request
        );
    }

    public PedidoResponse cancelarPedido(
            Long id,
            CancelamentoRequest request
    ) {
        return cancelamentoService.cancelarPedido(
                id,
                request
        );
    }

    public PedidoResponse cancelarItens(
            Long id,
            String setor,
            CancelamentoItensRequest request
    ) {
        return cancelamentoService.cancelarItens(
                id,
                setor,
                request
        );
    }

    public PedidoResponse cancelarPedidoCompleto(
            Long id,
            String justificativa
    ) {
        return cancelamentoService.cancelarPedidoCompleto(
                id,
                justificativa
        );
    }

    // Entrega

    public PedidoResponse sairParaEntrega(Long id) {
        return entregaService.sairParaEntrega(id);
    }

    public PedidoResponse entregar(Long id) {
        return entregaService.entregar(id);
    }

    // Item Operacao

    public PedidoResponse iniciarProducaoItem(
            Long pedidoId,
            Long itemId
    ) {
        return itemOperacaoService.iniciarProducaoItem(
                pedidoId,
                itemId
        );
    }

    public PedidoResponse colocarPendenteItem(
            Long pedidoId,
            Long itemId,
            PedidoPendenciaRequest request
    ) {
        return itemOperacaoService.colocarPendenteItem(
                pedidoId,
                itemId,
                request
        );
    }

    public PedidoResponse finalizarItem(
            Long pedidoId,
            Long itemId
    ) {
        return itemOperacaoService.finalizarItem(
                pedidoId,
                itemId
        );
    }
}