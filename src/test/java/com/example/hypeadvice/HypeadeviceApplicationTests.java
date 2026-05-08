package com.example.hypeadvice;

import com.example.hypeadvice.domain.service.AdviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class HypeadeviceApplicationTests {

	@Autowired
	private AdviceService adviceService;

	@Test
	void contextLoads() {
		assertNotNull(adviceService, "AdviceService deve ser carregado pelo contexto Spring.");
	}

}
