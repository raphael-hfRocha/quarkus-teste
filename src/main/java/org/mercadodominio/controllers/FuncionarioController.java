package org.mercadodominio.controllers;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.idempotency.Idempotent;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.mercadodominio.models.entities.Cliente;
import org.mercadodominio.models.entities.Funcionario;
import org.mercadodominio.models.entities.Produto;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Path("/funcionarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuncionarioController {

    @GET
    @RateLimit(value = 5, window = 1, windowUnit = ChronoUnit.MINUTES)
    @Idempotent
    public List<Funcionario> getAllUFuncionarios() {
        return Funcionario.listAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @RateLimit(value = 5, window = 1, windowUnit = ChronoUnit.MINUTES)
    @Idempotent
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Funcionario encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Funcionario.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Funcionario não encontrado"
            )
    })
    public Response getFuncionarioById(@PathParam("id") Long id) {
        Funcionario funcionario = Funcionario.findById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Funcionário não encontrado").build();
        }
        return Response.ok(funcionario).build();
    }

    @POST
    @Transactional
    @Idempotent(expireAfter = 7200)
    public Response addFuncionario(@RequestBody Funcionario funcionario) {
        // Validação básica dos campos obrigatórios
        if (funcionario.getFuncionarioNome() == null || funcionario.getFuncionarioNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do funcionario é obrigatório").build();
        }

        // Garante que é um novo funcionário (ID deve ser nulo)
        if (funcionario.getFuncionarioId() != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID não deve ser fornecido para um novo funcionário").build();
        }

        Funcionario funcionarioExistente = Funcionario.find("funcionarioEmail", funcionario.getFuncionarioEmail()).firstResult();
        if (funcionarioExistente != null) {
            return Response.status(Response.Status.OK).entity(funcionarioExistente).build();
        }

        funcionario.persist();
        // return Response.status(Response.Status.CREATED).entity(funcionario).build();

        return Response
                .created(URI.create("/funcionario/" + funcionario.getFuncionarioId()))
                .entity(funcionario)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Idempotent
    public Response editFuncionario(@PathParam("id") Long id, @RequestBody Funcionario funcionarioAtualizado) {
        Funcionario funcionario = Funcionario.findById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Funcionário não encontrado").build();
        }

        if (funcionarioAtualizado.getFuncionarioNome() == null || funcionarioAtualizado.getFuncionarioNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do funcionario é obrigatório").build();
        }

        funcionario.setFuncionarioNome(funcionarioAtualizado.getFuncionarioNome());
        funcionario.setFuncionarioEmail(funcionarioAtualizado.getFuncionarioEmail());
        funcionario.setFuncionarioIdade(funcionarioAtualizado.getFuncionarioIdade());

        funcionario.persist();

        return Response.ok(funcionario).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Idempotent
    public Response deleteFuncionario(@PathParam("id") Long id) {
        boolean deleted = Funcionario.deleteById(id); // Deleta o funcionário pelo ID
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
