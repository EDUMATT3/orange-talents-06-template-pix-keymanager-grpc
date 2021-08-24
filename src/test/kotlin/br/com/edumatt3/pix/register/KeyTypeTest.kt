package br.com.edumatt3.pix.register

import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class KeyTypeTest{

    //CPF
    @Test
    internal fun `should be true for a valid cpf`() {
        assertTrue(KeyType.CPF.validate("00000000000"))
    }

    @Test
    internal fun `should be false for a invalid cpf`() {
        assertFalse(KeyType.CPF.validate("a0000000000"))
    }

    @Test
    internal fun `should be false when the cpf is not informed`() {
        with(KeyType.CPF){
            assertFalse(validate(null))
            assertFalse(validate(""))
        }
    }

    //PHONENUMBER
    @Test
    internal fun `should be true for a valid phonenumber`(){
        assertTrue(KeyType.PHONE.validate("+5519999999999"))
    }

    @Test
    internal fun `should be false for a invalid phonenumber`(){
        assertFalse(KeyType.PHONE.validate("+551999999999c"))
    }

    @Test
    internal fun `should be false when phonenumber is not informed`(){
        with(KeyType.PHONE){
            assertFalse(validate(null))
            assertFalse(validate(""))
        }
    }

    //Email

    @Test
    internal fun `should be false when email isnt infromed`(){
        with(KeyType.EMAIL){
            assertFalse(validate(null))
            assertFalse(validate(""))
        }
    }
}