package com.iftm.client.tests.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

// para testar camada de serviço utilize essa notação, que carrega o contexto com os recursos do Spring boot
@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {
		
	@InjectMocks
	private ClientService servico;
	
	@Mock
	private ClientRepository rep; 
	
	/**
	 * Atividade A6
	 * Cenário de Teste
	 * Entrada:
	 * 		- idExistente: 2
	 * Resultado:
	 * 		- void
	 */
	@Test
	public void testarApagarRetornaNadaQuandoIDExiste() {
		//construir cenário
		//entrada
		Long idExistente = 2l;
		//configurar Mock
		Mockito.doNothing().when(rep).deleteById(idExistente);
		//executar o teste
		Assertions.assertDoesNotThrow(()->{servico.delete(idExistente);});
		//verificar as execuções da classe mock e de seus métodos
		Mockito.verify(rep, Mockito.times(1)).deleteById(idExistente);
	}
	
	/**
	 * Atividade A6
	 * Cenário de Teste : id não existe e retorna exception
	 * Entrada:
	 * 		- idExistente: 1000
	 * Resultado:
	 * 		- ResourceNotFoundException
	 */
	@Test
	public void testarApagarRetornaExceptionQuandoIDNaoExiste() {
		//construir cenário
		//entrada
		Long idNaoExistente = 1000l;
		//configurar Mock
		Mockito.doThrow(EmptyResultDataAccessException.class).when(rep).deleteById(idNaoExistente);
		//executar o teste
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{servico.delete(idNaoExistente);});
		//verificar as execuções da classe mock e de seus métodos
		Mockito.verify(rep, Mockito.times(1)).deleteById(idNaoExistente);
	}	
	
	/**
	 * Exemplo Extra
	 * Cenário de Teste : método findByIncomeGreaterThan retorna a página com clientes corretos
	 * Entrada:
	 * 		- Paginação:
	 * 			- Pagina = 1;
	 * 			- 2
	 * 			- Asc
	 * 			- Income
	 * 		- Income: 4800.00
	 * 		- Clientes:
		Pagina: 0
		{
            "id": 7,
            "name": "Jose Saramago",
            "cpf": "10239254871",
            "income": 5000.0,
            "birthDate": "1996-12-23T07:00:00Z",
            "children": 0
        },
        
        {
            "id": 4,
            "name": "Carolina Maria de Jesus",
            "cpf": "10419244771",
            "income": 7500.0,
            "birthDate": "1996-12-23T07:00:00Z",
            "children": 0
        },
        Pagina: 1
        {
            "id": 8,
            "name": "Toni Morrison",
            "cpf": "10219344681",
            "income": 10000.0,
            "birthDate": "1940-02-23T07:00:00Z",
            "children": 0
        }
	 * Resultado:
	 * 		Página não vazia
	 * 		Página contendo um cliente
	 * 		Página contendo o cliente da página 1
	 */
	@Test
	public void testarApagarRetornaExceptionQuandoIDNaoExiste2() {
		//construir cenário
		//entrada do método que testado
		//PageRequest.of(
			// qual Página deverá ser retornada (1 = a segunda página)
			// quantidade de objetos(client) que serão apresentados por página
			// ordem de apresentação crescente(ASC) decrescente(DESC)
			// campo que irá ordenar a página
		PageRequest pageRequest = PageRequest.of(1, 2, Direction.valueOf("ASC"), "income");
		Double entrada = 4800.00;
		
		//retorno que o método da classe mock deverá retornar
		List <Client> lista = new ArrayList<Client>();
		lista.add(new Client(8L, "Toni Morrsion", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0));

		Page<Client> pag = new PageImpl<>(lista, pageRequest, 1);
		//configurar Mock
		Mockito.when(rep.findByIncomeGreaterThan(entrada, pageRequest)).thenReturn(pag);
		//executar o teste
		Page<ClientDTO> resultado = servico.findByIncomeGreaterThan(pageRequest, entrada);
		//verificar as execuções da classe mock e de seus métodos
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(1, resultado.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(rep, Mockito.times(1)).findByIncomeGreaterThan(entrada, pageRequest);
	}	
	
	/** Exercicios extras feitos em sala
	 * findByCpfLike deveria retornar uma página (e chamar o método findByCpfLike do repository)
	 * Cenário de teste
	 * Entradas necessárias:
	 *  - cpf : "%447%"
	 * 	- Uma PageRequest com os valores
	 * 		- page = 0
	 * 		- size = 3
	 * 		- direction = Direction.valueOf("ASC")
	 * 		- order = "name"
	 * 	- Lista de clientes esperada
        {
            "id": 4,
            "name": "Carolina Maria de Jesus",
            "cpf": "10419244771",
            "income": 7500.0,
            "birthDate": "1996-12-23T07:00:00Z",
            "children": 0
        },
                	
        Resultado Esperado:
        	- Página não vazia
        	- Página contendo um cliente
        	- Página contendo exatamente o cliente esperado.
	 */	
	
	@Test
	public void testarSeBuscarClientesPorCPFLikeRetornaUmaPaginaComClientesComCPFQueContemTextoInformado(){
		String cpf = "%447%";
		PageRequest pageRequest = PageRequest.of(0, 3, Direction.valueOf("ASC"), "name");	
		
		
		List <Client> listaClientes = new ArrayList<Client>();
		listaClientes.add(new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0));
		
		Page<Client> clientes = new PageImpl<Client>(listaClientes);
		
		Mockito.when(rep.findByCpfLike(cpf, pageRequest)).thenReturn(clientes);
		Page<ClientDTO> resultado = servico.findByCpfLike(pageRequest, cpf);
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(listaClientes.size(), resultado.getNumberOfElements());
		for (int i = 0; i < listaClientes.size(); i++) {
			Assertions.assertEquals(listaClientes.get(i), resultado.toList().get(i).toEntity());
		}		
		Mockito.verify(rep, Mockito.times(1)).findByCpfLike(cpf, pageRequest);		
	}	

	
}
