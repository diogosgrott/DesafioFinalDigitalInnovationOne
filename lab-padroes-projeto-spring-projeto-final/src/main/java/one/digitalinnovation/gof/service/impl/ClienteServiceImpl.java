package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClientesService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Imprelementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * intetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 *
 * @author Diogo
 *
 */

@Service
public class ClienteServiceImpl implements ClientesService {

	// TODO Singleton: Injetar os componentes do Spring com @Autowired
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;

	// TODO Strategy: Implementar os métodos definidos na interface
	// TODO Facade: Abstrair integrações com subsistemas, provendo uma interface
	// simples

	@Override
	public Iterable<Cliente> buscarTodos() {
		// Buscar todos os Clientes.
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Buscar Clientes por ID
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);

	}

	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe(pelo CEP)
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente)
		clienteRepository.save(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		// Buscar Cliente por ID, caso exista:
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if (clienteBd.isPresent()) {
			// Verificar se o Endereço do Cliente já existe (pelo CEP)
			// Caso não exista, integrar com o ViaCEP e persistir retorno
			// Alterar Cliente, vinculando o endereço (novo ou existente)
			salvarClienteComCep(cliente);
		}

	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID
		clienteRepository.deleteById(id);

	}

}
