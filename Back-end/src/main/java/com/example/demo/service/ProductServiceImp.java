package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Category;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product_Image;
import com.example.demo.entity.Product_SKU;
import com.example.demo.entity.Recommendation;
import com.example.demo.entity.Review;
import com.example.demo.entity.Product;
import com.example.demo.payload.ProductDetailDTO;
import com.example.demo.payload.ProductListDTO;
import com.example.demo.payload.CategoryDTO;
import com.example.demo.payload.ProductCreateDTO;
import com.example.demo.payload.ProductDTO;
import com.example.demo.payload.ProductImageDTO;
import com.example.demo.payload.ProductIncludeImageDTO;
import com.example.demo.payload.ProductSkuDTO;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductSKURepository;
import com.example.demo.repository.RecommendationRepository;
import com.example.demo.repository.ReviewRepository;


@Service
public class ProductServiceImp implements ProductService{

	private final ProductSKURepository productSKURepository;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartItemRepository cartItemRepository;
	private final RecommendationRepository recommendationRepository;
	private final ReviewRepository reviewRepository;

	public ProductServiceImp(ProductRepository productRepository, CategoryRepository categoryRepository, ProductSKURepository productSKURepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository, RecommendationRepository recommendationRepository, ReviewRepository reviewRepository) {
		super();
		this.productRepository= productRepository;
		this.categoryRepository= categoryRepository;
		this.productSKURepository= productSKURepository;
		this.orderItemRepository= orderItemRepository;
		this.cartItemRepository= cartItemRepository;
		this.recommendationRepository= recommendationRepository;
		this.reviewRepository= reviewRepository;
	}

//	//List Product [User] ->ok
//	@Override
//	public List<ProductDTO> listAllProducts() {
//		if(productRepository.findAll().isEmpty())
//			throw new NullPointerException("Error: No object found.");
//
//		List<ProductDTO> productDTOList= new ArrayList<>();
//
//
//		for(Products product: productRepository.findAll()) {
//			ProductDTO productDTO= new ProductDTO();
//			productDTO.setProduct_id(product.getProduct_id());
//			productDTO.setProduct_status_id(product.getProduct_status_id());
//			productDTO.setProduct_name(product.getProduct_name());
//			productDTO.setDescription_list(product.getDescription_list());
//			productDTO.setDescription_details(product.getDescription_details());
//			productDTO.setSearch_word(product.getSearch_word());
//			productDTO.setPrice(product.getPrice());
//			productDTOList.add(productDTO);
//		}
//		return productDTOList;
//	}


	// get product by id [user]->ok
	@Override
	public ProductDetailDTO getProductByIdUser(String id) {
		Product products = productRepository.findById(id).orElseThrow(() -> new NullPointerException("Error: No object found."));

		ProductDetailDTO productDetailDTO= new ProductDetailDTO();
		List<ProductDetailDTO> productDTOList= new ArrayList<ProductDetailDTO>();

		List<ProductSkuDTO> productSkuDTOList= new ArrayList<ProductSkuDTO>();
		List<String> pList= new ArrayList<>();
		for(Product_Image p: products.getProduct_Image()) {
			pList.add(p.getUrl());
		}
		productDetailDTO.setUrlImage(pList);
		productDetailDTO.setProduct_id(products.getProduct_id());
		productDetailDTO.setProduct_status_id(products.getProduct_status_id());
		productDetailDTO.setProduct_name(products.getProduct_name());
		productDetailDTO.setDescription_list(products.getDescription_list());
		productDetailDTO.setDescription_details(products.getDescription_details());
		productDetailDTO.setPrice(products.getPrice());
		for(Product_SKU pSKU: products.getProductSKUs()) {
			ProductSkuDTO productSkuDTO= new ProductSkuDTO();
			productSkuDTO.setId(pSKU.getId());
			productSkuDTO.setStock(pSKU.getStock());
			productSkuDTO.setSale_limit(pSKU.getSale_limit());
			productSkuDTO.setSize(pSKU.getSize());
			productSkuDTO.setIs_deleted(pSKU.isIs_deleted());
			productSkuDTO.setProduct_id(products.getProduct_id());
			productSkuDTOList.add(productSkuDTO);
		}
		productDetailDTO.setProductSKUs(productSkuDTOList);
		return productDetailDTO;
	}

