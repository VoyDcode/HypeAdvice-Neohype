package com.example.hypeadvice.domain.service;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.repository.AdviceRepository;
import com.example.hypeadvice.domain.vo.AdviceListVO;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de servico responsavel pela orquestracao das regras de negocio
 * relacionadas a entidade {@link Advice}.
 *
 * <p>Atua como fachada entre os Beans/Controllers e dois recursos:
 * <ul>
 *   <li>{@link AdviceRepository} - persistencia local (PostgreSQL).</li>
 *   <li>{@link AdvicesLIPService} - integracao com a API publica Advice Slip.</li>
 * </ul>
 */
@Service
public class AdviceService {

    private final AdviceRepository adviceRepository;
    private final AdvicesLIPService advicesLIPService;

    public AdviceService(AdviceRepository adviceRepository, AdvicesLIPService advicesLIPService) {
        this.adviceRepository = adviceRepository;
        this.advicesLIPService = advicesLIPService;
    }

    /**
     * Persiste um conselho na base local.
     * Em caso de qualquer excecao a transacao sofre rollback.
     *
     * @param advice conselho a ser salvo (nao deve ser nulo)
     * @return entidade gerenciada com ID gerado pelo banco
     */
    @Transactional(rollbackFor = Exception.class)
    public Advice save(Advice advice) {
        return adviceRepository.saveAndFlush(advice);
    }

    /**
     * Retorna todos os conselhos cadastrados na base local.
     * Operacao apenas de leitura.
     */
    @Transactional(readOnly = true)
    public List<Advice> findAll() {
        return adviceRepository.findAll();
    }

    /**
     * Solicita um conselho aleatorio a API externa Advice Slip.
     *
     * @return novo {@link Advice} preenchido com a descricao retornada pela API
     * @throws UnirestException em caso de falha na chamada HTTP
     */
    public Advice gerar() throws UnirestException {
        return advicesLIPService.gerar();
    }

    /**
     * Busca conselhos na API externa filtrando pela descricao informada.
     * Retorna {@code null} caso a descricao seja vazia ou em branco.
     *
     * @param advice objeto contendo a descricao a ser pesquisada
     * @return VO com a lista de conselhos encontrados, ou {@code null}
     * @throws UnirestException em caso de falha na chamada HTTP
     */
    public AdviceListVO buscar(Advice advice) throws UnirestException {
        String descricao = advice.getDescricao();
        if (StringUtils.isNotBlank(descricao)) {
            return advicesLIPService.buscarByDescricao(descricao);
        }
        return null;
    }

    /**
     * Busca um conselho na API externa pelo ID. O resultado eh adaptado para
     * {@link AdviceListVO} para reutilizar a mesma view do filtro por descricao.
     *
     * @param id identificador do conselho na API externa
     * @return VO com o conselho encontrado, ou {@code null} se o ID for nulo
     * @throws UnirestException em caso de falha na chamada HTTP
     * @throws com.example.hypeadvice.domain.exception.RecursoNaoEncontradoException
     *         quando o ID nao existe na API externa
     */
    public AdviceListVO buscarById(Long id) throws UnirestException {
        if (id != null) {
            return advicesLIPService.buscarById(id);
        }
        return null;
    }
}
