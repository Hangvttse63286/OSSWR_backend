package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Category;
import com.example.demo.entity.Products;
import com.example.demo.payload.CategoryDTO;
import com.example.demo.payload.ProductIncludeSkuDTO;


public interface CategoryService {
	//Category
	List<CategoryDTO> listAllCategories();
	List<ProductIncludeSkuDTO> listProductByCategoryId(Long id);	
	void deleteCategory(Long id);
	CategoryDTO getCategoryById(Long id);
	CategoryDTO updateCategoryById(Long id, CategoryDTO categoryRequest);
	Category createCategory(CategoryDTO categoryRequest);
}
