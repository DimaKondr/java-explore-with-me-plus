package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryRequest;
import ru.practicum.ewm.model.Category;

@UtilityClass
public class CategoryMapper {

    Category toEntity(NewCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }

    Category toEntity(CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }

    CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}