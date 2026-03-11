package com.uteq.edu.ec.ms_clientes.service;

import com.uteq.edu.ec.ms_clientes.dto.FacturaDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    private final RestTemplate restTemplate;

    private static final String URL_FACTURAS = "http://74.249.40.210:8080/api/facturas";

    public FacturaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<FacturaDto> obtenerFacturasPorCedula(String cedula) {
        try {
            ResponseEntity<List<FacturaDto>> response = restTemplate.exchange(
                    URL_FACTURAS,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FacturaDto>>() {}
            );

            List<FacturaDto> facturas = response.getBody();

            if (facturas == null) {
                return Collections.emptyList();
            }

            return facturas.stream()
                    .filter(f -> f.getCliente() != null)
                    .filter(f -> f.getCliente().getDni() != null)
                    .filter(f -> f.getCliente().getDni().trim().equals(cedula.trim()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}