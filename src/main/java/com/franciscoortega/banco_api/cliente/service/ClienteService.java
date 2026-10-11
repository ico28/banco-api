package com.franciscoortega.banco_api.cliente.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.franciscoortega.banco_api.cliente.dto.ClienteRequest;
import com.franciscoortega.banco_api.cliente.dto.ClienteResponse;
import com.franciscoortega.banco_api.cliente.model.Cliente;
import com.franciscoortega.banco_api.cliente.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.findByDocumento(request.documento()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un cliente con ese documento");
        }
        Cliente cliente = new Cliente();
        cliente.setDocumento(request.documento());
        cliente.setNombre(request.nombre());
        cliente.setApellidos(request.apellidos());
        cliente.setEmail(request.email());
        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return clienteRepository.findById(id)
                .map(ClienteResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente no encontrado"));
    }
}