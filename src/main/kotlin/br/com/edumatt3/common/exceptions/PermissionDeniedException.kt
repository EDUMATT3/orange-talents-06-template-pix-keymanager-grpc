package br.com.edumatt3.common.exceptions

class PermissionDeniedException(override val message: String = "you don't have permission") : RuntimeException()
