package com.example.hypeadvice.domain.bean;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.enums.AdviceTypeEnum;
import com.example.hypeadvice.domain.service.AdviceService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;

/**
 * Managed Bean da tela {@code advice-crud.xhtml} (cadastro de conselhos).
 *
 * <p>Mantido em escopo de view ({@link ViewScoped} - CDI) para preservar
 * o estado durante o ciclo de submit/render da pagina.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Listar conselhos cadastrados.</li>
 *   <li>Cadastrar novo conselho com validacao do campo tipo.</li>
 *   <li>Acionar a geracao de conselho aleatorio via API externa.</li>
 * </ul>
 */
@Named
@ViewScoped
public class AdviceBean extends Bean {

    @Autowired
    private AdviceService adviceService;

    private Advice advice = new Advice();
    private List<Advice> advices;

    /** Carrega a lista de conselhos cadastrados na inicializacao da view. */
    public void initBean() {
        advices = adviceService.findAll();
    }

    /**
     * Persiste o conselho corrente. Valida que o campo tipo foi informado,
     * evitando que a UI submeta um valor nulo (o select tem opcao "Selecionar"
     * sem valor associado).
     */
    public void salvar() {
        if (advice.getTipo() == null) {
            addFaceMessage(FacesMessage.SEVERITY_ERROR, "Erro", "O Tipo de Conselho e obrigatorio.");
            return;
        }
        adviceService.save(advice);
        advices.add(advice);
        resetAdvice();
        addFaceMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Conselho cadastrado com sucesso.");
    }

    /** Solicita um conselho aleatorio a API externa e preenche o formulario. */
    public void gerar() {
        try {
            advice = adviceService.gerar();
        } catch (UnirestException e) {
            addMessageError(e);
        }
    }

    public void resetAdvice() {
        advice = new Advice();
    }

    public AdviceTypeEnum[] getTipos() {
        return AdviceTypeEnum.values();
    }

    public List<Advice> getAdvices() {
        return advices;
    }

    public void setAdvices(List<Advice> advices) {
        this.advices = advices;
    }

    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }
}
