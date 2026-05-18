package com.example.hypeadvice.domain.bean;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.service.AdviceService;
import com.example.hypeadvice.domain.vo.AdviceListVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

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

    public void buscar() {
        try {
            this.adviceListVO = adviceService.buscar(advice);
            addFaceMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Conselhos localizados.");
        } catch (Exception e) {
            addMessageError(e);
        }
    }

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
