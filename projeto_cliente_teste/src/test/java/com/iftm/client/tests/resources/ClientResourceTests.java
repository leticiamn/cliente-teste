package com.iftm.client.tests.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourceTests {
	private int qtdClientes = 12;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// Para o teste real da aplicação iremos comentar ou retirar.
	//@MockBean
	//private ClientService service;

//	@Test
//	public void testarListarTodosClientesRetornaOKeClientes() throws Exception {
//		//configuração do meu mock
//		/*
//		List<ClientDTO> listaClientes = new ArrayList<ClientDTO>();
//		listaClientes.add(new ClientDTO(
//				new Client(7l, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));
//		listaClientes.add(new ClientDTO(new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0,
//				Instant.parse("1996-12-23T07:00:00Z"), 0)));
//		listaClientes.add(new ClientDTO(
//				new Client(8l, "Toni Morrison", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0)));
//		Page<ClientDTO> page = new PageImpl<ClientDTO>(listaClientes);
//		when(service.findAllPaged(any())).thenReturn(page);
//		qtdClientes = 3;
//		*/
//
//		//iremos realizar o teste
//		mockMvc.perform(get("/clients")
//				.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.content").exists())
//				.andExpect(jsonPath("$.content").isArray())
//				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 7L).exists())
//				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 4L).exists())
//				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 8L).exists())
//				.andExpect(jsonPath("$.totalElements").value(qtdClientes));
//	}
	
	@Test
	public void testarBuscaPorIDExistenteRetornaJsonCorreto() throws Exception {
		long idExistente = 3L;
		ResultActions resultado = mockMvc.perform(get("/clients/{id}",idExistente)
				.accept(MediaType.APPLICATION_JSON));
		resultado.andExpect(status().isOk());
		resultado.andExpect(jsonPath("$.id").exists());
		resultado.andExpect(jsonPath("$.id").value(idExistente));
		resultado.andExpect(jsonPath("$.name").exists());		
		resultado.andExpect(jsonPath("$.name").value("Clarice Lispector"));
	}
	
	@Test
	public void testarBuscaPorIdNaoExistenteRetornaNotFound() throws Exception {
		long idNaoExistente = 300L;
		ResultActions resultado = mockMvc.perform(get("/clients/{id}", idNaoExistente)
				.accept(MediaType.APPLICATION_JSON));
		resultado.andExpect(status().isNotFound());
		resultado.andExpect(jsonPath("$.error").exists());
		resultado.andExpect(jsonPath("$.error").value("Resource not found"));
		resultado.andExpect(jsonPath("$.message").exists());
		resultado.andExpect(jsonPath("$.message").value("Entity not found"));
	}


	@Test
	public void testarRetornoInsert() throws Exception {
		Client dto = new Client(1L, "João", "12345678910", 999.0, Instant.parse("2019-01-30T06:01:46.730Z"), 1);
		String json = objectMapper.writeValueAsString(dto);
		ResultActions response =
				mockMvc.perform(post("/clients/")
						.content(json)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		response.andExpect(status().isCreated());
		response.andExpect(jsonPath("$.id").exists());
		response.andExpect(jsonPath("$.id").value(dto.getId()));
		response.andExpect(jsonPath("$.name").exists());
		response.andExpect(jsonPath("$.name").value("João"));
		response.andExpect(jsonPath("$.cpf").exists());
		response.andExpect(jsonPath("$.cpf").value("12345678910"));
		response.andExpect(jsonPath("$.income").exists());
		response.andExpect(jsonPath("$.income").value(999.0));
		response.andExpect(jsonPath("$.children").exists());
		response.andExpect(jsonPath("$.children").value(1));
	}

	@Test
	public void testarRetornoUpdate() throws Exception {
		ClientDTO dto = new ClientDTO(2l, "Leticia", "13312215512", 1000.0, Instant.parse("1996-07-12T06:01:49.766Z"), 2);
		String json = objectMapper.writeValueAsString(dto);
		ResultActions response =
				mockMvc.perform(put("/clients/2", dto)
						.content(json)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		response.andExpect(status().isOk());
		response.andExpect(jsonPath("$.id").exists());
		response.andExpect(jsonPath("$.id").value(dto.getId()));
		response.andExpect(jsonPath("$.name").exists());
		response.andExpect(jsonPath("$.name").value("Leticia"));
		response.andExpect(jsonPath("$.cpf").exists());
		response.andExpect(jsonPath("$.cpf").value("13312215512"));
		response.andExpect(jsonPath("$.income").exists());
		response.andExpect(jsonPath("$.income").value(1000.0));
		response.andExpect(jsonPath("$.children").exists());
		response.andExpect(jsonPath("$.children").value(2));
	}

	@Test
	public void testarRetornoUpdateIdNaoExistente() throws Exception {
		ClientDTO dto = new ClientDTO(3l, "Joaquim", "10020030010", 2000.0, Instant.parse("1996-07-12T06:01:49.766Z"), 5);
		String json = objectMapper.writeValueAsString(dto);
		ResultActions response =
				mockMvc.perform(put("/clients/1000", dto)
						.content(json)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		response.andExpect(status().isNotFound());
		response.andExpect(jsonPath("$.error").exists());
		response.andExpect(jsonPath("$.error").value("Resource not found"));
	}

	@Test
	public void testarRetornoDeleteIdNaoExistente() throws Exception {
		ClientDTO dto = new ClientDTO(3l, "Joaquim", "10020030010", 2000.0, Instant.parse("1996-07-12T06:01:49.766Z"), 5);
		String json = objectMapper.writeValueAsString(dto);
		ResultActions response =
				mockMvc.perform(delete("/clients/1000")
						.content(json)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		response.andExpect(status().isNotFound());
	}

	@Test
	public void testarRetornoDeleteQuandoIdExistir() throws Exception {
		ResultActions response =
				mockMvc.perform(delete("/clients/3")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		response.andExpect(status().isNoContent());
	}

	@Test
	public void testarFindByIncome() throws Exception {
		List<ClientDTO> clients = new ArrayList<>();

		JSONParser jsonParser= new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		clients.add(new ClientDTO(7L, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0));

		JSONArray listJson = (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(clients));

		double income = 5000.00;

		ResultActions response = mockMvc.perform(get("/clients/income/").param("income", String.valueOf(income)).accept(MediaType.APPLICATION_JSON));

		response.andExpect(status().isOk());
		response.andExpect(jsonPath("$.content").exists());
		response.andExpect(jsonPath("$.content").value(Matchers.containsInAnyOrder(listJson.toArray())));
		response.andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(1));
	}
}
