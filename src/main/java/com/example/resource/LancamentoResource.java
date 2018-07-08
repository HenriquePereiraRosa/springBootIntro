/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.resource;

import com.example.event.RecursoCriadoEvent;
import com.example.model.Lancamento;
import com.example.repository.LancamentoRepository;
import com.example.repository.filter.LancamentoFilter;
import com.example.repository.projection.ResumoLancamento;
import com.example.service.LancamentoService;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author user
 */
        
@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
    
    @Autowired
    private LancamentoRepository lancamentoRepository;
    
    @Autowired
    private LancamentoService lancamentoService;
        
    @Autowired
    private ApplicationEventPublisher publisher;
    
    @GetMapping
    @CrossOrigin
    public Page<Lancamento> search(LancamentoFilter filter, Pageable pageable) {
        return lancamentoRepository.search(filter, pageable);
    } 
    
    
    @GetMapping(params = "resumo") // If there is an param called "resumo" in the requisition, then call this method.
    @CrossOrigin
    public Page<ResumoLancamento> resume(LancamentoFilter filter, Pageable pageable) {
        return lancamentoRepository.resume(filter, pageable);
    }
    
    // Save on db method
    @PostMapping
    @CrossOrigin
    public ResponseEntity<Lancamento> saveOnDb( @Valid @RequestBody Lancamento lancamento, HttpServletResponse response){
        Lancamento objectSaved = lancamentoRepository.save(lancamento);
        
        publisher.publishEvent(new RecursoCriadoEvent(this, response, objectSaved.getId())); // Create an event when an object is saved
        
        return ResponseEntity.status(HttpStatus.CREATED).body(objectSaved);
    }
    
    // SerchById
    @GetMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<? extends Object> searchById( @PathVariable Long id ) {
        Lancamento lancamento = lancamentoRepository.getOne(id);
        return (lancamento == null)? ResponseEntity.notFound().build() : ResponseEntity.ok(lancamento);
    }
    
    
     // Deletion By Name
    @DeleteMapping("/{id}")
    @CrossOrigin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id){
        Lancamento lancamento = lancamentoRepository.getOne(id);
        lancamentoRepository.delete(lancamento);
    }
    
    // Update resourse
    @PutMapping("/{id}")
    @CrossOrigin
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
    public ResponseEntity<Lancamento> atualizar(@PathVariable Long id, @Valid @RequestBody Lancamento lancamento) {
        try {
            Lancamento lancamentoFromDb = lancamentoService.update(id, lancamento);
            return ResponseEntity.ok(lancamentoFromDb);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
}
