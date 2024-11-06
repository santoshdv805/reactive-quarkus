package org.pms.resource;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Path("product")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ProductResource {

	@GET
	public Uni<List<Product>> getAll() {
		return Product.listAll(Sort.by("price"));
	}

	@GET
	@Path("{id}")
	public Uni<Response> getById(Long id) {
		return Product.findById(id)
				.onItem().transform(entity -> entity!=null?Response.ok(entity):Response.status(Response.Status.NO_CONTENT))
                .onItem().transform(Response.ResponseBuilder :: build);
	}
	
	@GET
	@Path("getProductByIdAndQuantity/{id}/{count}")
	public Uni<Response> getProductByIdAndQuantity(Long id, Long count) {
		return Product.find("id =:id and quantity >= :count", Parameters.with("id", id).and("count", count).map())
				.list().onItem().transform(entity -> entity!=null && !entity.isEmpty()? Response.ok(entity): Response.status(Response.Status.NO_CONTENT))
				.onItem().transform(Response.ResponseBuilder :: build);
	}
	
	
	@POST
    public Uni<Response> addProduct(Product product) {
        if (product == null || product.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        return Panache.withTransaction(product::persist)
                    .replaceWith(Response.ok(product).status(CREATED)::build);
    }

	@PUT
    @Path("{id}")
    public Uni<Response> updateProduct(Long id, Product product) {
        if (product == null || product.name == null) {
            throw new WebApplicationException("Product name was not set on request.", 422);
        }

        return Panache
                .withTransaction(() -> Product.<Product> findById(id)
                    .onItem().ifNotNull().invoke(entity -> {
                    entity.name = product.name;
                    entity.description = product.description; 
                    entity.price = product.price; 
                    entity.quantity = product.quantity;
                    })
                )
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }
	
	@DELETE
    @Path("{id}")
    public Uni<Response> deleteProduct(Long id) {
        return Panache.withTransaction(() -> Product.deleteById(id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

	
	@Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            Throwable throwable = exception;

            int code = 500;
            if (throwable instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            // This is a Mutiny exception and it happens, for example, when we try to insert a new
            // fruit but the name is already in the database
            if (throwable instanceof CompositeException) {
                throwable = ((CompositeException) throwable).getCause();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", throwable.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", throwable.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}
