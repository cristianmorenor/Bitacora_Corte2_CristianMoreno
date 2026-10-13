package com.restaurante.service;

import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroClienteRequestDTO;
import com.restaurante.model.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO dto);

    AuthResponseDTO registrarCliente(RegistroClienteRequestDTO dto);
}