	//Create product - Admin
//	@Override
//	public ProductIncludeImageDTO createProduct(ProductIncludeImageDTO productRequest) {
//
//		Set<Product_Image> productImageDTOList= new HashSet<>();
//		Product_Image productImage= new Product_Image();
//		Products products= new Products();
//
//		products.setProduct_id(productRequest.getProduct_id());
//		products.setProduct_status_id(productRequest.getProduct_status_id());
//		products.setProduct_name(productRequest.getProduct_name());
//		products.setDescription_list(productRequest.getDescription_list());
//		products.setDescription_details(productRequest.getDescription_details());
//		products.setSearch_word(productRequest.getSearch_word());
//		products.setDiscount_id(productRequest.getDiscount_id());
//		for(ProductImageDTO p: productRequest.getProductImage()) {
//			productImage.setName(p.getName());
//			productImage.setProduct_image_id(p.getProduct_image_id());
//			productImage.setUrl(p.getUrl());
//			productImageDTOList.add(productImage);
//		}
//		products.setProduct_Image(productImageDTOList);
//
//
//		return productRepository.save(products);
//
//	}

	//Create product(category+image) - Admin
	@Override
	public Product createProductAll(ProductCreateDTO productRequest) {
		Product product=  productRepository.findById(productRequest.getProduct_id()).get();
		Set<Product_Image> productImageList= new HashSet<>();

		Set<Category> categoryList= new HashSet<Category>();


		Category category= categoryRepository.findByName(productRequest.getCategory().get(0).getCategory_name());

		List<Product_SKU> product_SKU_List= new ArrayList<Product_SKU>();
		Product_SKU product_SKU= new Product_SKU();

		Product products= new Product();

		if(category == null) {
			for(CategoryDTO c: productRequest.getCategory()) {
				Category cate= new Category();
				cate.setName(c.getCategory_name());
				cate.setIs_deleted(c.isIs_deleted());
				categoryList.add(cate);
			}
		}
		else {
			categoryList.add(category);
		}
		products.setCategories(categoryList);
		for(ProductImageDTO p: productRequest.getProductImage()) {
			Product_Image productImage= new Product_Image();
			productImage.setName(p.getName());
			productImage.setUrl(p.getUrl());
			productImage.setProducts(product);
			productImageList.add(productImage);
		}
		products.setProduct_Image(productImageList);
		products.setProduct_id(productRequest.getProduct_id());
		products.setProduct_status_id(productRequest.getProduct_status_id());
		products.setProduct_name(productRequest.getProduct_name());
		products.setDescription_list(productRequest.getDescription_list());
		products.setDescription_details(productRequest.getDescription_details());
		products.setSearch_word(productRequest.getSearch_word());
		products.setPrice(productRequest.getPrice());
//		for(ProductSkuDTO p: productRequest.getProductSKUs()) {
//			product_SKU.setId(p.getId());
//			product_SKU.setStock(p.getStock());
//			product_SKU.setSale_limit(p.getSale_limit());
//			product_SKU.setSize(p.getSize());
//			product_SKU.setIs_deleted(p.isIs_deleted());
//			product_SKU_List.add(product_SKU);
//		}
//
//		Set<Product_SKU> product_SKUs = new HashSet<Product_SKU>();
//
//		product_SKU_List.forEach(p -> { product_SKUs.add(p); });
//
//		products.setProductSKUs(product_SKUs);

		return productRepository.save(products);
	}

		@Override
		public Product createProduct(ProductDTO productRequest) {
			List<Product> productList= productRepository.findAll();
			Product_SKU product_SKU= new Product_SKU();

			Product products= new Product();

			for(Product p: productList) {
				if(productRequest.getProduct_id().equalsIgnoreCase(p.getProduct_id())) {
					return null;
				}
				else {
					products.setProduct_id(productRequest.getProduct_id());
					products.setProduct_status_id(productRequest.getProduct_status_id());
					products.setProduct_name(productRequest.getProduct_name());
					products.setDescription_list(productRequest.getDescription_list());
					products.setDescription_details(productRequest.getDescription_details());
					products.setSearch_word(productRequest.getSearch_word());
				}
			}

			return productRepository.save(products);
		}

