/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.repository.lancamento;

import com.example.dto.LancamentoEstatisticaCategoria;
import com.example.dto.LancamentoEstatisticaDia;
import com.example.model.Lancamento;
import com.example.repository.filter.LancamentoFilter;
import com.example.repository.projection.ResumoLancamento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author user
 */
public interface LancamentoRepositoryQuery {
    
    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
    public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);
    public Page<Lancamento> search(LancamentoFilter filter, Pageable pageable);    
    public Page<ResumoLancamento> resume(LancamentoFilter filter, Pageable pageable);
}
