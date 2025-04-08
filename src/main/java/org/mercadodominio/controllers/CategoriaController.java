package org.mercadodominio.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.mercadodominio.models.entities.Categoria;

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
}
