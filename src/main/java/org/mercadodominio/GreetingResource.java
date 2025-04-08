package org.mercadodominio;

import org.mercadodominio.controllers.*;
import org.mercadodominio.models.entities.*;
import jakarta.ws.rs.core.Response;
import java.util.List;
 
public class GreetingResource {

    private final ClienteController clienteController = new ClienteController();
    private final ProdutoController produtoController = new ProdutoController();
    private final CategoriaController categoriaController = new CategoriaController();
    private final FuncionarioController funcionarioController = new FuncionarioController();

    // ==================== CLIENTE ====================

    public Response listarClientes() {
        List<Cliente> clientes = clienteController.listarClientes();
        return Response.ok(clientes).build();
    }

    public Response buscarCliente(Long id) {
        Cliente cliente = clienteController.buscarCliente(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Cliente não encontrado").build();
        }
        return Response.ok(cliente).build();
    }

    public Response adicionarCliente(Cliente cliente) {
        clienteController.adicionarCliente(cliente);
        if (cliente == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O cliente enviado é inválido ou está vazio.")
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    public Response editarCliente(Long id, Cliente cliente) {
        clienteController.editarCliente(id, cliente);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(cliente).build();
    }

    public Response excluirCliente(Long id) {
        clienteController.excluirCliente(id);
        return Response.noContent().build();
    }

    // ==================== PRODUTO ====================

    public Response listarProdutos() {
        List<Produto> produtos = produtoController.getAllProdutos();
        return Response.ok(produtos).build();
    }

    public Response buscarProduto(Long id) {
        Response produto = produtoController.getProdutoById(id);
        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Produto não encontrado").build();
        }
        return Response.ok(produto).build();
    }

    public Response adicionarProduto(Produto produto) {
        produtoController.criarProduto(produto);
        return Response.status(Response.Status.CREATED).entity(produto).build();
    }

    public Response editarProduto(Long id, Produto produto) {
        produtoController.atualizarProduto(id, produto);
        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(produto).build();
    }

    public Response deletarProduto(Long id) {
        produtoController.deletarProduto(id);
        return Response.noContent().build();
    }

    // ==================== CATEGORIA ====================

    public Response listarCategorias() {
        List<Categoria> categorias = categoriaController.getAllCategorias();
        return Response.ok(categorias).build();
    }
 
    public Response buscarCategoria(Long id) {
        Response categoria = categoriaController.getCategoriaById(id);
        if (categoria == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Categoria não encontrada").build();
        }
        return Response.ok(categoria).build();
    }


    // ==================== FUNCIONARIO ====================

    public Response listarFuncionarios() {
        List<Funcionario> funcionarios = funcionarioController.getAllUFuncionarios();
        return Response.ok(funcionarios).build();
    }

    public Response buscarFuncionario(Long id) {
        Response funcionario = funcionarioController.getFuncionarioById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Funcionário não encontrado")
                    .build();
        }
        return Response.ok(funcionario).build();
    }

    public Response adicionarFuncionario(Funcionario funcionario) {
        funcionarioController.addFuncionario(funcionario);
        return Response.status(Response.Status.CREATED).entity(funcionario).build();
    }

    public Response editarFuncionario(Long id, Funcionario funcionario) {
        funcionarioController.editFuncionario(id, funcionario);
        return Response.ok(funcionario).build();
    }

    public Response excluirFuncionario(Long id) {
        funcionarioController.deleteFuncionario(id);
        return Response.noContent().build();
    }
}