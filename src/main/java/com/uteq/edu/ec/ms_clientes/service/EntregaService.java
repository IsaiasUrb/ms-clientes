package com.uteq.edu.ec.ms_clientes.service;

import com.uteq.edu.ec.ms_clientes.dto.EntregaDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntregaService {

    private final RestTemplate restTemplate;

    private static final String URL_ENTREGAS = "http://130.107.144.11:8092/api/entregas";

    public EntregaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EntregaDto> obtenerEntregasPorCedula(String cedula) {
        try {
            ResponseEntity<List<EntregaDto>> response = restTemplate.exchange(
                    URL_ENTREGAS,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EntregaDto>>() {}
            );

            List<EntregaDto> entregas = response.getBody();

            if (entregas == null) {
                return Collections.emptyList();
            }

            return entregas.stream()
                    .filter(e -> e.getClientCedula() != null)
                    .filter(e -> e.getClientCedula().trim().equals(cedula.trim()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}