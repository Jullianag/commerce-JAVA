package com.meusprojetos.commerce.dto;

import com.meusprojetos.commerce.entities.Category;
import com.meusprojetos.commerce.entities.Product;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public class ProductDTO {

    private Long id;
    @Size(min = 3, max = 80, message = "O nome precisa ter de 3 a 80 caracteres.")
    @NotBlank(message = "Campo obrigatório!")
    private String name;
    @Size(min = 10, message = "Descrição deve ter no mínimo 10 caracteres!")
    @NotBlank(message = "Campo obrigatório!")
    private String description;
    @Positive(message = "O preço deve ser positivo!")
    @NotNull(message = "O preço não pode ser nulo!")
    private Double price;
    private String imgUrl;

    @NotEmpty(message = "Não pode ser vazio!")
    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        for (Category cat : entity.getCategories()) {
            categories.add(new CategoryDTO(cat));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
