package org.mercadodominio.controllers;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.acme.idempotency.Idempotent;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.mercadodominio.models.entities.Categoria;
import org.mercadodominio.models.entities.Cliente;
import org.mercadodominio.models.entities.Produto;

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaController {

    @GET
    @RateLimit(value = 5, window = 1, windowUnit = ChronoUnit.MINUTES)
    @Idempotent
    public List<Categoria> getAllCategorias() {
        return Categoria.listAll(); // Método do PanacheEntity para listar todas as categorias
    }

    @GET
    @Path("/{id}")
    @RateLimit(value = 5, window = 1, windowUnit = ChronoUnit.MINUTES)
    @Idempotent
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Categoria encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Categoria.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada"
            )
    })
    public Response getCategoriaById(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID inválido").build();
        }

        Categoria categoria = Categoria.findById(id);
        if (categoria == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Categoria não encontrada").build();
        }
        return Response.ok(categoria).build();
    }

    @POST
    @Transactional
    @Idempotent(expireAfter = 7200)
    public Response criarCategoria(Categoria categoria) {
        if (categoria.getCategoriaId() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID não pode ser fornecido para categoria").build();
        }
        if (categoria.getCategoriaNome() == null || categoria.getCategoriaNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O nome da categoria não pode ser vazio").build();
        }

        categoria.persist();

        // return Response.ok(categoria).build();

        return Response
                .created(URI.create("/categoria/" + categoria.getCategoriaId()))
                .entity(categoria)
                .build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @Idempotent

    public Response editarCategoria(@PathParam("id") Long id, Categoria categoriaAtualizada) {
        Categoria categoria = Categoria.findById(id);
        if (categoria == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (categoriaAtualizada.getCategoriaNome() == null || categoriaAtualizada.getCategoriaNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do categoria é obrigatório").build();
        }

        categoria.setCategoriaNome(categoriaAtualizada.getCategoriaNome());

        categoria.persist();

        return Response.ok(categoria).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response excluirCategoria(Long id) {
        boolean deleted = Categoria.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

}
