package me.fivi.exception

import groovy.transform.CompileStatic

@CompileStatic
class NotFoundException extends RuntimeException {
    NotFoundException(String message) {
        super(message)
    }
}

@CompileStatic
class AccountAlreadyExistsException extends RuntimeException {
    AccountAlreadyExistsException(String message) {
        super(message)
    }
}

@CompileStatic
class EmailConfirmationException extends RuntimeException {
    EmailConfirmationException(String message) {
        super(message)
    }
}

@CompileStatic
class AuthenticationException extends RuntimeException {
    AuthenticationException(String message) {
        super(message)
    }
}

@CompileStatic
class InvalidTokenException extends RuntimeException {
    InvalidTokenException(String message) {
        super(message)
    }
}

@CompileStatic
class InvalidRequestException extends RuntimeException {
    InvalidRequestException(String message) {
        super(message)
    }
}