package me.fivi.exception

import me.fivi.dto.ApiResponse
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
@Singleton
@Requires(classes = [Exception.class, ExceptionHandler.class])
class DefaultExceptionHandler implements ExceptionHandler<Exception, HttpResponse<?>> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionHandler.class)

    @Override
    HttpResponse<?> handle(HttpRequest request, Exception exception) {
        LOG.error("Exception caught by global handler", exception)

        return HttpResponse.serverError(ApiResponse.error("An unexpected error occurred"))
    }
}

@CompileStatic
@Singleton
@Requires(classes = [NotFoundException.class, ExceptionHandler.class])
class NotFoundExceptionHandler implements ExceptionHandler<NotFoundException, HttpResponse<?>> {

    @Override
    HttpResponse<?> handle(HttpRequest request, NotFoundException exception) {
        return HttpResponse.notFound(ApiResponse.error(exception.message))
    }
}

@CompileStatic
@Singleton
@Requires(classes = [AccountAlreadyExistsException.class, ExceptionHandler.class])
class AccountAlreadyExistsExceptionHandler implements ExceptionHandler<AccountAlreadyExistsException, HttpResponse<?>> {

    @Override
    HttpResponse<?> handle(HttpRequest request, AccountAlreadyExistsException exception) {
        return HttpResponse.status(HttpStatus.CONFLICT).body(ApiResponse.error(exception.message))
    }
}

@CompileStatic
@Singleton
@Requires(classes = [EmailConfirmationException.class, ExceptionHandler.class])
class EmailConfirmationExceptionHandler implements ExceptionHandler<EmailConfirmationException, HttpResponse<?>> {

    @Override
    HttpResponse<?> handle(HttpRequest request, EmailConfirmationException exception) {
        return HttpResponse.badRequest(ApiResponse.error(exception.message))
    }
}

@CompileStatic
@Singleton
@Requires(classes = [AuthenticationException.class, ExceptionHandler.class])
class AuthenticationExceptionHandler implements ExceptionHandler<AuthenticationException, HttpResponse<?>> {

    @Override
    HttpResponse<?> handle(HttpRequest request, AuthenticationException exception) {
        return HttpResponse.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(exception.message))
    }
}

@CompileStatic
@Singleton
@Requires(classes = [InvalidTokenException.class, ExceptionHandler.class])
class InvalidTokenExceptionHandler implements ExceptionHandler<InvalidTokenException, HttpResponse<?>> {

    @Override
    HttpResponse<?> handle(HttpRequest request, InvalidTokenException exception) {
        return HttpResponse.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(exception.message))
    }
}

@CompileStatic
@Singleton
@Requires(classes = [InvalidRequestException.class, ExceptionHandler.class])
class InvalidRequestExceptionHandler implements ExceptionHandler<InvalidRequestException, HttpResponse<?>> {

    @Override
    HttpResponse<?> handle(HttpRequest request, InvalidRequestException exception) {
        return HttpResponse.badRequest(ApiResponse.error(exception.message))
    }
}
