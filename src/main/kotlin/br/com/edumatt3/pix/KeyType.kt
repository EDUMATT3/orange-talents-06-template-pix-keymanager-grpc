package br.com.edumatt3.pix

import io.micronaut.validation.validator.constraints.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class KeyType {

    CPF {
        override fun validate(key: String?): Boolean {
            return when {
                key.isNullOrBlank() -> false
                !key.matches("\\d{11}".toRegex()) -> false
                else -> CPFValidator().run {
                    initialize(null)
                    isValid(key, null)
                }
            }
        }
    },
    PHONE {
        override fun validate(key: String?): Boolean {
            return if (key.isNullOrBlank()) false
            else key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun validate(key: String?): Boolean = if (key.isNullOrBlank()) false else EmailValidator().run {
                    initialize(null)
                    isValid(key, null)
                }
    },
    RANDOM {
        override fun validate(key: String?): Boolean = key.isNullOrBlank()
    };

    abstract fun validate(key: String?): Boolean
}