package com.example.hypeadvice.domain.enums;

/**
 * Tipos possiveis de um conselho. Persistido como string na coluna
 * {@code TIPO} da tabela {@code advice} via {@link javax.persistence.EnumType#STRING}.
 */
public enum AdviceTypeEnum {
    /** Conselho disponibilizado gratuitamente. */
    GRATUITO,
    /** Conselho que requer pagamento para ser acessado. */
    PAGO
}
