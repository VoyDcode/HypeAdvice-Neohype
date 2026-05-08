package com.example.hypeadvice.domain.exception;

import java.io.Serializable;

/**
 * Excecao de runtime usada para sinalizar que um recurso solicitado a uma
 * fonte de dados (no contexto deste projeto, a API externa Advice Slip)
 * nao foi encontrado.
 *
 * <p>Eh lancada principalmente em {@link com.example.hypeadvice.domain.service.AdvicesLIPService}
 * quando a API responde com payload de notice (ex.: {@code "type":"notice"})
 * apesar do HTTP 200, e capturada nos beans para exibir mensagem ao usuario.
 */
public class RecursoNaoEncontradoException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException() {
        super("Recurso não encontrado");
    }

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(Throwable causa) {
        super("Recurso não encontrado", causa);
    }

    public RecursoNaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
