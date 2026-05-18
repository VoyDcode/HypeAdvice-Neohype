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

@Service
public class AdvicesLIPService {

    private static final String API_BASE_URL = "https://api.adviceslip.com/advice";
    private static final String API_SEARCH_URL = "https://api.adviceslip.com/advice/search/";

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

    private String adicionarDate(String body) {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        return body.replace("}}", ",\"date\":\"" + date + "\"}}");
    }
}
