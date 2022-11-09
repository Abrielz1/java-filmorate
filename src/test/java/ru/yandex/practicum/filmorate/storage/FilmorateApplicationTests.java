package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.http.HttpClient;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
		HttpClient httpClient = HttpClient.newBuilder().build();
	}

}