	// Get product by id [Admin]->ok
	@Override
	public ProductListDTO getProductByIdAdmin(String id) {
		Product products = productRepository.findById(id).orElseThrow(() -> new NullPointerException("Error: No object found."));

		ProductListDTO ProductListDTO= new ProductListDTO();
		List<ProductListDTO> productDTOList= new ArrayList<ProductListDTO>();

		List<ProductSkuDTO> productSkuDTOList= new ArrayList<ProductSkuDTO>();
		List<CategoryDTO> categoryDTOList= new ArrayList<CategoryDTO>();

		List<ProductImageDTO> pList= new ArrayList<ProductImageDTO>();

		CategoryDTO categoryDTO= new CategoryDTO();
		for(Category c: products.getCategories()) {
			categoryDTO.setId(c.getId());
			categoryDTO.setCategory_name(c.getName());
			categoryDTO.setIs_deleted(c.isIs_deleted());
			categoryDTOList.add(categoryDTO);
		}
		ProductListDTO.setCategory(categoryDTOList);
		for(Product_Image p: products.getProduct_Image()) {
			ProductImageDTO pDto= new ProductImageDTO();
			pDto.setName(p.getName());
			pDto.setProduct_image_id(p.getProduct_image_id());
			pDto.setUrl(p.getUrl());
			pList.add(pDto);
		}
		ProductListDTO.setProductImage(pList);
		ProductListDTO.setProduct_id(products.getProduct_id());
		ProductListDTO.setProduct_status_id(products.getProduct_status_id());
		ProductListDTO.setProduct_name(products.getProduct_name());
		ProductListDTO.setDescription_list(products.getDescription_list());
		ProductListDTO.setDescription_details(products.getDescription_details());
		ProductListDTO.setSearch_word(products.getSearch_word());
		ProductListDTO.setPrice(products.getPrice());
		for(Product_SKU pSKU: products.getProductSKUs()) {
			ProductSkuDTO productSkuDTO= new ProductSkuDTO();
			productSkuDTO.setId(pSKU.getId());
			productSkuDTO.setStock(pSKU.getStock());
			productSkuDTO.setSale_limit(pSKU.getSale_limit());
			productSkuDTO.setSize(pSKU.getSize());
			productSkuDTO.setIs_deleted(pSKU.isIs_deleted());
			productSkuDTO.setProduct_id(products.getProduct_id());
			productSkuDTOList.add(productSkuDTO);
		}
		ProductListDTO.setProductSKUs(productSkuDTOList);
		return ProductListDTO;
	}

	//Delete PRoduct [Admin] - ok
	@Override
	public void deleteProduct(String id) {
		Product products= productRepository.findById(id).orElseThrow(() -> new NullPointerException("Error: No object found."));
		Collection<Category> categories = products.getCategories();
		Set<Product_SKU> productSKUs = products.getProductSKUs();
		List<Recommendation> recommendations = recommendationRepository.findByProduct(products);
		List<Review> reviews = reviewRepository.findByProducts(products);
		if (!recommendations.isEmpty()) {
			recommendationRepository.deleteAllInBatch(recommendations);
		}
		if (!reviews.isEmpty()) {
			reviewRepository.deleteAllInBatch(reviews);
		}
		if (!categories.isEmpty()) {
			for (Category category : categories) {
				category.getProducts().remove(products);
			}
			categoryRepository.saveAllAndFlush(categories);
		}
		if (!productSKUs.isEmpty()) {
			for (Product_SKU productSKU : productSKUs) {
				List<OrderItem> orderItems = orderItemRepository.findByProductSKU(productSKU);
				if (!orderItems.isEmpty())
					orderItemRepository.deleteAllInBatch(orderItems);
				List<CartItem> cartItems = cartItemRepository.findByProductSKU(productSKU);
				if (!cartItems.isEmpty())
					cartItemRepository.deleteAllInBatch(cartItems);
			}
		}

		productRepository.delete(products);
	}

