package com.restaurante.controller;

import com.restaurante.model.domain.Rol;
import com.restaurante.repository.EventoPedidoRepository;
import com.restaurante.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlatoControllerTest {

    private static final String PLATO_JSON = """
            {"nombre":"Picanha","precio":52000,"categoria":"CORTES","esCorte":true,"minutosPreparacion":25}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private EventoPedidoRepository eventoPedidoRepository;

    private String bearer(Rol rol) {
        return "Bearer " + jwtService.generarToken("usuario-" + rol.name().toLowerCase(), rol);
    }

    @Test
    @DisplayName("Sin token - debe responder 401")
    void crear_sinToken_debeResponder401() throws Exception {
        mockMvc.perform(post("/api/v1/platos").contentType(MediaType.APPLICATION_JSON).content(PLATO_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Rol MESERO - no puede crear platos (403)")
    void crear_conRolMesero_debeResponder403() throws Exception {
        mockMvc.perform(post("/api/v1/platos").header("Authorization", bearer(Rol.MESERO))
                        .contentType(MediaType.APPLICATION_JSON).content(PLATO_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Rol ADMIN - crea, consulta y borra un plato")
    void crudCompleto_conRolAdmin_debeFuncionar() throws Exception {
        String respuesta = mockMvc.perform(post("/api/v1/platos").header("Authorization", bearer(Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content(PLATO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.activo").value(true))
                .andReturn().getResponse().getContentAsString();
        String id = respuesta.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/v1/platos/" + id).header("Authorization", bearer(Rol.COCINERO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Picanha"));

        mockMvc.perform(delete("/api/v1/platos/" + id).header("Authorization", bearer(Rol.ADMIN)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/platos/" + id).header("Authorization", bearer(Rol.ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Plato invalido - debe responder 400 con el detalle de validacion")
    void crear_datosInvalidos_debeResponder400() throws Exception {
        mockMvc.perform(post("/api/v1/platos").header("Authorization", bearer(Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"precio\":-1,\"categoria\":\"CORTES\",\"esCorte\":true,\"minutosPreparacion\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Menu - es publico, no necesita token")
    void menu_sinToken_debeResponder200() throws Exception {
        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(status().isOk());
    }
}
