package br.com.edumatt3.common

import io.micronaut.aop.Around

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Around
annotation class ExceptionHandlerAround