package com.restaurante;

import com.restaurante.repository.EventoPedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RestauranteApplicationTests {

	@MockitoBean
	private EventoPedidoRepository eventoPedidoRepository;

	@Test
	void contextLoads() {
	}

}
