package org.mercadodominio.controllers;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.mercadodominio.models.entities.Categoria;
import org.mercadodominio.models.entities.Cliente;

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaController {

    @GET
    public List<Categoria> getAllCategorias() {
        return Categoria.listAll(); // Método do PanacheEntity para listar todas as categorias
    }
    
    @GET
    @Path("/{id}")
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
    public Response criarCategoria(Categoria categoria) {
        if(categoria.getCategoriaId() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID não pode ser fornecido para categoria").build();
        }
        if(categoria.getCategoriaNome() == null || categoria.getCategoriaNome().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O nome da categoria não pode ser vazio").build();
        }

        categoria.persist();

        return Response.ok(categoria).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
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
