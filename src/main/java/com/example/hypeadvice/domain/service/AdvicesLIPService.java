package com.example.hypeadvice.domain.service;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.exception.RecursoNaoEncontradoException;
import com.example.hypeadvice.domain.utils.Utils;
import com.example.hypeadvice.domain.vo.AdviceListVO;
import com.example.hypeadvice.domain.vo.AdviceVO;
import com.example.hypeadvice.domain.vo.Slip;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * Servico responsavel pela integracao com a API publica Advice Slip
 * (<a href="https://api.adviceslip.com/">https://api.adviceslip.com/</a>).
 *
 * <p>Encapsula as chamadas HTTP via Unirest, faz o parsing do JSON usando
 * {@link Utils#jsonToObject} e converte respostas de erro da API
 * (status 200 com payload de notice) em {@link RecursoNaoEncontradoException}.
 */
@Service
public class AdvicesLIPService {

    private static final String API_BASE_URL = "https://api.adviceslip.com/advice";
    private static final String API_SEARCH_URL = "https://api.adviceslip.com/advice/search/";

    /**
     * Solicita um conselho aleatorio a API externa.
     *
     * @return {@link Advice} construido a partir da descricao retornada
     * @throws UnirestException em caso de falha na chamada HTTP
     */
    public Advice gerar() throws UnirestException {
        HttpResponse<String> response = Unirest.get(API_BASE_URL)
                .header("Accept-Language", "br")
                .header("Content-Type", "application/json")
                .asString();
        int status = response.getStatus();
        if (HttpStatus.OK.value() == status) {
            try {
                String body = response.getBody();
                AdviceVO adviceVO = Utils.jsonToObject(AdviceVO.class, body);
                if (adviceVO != null) {
                    Slip slip = adviceVO.getSlip();
                    return new Advice(slip.getAdvice());
                }
            } catch (Exception e) {
                throw new RuntimeException("Status Code " + status + ", message " + e.getMessage());
            }
            throw new RuntimeException("Status Code " + status + ", message " + response.getStatusText());
        } else {
            throw new RuntimeException("Status Code " + status + ", message " + response.getStatusText());
        }
    }

    /**
     * Busca conselhos na API externa pelo termo informado na descricao.
     *
     * @param descricao termo de busca
     * @return VO com a lista de conselhos encontrados
     * @throws RecursoNaoEncontradoException quando a API retorna mensagem de "no advice slips found"
     * @throws UnirestException em caso de falha na chamada HTTP
     */
    public AdviceListVO buscarByDescricao(String descricao) throws UnirestException {
        HttpResponse<String> response = Unirest.get(API_SEARCH_URL + descricao)
                .header("Accept-Language", "br")
                .header("Content-Type", "application/json")
                .asString();
        int status = response.getStatus();
        if (HttpStatus.OK.value() == status) {
            try {
                String body = response.getBody();
                if (body.contains("No advice slips found matching that search term")) {
                    throw new RecursoNaoEncontradoException("Nenhum conselho encontrado para a descricao informada.");
                }
                AdviceListVO vo = Utils.jsonToObject(AdviceListVO.class, body);
                if (vo != null) {
                    return vo;
                }
            } catch (RecursoNaoEncontradoException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Status Code " + status + ", message " + e.getMessage());
            }
            throw new RuntimeException("Status Code " + status + ", message " + response.getStatusText());
        } else {
            throw new RuntimeException("Status Code " + status + ", message " + response.getStatusText());
        }
    }

    /**
     * Busca um conselho na API externa pelo ID.
     *
     * <p>A API responde com HTTP 200 mesmo quando o ID nao existe,
     * retornando um payload com {@code "type":"notice"}. Esse caso eh
     * detectado e convertido em {@link RecursoNaoEncontradoException}.
     *
     * <p>Como a API nao retorna o campo {@code date} na busca por ID,
     * a data atual eh injetada no JSON antes do parse para manter a
     * coluna preenchida na tabela.
     *
     * @param id identificador do conselho
     * @return VO contendo o conselho encontrado em uma lista de um item
     * @throws RecursoNaoEncontradoException quando o ID nao existe
     * @throws UnirestException em caso de falha na chamada HTTP
     */
    public AdviceListVO buscarById(Long id) throws UnirestException {
        HttpResponse<String> response = Unirest.get(API_BASE_URL + "/" + id)
                .header("Accept-Language", "br")
                .header("Content-Type", "application/json")
                .asString();
        int status = response.getStatus();
        if (HttpStatus.OK.value() == status) {
            try {
                String body = response.getBody();
                if (body.contains("Advice slip not found") || body.contains("\"type\":\"notice\"")) {
                    throw new RecursoNaoEncontradoException("Conselho com ID " + id + " nao encontrado.");
                }
                body = adicionarDate(body);
                AdviceVO adviceVO = Utils.jsonToObject(AdviceVO.class, body);
                if (adviceVO != null && adviceVO.getSlip() != null) {
                    AdviceListVO vo = new AdviceListVO();
                    vo.setSlips(Collections.singletonList(adviceVO.getSlip()));
                    return vo;
                }
            } catch (RecursoNaoEncontradoException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Status Code " + status + ", message " + e.getMessage());
            }
            throw new RuntimeException("Status Code " + status + ", message " + response.getStatusText());
        } else {
            throw new RuntimeException("Status Code " + status + ", message " + response.getStatusText());
        }
    }

    /**
     * Injeta um campo {@code date} com a data/hora atual dentro do objeto
     * {@code slip} do JSON retornado pela API. Necessario porque a busca
     * por ID nao retorna esse campo, e o VO espera ele preenchido.
     */
    private String adicionarDate(String body) {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        return body.replace("}}", ",\"date\":\"" + date + "\"}}");
    }
}
