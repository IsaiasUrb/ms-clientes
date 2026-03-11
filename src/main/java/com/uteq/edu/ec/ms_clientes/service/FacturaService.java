package com.uteq.edu.ec.ms_clientes.service;

import com.uteq.edu.ec.ms_clientes.dto.FacturaDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
            String cedulaLimpia = cedula == null ? "" : cedula.trim();

            FacturaDto[] response = restTemplate.getForObject(URL_FACTURAS, FacturaDto[].class);

            if (response == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(response)
                    .filter(f -> f.getCliente() != null)
                    .filter(f -> f.getCliente().getDni() != null)
                    .filter(f -> f.getCliente().getDni().trim().equals(cedulaLimpia))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}