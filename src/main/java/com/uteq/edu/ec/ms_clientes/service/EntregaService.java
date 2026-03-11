package com.uteq.edu.ec.ms_clientes.service;

import com.uteq.edu.ec.ms_clientes.dto.EntregaDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
            String cedulaLimpia = cedula == null ? "" : cedula.trim();

            EntregaDto[] response = restTemplate.getForObject(URL_ENTREGAS, EntregaDto[].class);

            if (response == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(response)
                    .filter(e -> e.getClientCedula() != null)
                    .filter(e -> e.getClientCedula().trim().equals(cedulaLimpia))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}