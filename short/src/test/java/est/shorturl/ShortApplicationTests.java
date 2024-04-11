package est.shorturl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ShortApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() throws Exception {
		ShortModel model = new ShortModel();
		model.setOriginalUrl("https://example.com/library/react");
		model.setShortUrl("http://short.est/PdE6o");

		mockMvc.perform(MockMvcRequestBuilders.get("/encode")
				.param("url", model.getOriginalUrl()))
				.andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(model)));

		// test idempotency
		mockMvc.perform(MockMvcRequestBuilders.get("/encode")
				.param("url", model.getOriginalUrl()))
				.andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(model)));

		mockMvc.perform(MockMvcRequestBuilders.get("/decode")
				.param("url", model.getShortUrl()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(model)));

		mockMvc.perform(MockMvcRequestBuilders.get("/decode")
				.param("url", model.getShortUrl()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(model)));
	}

	@Test
	void missingParam() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/encode"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.get("/encode")
				.param("url", ""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.get("/decode"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.get("/decode")
				.param("url", ""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void badRequest() throws Exception {
		ShortModel model = new ShortModel();
		model.setOriginalUrl("Hello World!");
		model.setShortUrl("http://short.est/AMY8th0");

		mockMvc.perform(MockMvcRequestBuilders.get("/encode")
				.param("url", model.getOriginalUrl()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("bad request: no protocol: Hello World!"));

		mockMvc.perform(MockMvcRequestBuilders.get("/decode")
				.param("url", model.getOriginalUrl()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("bad request: no protocol: Hello World!"));

		mockMvc.perform(MockMvcRequestBuilders.get("/decode")
				.param("url", model.getShortUrl()))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content()
						.string("not found: unknown URL: http://short.est/AMY8th0"));
	}
}
