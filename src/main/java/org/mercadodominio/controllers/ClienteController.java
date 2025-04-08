package org.mercadodominio.controllers;

import java.util.List;
import org.mercadodominio.models.entities.Cliente;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    @GET
    public List<Cliente> listarClientes() {
        return Cliente.listAll(); // Método do PanacheEntity para listar todos os clientes
    }

    @GET
    @Path("/{id}")
    public Cliente buscarCliente(Long id) {
        return Cliente.findById(id); // Busca um cliente pelo ID
    }

    @POST
    @Transactional
    public Response adicionarCliente(Cliente cliente) {
        if (cliente.getClienteNome() == null || cliente.getClienteNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do cliente é obrigatório").build();
        }

        if (cliente.getClienteId() != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID não deve ser fornecido para um novo cliente").build();
        }

        cliente.persist();
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response editarCliente(@PathParam("id") Long id, Cliente clienteAtualizado) {
        Cliente cliente = Cliente.findById(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (clienteAtualizado.getClienteNome() == null || clienteAtualizado.getClienteNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do cliente é obrigatório").build();
        }

        cliente.setClienteNome(clienteAtualizado.getClienteNome());
        cliente.setClienteEmail(clienteAtualizado.getClienteEmail()); 
        cliente.setClienteIdade(clienteAtualizado.getClienteIdade());

        cliente.persist();

        return Response.ok(cliente).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response excluirCliente(Long id) {
        boolean deleted = Cliente.deleteById(id); // Deleta o cliente pelo ID
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
