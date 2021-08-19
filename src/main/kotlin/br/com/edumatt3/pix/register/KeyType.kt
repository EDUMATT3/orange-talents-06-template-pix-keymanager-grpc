package br.com.edumatt3.pix.register

import io.micronaut.validation.validator.constraints.EmailValidator

enum class KeyType {

    CPF {
        override fun validate(key: String?): Boolean {
            return if (key.isNullOrBlank()) false
            else key.matches("\\d{11}".toRegex())
        }
    },
    PHONENUMBER {
        override fun validate(key: String?): Boolean {
            return if (key.isNullOrBlank()) false
            else key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun validate(key: String?): Boolean {
            if (key.isNullOrBlank()) return false

            return EmailValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }
    },
    RANDOM {
        override fun validate(key: String?): Boolean = key.isNullOrBlank()
    };

    abstract fun validate(key: String?): Boolean
}