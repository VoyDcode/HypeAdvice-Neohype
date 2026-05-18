package com.example.hypeadvice.domain.exception;

import java.io.Serializable;

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
