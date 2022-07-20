package model.dao;

import java.util.ArrayList;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {

	void insert(Seller obj);

	void update(Seller obj);

	void deleteById(Integer id);

	Seller findById(Integer id);

	ArrayList<Seller> findAll();
	
	ArrayList<Seller> findByDepartment(Department department);
}
