/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.exceptionHandler;

import com.example.service.exception.PessoaInexistenteOuInativaException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author user
 */
@ControllerAdvice // Notation to observe Exceptions for entire application
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource; // Spring Object to get the messages from messages.properties file.
    
    private List<Erro> criateErrorList( BindingResult bindingResult ) {
        List<Erro> errors = new ArrayList(); 
        for( FieldError fieldError : bindingResult.getFieldErrors() ) {
            String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String mensaDesenvolvedor = fieldError.toString();
            errors.add( new Erro( mensagemUsuario, mensaDesenvolvedor ));
        }
        return errors;
    }
    
    // Send message for Invalid resiquition( eg: invalid field) message.
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, 
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("requisicao.invalida", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = messageSource.getMessage("leia.atentamente", null, LocaleContextHolder.getLocale())
                + ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<Erro> errors = Arrays.asList( new Erro( mensagemUsuario, mensagemDesenvolvedor ) );
        return handleExceptionInternal(ex, errors,
                headers, HttpStatus.BAD_REQUEST, request);
    }

    // Send message for Invalid field value.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Erro> errors = criateErrorList( ex.getBindingResult() );
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request); 
    }
    
    // Send a message when is tried to delete a non existing resource
    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso.nao.existe", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
    
    // Send a message when is tried to delete a non existing resource
    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    public ResponseEntity<Object> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.nao.existe", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    
    // Send a message when is tried to delete a non existing resource
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.alvo.nao.pode.ser.null", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    
    // Send a message when is tried to delete a non existing resource
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    
    
    // This exception could be handled in ApiExceptionHandler.java, but how is an especific exception, could be handled here too.
    @ExceptionHandler({PessoaInexistenteOuInativaException.class})
    public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-inativa", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<ApiExceptionHandler.Erro> erros = Arrays.asList(new ApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }    
    
    public static class Erro {
            
            private String mensagemUsuario;
            private String mensagemDesenvovedor;

        public Erro(String mensagemUsuario, String mensagemDesenvovedor) {
            this.mensagemUsuario = mensagemUsuario;
            this.mensagemDesenvovedor = mensagemDesenvovedor;
        }

        public String getMensagemUsuario() {
            return mensagemUsuario;
        }

        public void setMensagemUsuario(String mensagemUsuario) {
            this.mensagemUsuario = mensagemUsuario;
        }

        public String getMensagemDesenvovedor() {
            return mensagemDesenvovedor;
        }

        public void setMensagemDesenvovedor(String mensagemDesenvovedor) {
            this.mensagemDesenvovedor = mensagemDesenvovedor;
        }
                        
    }    
    
}
