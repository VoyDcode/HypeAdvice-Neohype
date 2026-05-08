package com.example.hypeadvice.domain.bean;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.service.AdviceService;
import com.example.hypeadvice.domain.vo.AdviceListVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Managed Bean da tela {@code advice-list.xhtml} (pesquisa de conselhos).
 *
 * <p>Suporta dois modos de busca contra a API externa Advice Slip:
 * <ul>
 *   <li>Por descricao (texto livre).</li>
 *   <li>Por ID.</li>
 * </ul>
 */
@Named
@ViewScoped
public class AdviceListBean extends Bean {

    @Autowired
    private AdviceService adviceService;

    private Advice advice = new Advice();
    private AdviceListVO adviceListVO;

    private Long idBusca;

    public void initBean() {
        advice = new Advice();
    }

    /** Executa a busca por descricao na API externa. */
    public void buscar() {
        try {
            this.adviceListVO = adviceService.buscar(advice);
            addFaceMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Conselhos localizados.");
        } catch (Exception e) {
            addMessageError(e);
        }
    }

    /**
     * Executa a busca por ID na API externa. Em caso de ID inexistente,
     * exibe a mensagem da {@code RecursoNaoEncontradoException} ao usuario.
     */
    public void buscarPorId() {
        try {
            this.adviceListVO = adviceService.buscarById(idBusca);
            addFaceMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Conselho localizado com sucesso.");
        } catch (Exception e) {
            addFaceMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public Long getIdBusca() {
        return idBusca;
    }

    public void setIdBusca(Long idBusca) {
        this.idBusca = idBusca;
    }

    public AdviceListVO getAdviceListVO() {
        return adviceListVO;
    }

    public void setAdviceListVO(AdviceListVO adviceListVO) {
        this.adviceListVO = adviceListVO;
    }

    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }
}
