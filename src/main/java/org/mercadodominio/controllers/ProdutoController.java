package org.mercadodominio.controllers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import io.smallrye.faulttolerance.api.RateLimit;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.mercadodominio.models.entities.Categoria;
import org.mercadodominio.models.entities.Produto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;

@Path("/produtos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoController {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RateLimit(value = 5, window = 1, windowUnit = ChronoUnit.MINUTES)
    public List<Produto> getAllProdutos() {
        return Produto.listAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @RateLimit(value = 5, window = 1, windowUnit = ChronoUnit.MINUTES)
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"
            )
    })
    public Response getProdutoById(@PathParam("id") Long id) {
        Produto produto = Produto.findById(id);
        return Response.ok(produto).build();
    }

    @POST
    @Transactional
    public Response criarProduto(Produto produto) {
        if (produto.getProdutoNome() == null || produto.getProdutoNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do produto é obrigatório").build();
        }

        if (produto.getProdutoId() != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID não deve ser fornecido para um novo produto").build();
        }

        Categoria categoria = Categoria.findById(produto.getProdutoCategoria().getCategoriaId());
        if (categoria == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Categoria inválida").build();
        }

        produto.setProdutoCategoria(categoria);

        Produto produtoExistente = Produto.find("produtoNome", produto.getProdutoNome()).firstResult();
        if (produtoExistente != null) {
            return Response.status(Response.Status.OK).entity(produtoExistente).build();
        }

        produto.persist();
        return Response.status(Response.Status.CREATED).entity(produto).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizarProduto(@PathParam("id") Long id, Produto produtoAtualizado) {
        Produto produto = Produto.findById(id);
        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Produto não encontrado").build();
        }

        if (produtoAtualizado.getProdutoNome() == null || produtoAtualizado.getProdutoNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nome do produto é obrigatório").build();
        }

        Categoria categoria = Categoria.findById(produtoAtualizado.getProdutoCategoria().getCategoriaId());
        if (categoria == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Categoria inválida").build();
        }

        produto.setProdutoNome(produtoAtualizado.getProdutoNome());
        produto.setProdutoCategoria(categoria);
        produto.setProdutoDescricao(produtoAtualizado.getProdutoDescricao());
        produto.setProdutoPreco(produtoAtualizado.getProdutoPreco());

        return Response.ok(produto).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletarProduto(@PathParam("id") Long id) {
        boolean deleted = Produto.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Produto não encontrado").build();
        }
        return Response.noContent().build();
    }
}