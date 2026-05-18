package com.example.hypeadvice.domain.service;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.repository.AdviceRepository;
import com.example.hypeadvice.domain.vo.AdviceListVO;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdviceService {

    private final AdviceRepository adviceRepository;
    private final AdvicesLIPService advicesLIPService;

    public AdviceService(AdviceRepository adviceRepository, AdvicesLIPService advicesLIPService) {
        this.adviceRepository = adviceRepository;
        this.advicesLIPService = advicesLIPService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Advice save(Advice advice) {
        return adviceRepository.saveAndFlush(advice);
    }

    @Transactional(readOnly = true)
    public List<Advice> findAll() {
        return adviceRepository.findAll();
    }

    public Advice gerar() throws UnirestException {
        return advicesLIPService.gerar();
    }

    public AdviceListVO buscar(Advice advice) throws UnirestException {
        String descricao = advice.getDescricao();
        if (StringUtils.isNotBlank(descricao)) {
            return advicesLIPService.buscarByDescricao(descricao);
        }
        return null;
    }

    public AdviceListVO buscarById(Long id) throws UnirestException {
        if (id != null) {
            return advicesLIPService.buscarById(id);
        }
        return null;
    }
}
