package com.gamertx.persistence;

import com.gamertx.domain.Product;
import com.gamertx.domain.repository.ProductRepository;
import com.gamertx.persistence.crud.ProductoCrudRepository;
import com.gamertx.persistence.entity.products_view.Imagen;
import com.gamertx.persistence.entity.products_view.Producto;
import com.gamertx.persistence.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//Con esta etiqueta le estamos indicando a Spring que la clase esta interactuando con la Base de datos realizando operaciones
@Repository
public class ProductoRepository implements ProductRepository {
    @Autowired
    private ProductoCrudRepository productoCrudRepository;
    @Autowired
    private ProductMapper mapper;

    @Override
    public List<Product> getAll(){
        List<Producto> productos = (List<Producto>) productoCrudRepository.findAll();
        List<Product> products = new ArrayList<>(productos.size());

        for (Producto producto : productos) {
            List<Imagen> imagenes = producto.getImagenes();
            List<String> urls = new ArrayList<>(imagenes.size());

            for (Imagen imagen : imagenes) {
                urls.add(imagen.getUrl());
            }

            Product product = mapper.toProduct(producto);
            product.setUrlsImages(urls);
            products.add(product);
        }
        return products;
    }

    @Override
    public Optional<List<Product>> getByCategory(int categoryId) {
        List<Producto> productos = productoCrudRepository.findByIdCategoriaOrderByNombreAsc(categoryId);
        return Optional.of(mapper.toProducts(productos));
    }

    @Override
    public Optional<List<Product>> getScarseProducts(int quantity) {
        Optional <List<Producto>> productos = productoCrudRepository.findByStockLessThan(quantity);
        return productos.map(prods -> mapper.toProducts(prods));
    }

    @Override
    public Optional<Product> getProduct(int productId) {
        return productoCrudRepository.findById(productId).map(producto -> mapper.toProduct(producto));
    }


    @Override
    public Product saveProduct(Product product) {
        Producto producto = mapper.toProducto(product);
        return mapper.toProduct(productoCrudRepository.save(producto));
    }

    @Override
    public void deleteProduct(int productId) {
        productoCrudRepository.deleteById(productId);
    };
    @Override
    public Product updateProduct(Product newProduct, int id) {
        Producto newProducto = mapper.toProducto(newProduct);
        return productoCrudRepository.findById(id).map(
                producto -> {
                    producto.setIdCategoria(newProducto.getIdCategoria());
                    producto.setMarca(newProducto.getMarca());
                    producto.setIdOferta(newProducto.getIdOferta());
                    producto.setNombre(newProducto.getNombre());
                    producto.setStock(newProducto.getStock());
                    producto.setDescripcion(newProducto.getDescripcion());
                    producto.setPrecio(newProducto.getPrecio());
                    producto.setFechaCreacion(newProducto.getFechaCreacion());
                    producto.setValoracion(newProducto.getValoracion());
                    return mapper.toProduct(productoCrudRepository.save(producto));
                }
        ).get();
    }
}
