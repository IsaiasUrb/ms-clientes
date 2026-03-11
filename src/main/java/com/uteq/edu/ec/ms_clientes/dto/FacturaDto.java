package com.uteq.edu.ec.ms_clientes.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaDto {

    private Long id;
    private String fecha;
    private BigDecimal total;
    private FacturaClienteDto cliente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public FacturaClienteDto getCliente() {
        return cliente;
    }

    public void setCliente(FacturaClienteDto cliente) {
        this.cliente = cliente;
    }
}