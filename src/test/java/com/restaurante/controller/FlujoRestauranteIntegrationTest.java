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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Recorre el flujo del restaurante de punta a punta: login, mesa, cuenta, pedido, cocina y pago
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlujoRestauranteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private EventoPedidoRepository eventoPedidoRepository;

    private String bearer(Rol rol) {
        return "Bearer " + jwtService.generarToken("usuario-" + rol.name().toLowerCase(), rol);
    }

    private String idDe(String json) {
        return json.replaceAll("^\\{\"id\":(\\d+).*", "$1");
    }

    @Test
    @DisplayName("Login del admin inicial - debe devolver un token JWT")
    void login_adminInicial_debeDevolverToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"AdminTest2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Registro de cliente - debe crear el usuario con rol CLIENTE")
    void registro_cliente_debeQuedarConRolCliente() throws Exception {
        mockMvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"cliente-nuevo\",\"password\":\"Clave12345\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    @DisplayName("Flujo completo - el pedido entregado se carga a la cuenta y se paga")
    void flujoCompleto_pedidoEntregado_debeCargarseALaCuenta() throws Exception {
        String plato = mockMvc.perform(post("/api/v1/platos").header("Authorization", bearer(Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bife de chorizo\",\"precio\":50000,\"categoria\":\"CORTES\",\"esCorte\":true,\"minutosPreparacion\":20}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String mesa = mockMvc.perform(post("/api/v1/mesas").header("Authorization", bearer(Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"numero\":77}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String idMesa = idDe(mesa);

        String cuenta = mockMvc.perform(post("/api/v1/mesas/" + idMesa + "/cuentas").header("Authorization", bearer(Rol.CAJERO)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String pedido = mockMvc.perform(post("/api/v1/pedidos").header("Authorization", bearer(Rol.CLIENTE))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"idMesa\":" + idMesa + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RECIBIDO"))
                .andReturn().getResponse().getContentAsString();
        String idPedido = idDe(pedido);

        mockMvc.perform(post("/api/v1/pedidos/" + idPedido + "/items").header("Authorization", bearer(Rol.MESERO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPlato\":" + idDe(plato) + ",\"cantidad\":2,\"terminoCoccion\":\"TRES_CUARTOS\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(100000.0));

        mockMvc.perform(patch("/api/v1/pedidos/" + idPedido + "/estado").param("nuevoEstado", "EN_PREPARACION")
                        .header("Authorization", bearer(Rol.CLIENTE)))
                .andExpect(status().isForbidden());

        for (String estado : new String[]{"EN_PREPARACION", "LISTO", "ENTREGADO"}) {
            mockMvc.perform(patch("/api/v1/pedidos/" + idPedido + "/estado").param("nuevoEstado", estado)
                            .header("Authorization", bearer(Rol.COCINERO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value(estado));
        }

        String idCuenta = idDe(cuenta);
        mockMvc.perform(get("/api/v1/cuentas/" + idCuenta).header("Authorization", bearer(Rol.CAJERO)))
                .andExpect(jsonPath("$.total").value(100000.0));

        mockMvc.perform(post("/api/v1/cuentas/" + idCuenta + "/pagos").header("Authorization", bearer(Rol.CAJERO))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"monto\":50000}"))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/api/v1/cuentas/" + idCuenta + "/pagos").header("Authorization", bearer(Rol.CAJERO))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"monto\":100000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CERRADA"));
    }

    @Test
    @DisplayName("Usuarios del personal - solo ADMIN los gestiona")
    void usuarios_soloAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios").header("Authorization", bearer(Rol.MESERO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo-cocinero\",\"password\":\"Clave12345\",\"rol\":\"COCINERO\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/usuarios").header("Authorization", bearer(Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo-cocinero\",\"password\":\"Clave12345\",\"rol\":\"COCINERO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("COCINERO"));
    }
}
