package com.restaurante.mapper;

import com.restaurante.model.domain.Usuario;
import com.restaurante.model.dto.request.UsuarioRequestDTO;
import com.restaurante.model.dto.response.UsuarioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Usuario toDomain(UsuarioRequestDTO dto);

    UsuarioResponseDTO toDTO(Usuario usuario);
}