	//Update Product [Admin] - ok
	@Override
	public ProductDTO updateProductById(String id, ProductDTO productRequest) {
		Product products= productRepository.findById(id).orElseThrow(() -> new NullPointerException("Error: No object found."));

		products.setDescription_details(productRequest.getDescription_details());
		products.setDescription_list(productRequest.getDescription_list());
		products.setProduct_name(productRequest.getProduct_name());
		products.setProduct_status_id(productRequest.getProduct_status_id());
		products.setSearch_word(productRequest.getSearch_word());
		productRepository.save(products);
		return getProductById(id);
	}

	//ok
	@Override
	public ProductDTO getProductById(String id) {
		Product products = productRepository.findById(id).orElseThrow(() -> new NullPointerException("Error: No object found."));

		ProductDTO productDTO= new ProductDTO();

		productDTO.setProduct_id(products.getProduct_id());
		productDTO.setProduct_status_id(products.getProduct_status_id());
		productDTO.setProduct_name(products.getProduct_name());
		productDTO.setDescription_list(products.getDescription_list());
		productDTO.setDescription_details(products.getDescription_details());
		productDTO.setSearch_word(products.getSearch_word());

		return productDTO;
	}


	//Delete Product-sku - Admin
//	@Override
//	public void deleteProductSku(Long id) {
//		Product_SKU product_SKU= productSKURepository.findById(id).orElseThrow(() -> new NullPointerException("Error: No object found."));
//		productSKURepository.delete(product_SKU);
//	}

	@Override
	public List<ProductDTO> listAllProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductIncludeImageDTO> listAllProductIncludeImage() {
		List<Product> products= productRepository.findAll();
		if(products.isEmpty())
			return new ArrayList<ProductIncludeImageDTO>();

		List<ProductIncludeImageDTO> productList= new ArrayList<>();


		for(Product product: products) {

			ProductIncludeImageDTO productDTO= new ProductIncludeImageDTO();
			productDTO.setProduct_id(product.getProduct_id());
			productDTO.setProduct_status_id(product.getProduct_status_id());
			productDTO.setProduct_name(product.getProduct_name());
			productDTO.setSearch_word(product.getSearch_word());
			productDTO.setPrice(product.getPrice());
			for(Product_Image p: product.getProduct_Image()) {
				if(p.isPrimary() == true) {
					productDTO.setImageUrl(p.getUrl());
				}
			}
			productList.add(productDTO);
		}
		return productList;
	}

	public List<ProductIncludeImageDTO> listAllProductIncludeImage(List<Product> products) {

		List<ProductIncludeImageDTO> productList= new ArrayList<>();

		for(Product product: products) {

			ProductIncludeImageDTO productDTO= new ProductIncludeImageDTO();
			productDTO.setProduct_id(product.getProduct_id());
			productDTO.setProduct_status_id(product.getProduct_status_id());
			productDTO.setProduct_name(product.getProduct_name());
			productDTO.setSearch_word(product.getSearch_word());
			productDTO.setPrice(product.getPrice());
			for(Product_Image p: product.getProduct_Image()) {
				if(p.isPrimary() == true) {
					productDTO.setImageUrl(p.getUrl());
				}
			}
			productList.add(productDTO);
		}
		return productList;
	}

	@Override
	public List<Product> listProductBySKUId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductIncludeImageDTO> search(String keyword) {
		if (keyword != null && !keyword.trim().isEmpty()) {
			List<Product> products = productRepository.search(keyword);
			if (!products.isEmpty())
				return listAllProductIncludeImage(products);
			return new ArrayList<ProductIncludeImageDTO>();
        }
        return listAllProductIncludeImage();
	}


//	@Override
//	public List<Products> listProductBySKUId(Long id) {
//		List<Products> resultOptional= productRepository.findByProductSKUsId(id);
//		if(resultOptional.isEmpty()) {
//			throw new NullPointerException("Error: No object found.");
//		}
//		else {
//			return resultOptional;
//		}
//	}

}
