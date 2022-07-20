package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection con;

	public SellerDaoJDBC(Connection con) {
		this.con = con;
	}

	@Override
	public void insert(Seller obj) {

		Locale.setDefault(Locale.US);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = con.prepareStatement(
					"INSERT INTO seller (Name,Email,BirthDate,BaseSalary,DepartmentId) VALUE(?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
			} else {
				throw new DbException("Error: 0 rows affected");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(
					"UPDATE seller SET Name=?, Email=?, BirthDate=?, BaseSalary=?, DepartmentId=? WHERE Id = ?");
			ps.setString(1, obj.getName());
			ps.setString(2, obj.getName());
			ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			ps.setInt(6, obj.getId());
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("DELETE FROM seller WHERE Id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(
					"SELECT seller.*,department.Name as DepName\n" + "FROM seller INNER JOIN department\n"
							+ "ON seller.DepartmentId = department.Id\n" + "WHERE seller.Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {

				Department department = instantiateDepartment(rs);

				Seller seller = instantiateSeller(rs, department);

				return seller;
			}

			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(department);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepName"));
		return department;
	}

	@Override
	public ArrayList<Seller> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Seller> sellers = new ArrayList<>();
		try {

			ps = con.prepareStatement(
					"SELECT seller.*,department.Name as DepName\n" + "FROM seller INNER JOIN department\n"
							+ "ON seller.DepartmentId = department.Id\n" + "ORDER BY Id");
			rs = ps.executeQuery();

			while (rs.next()) {
				sellers.add(this.instantiateSeller(rs, this.instantiateDepartment(rs)));
			}
			return sellers;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public ArrayList<Seller> findByDepartment(Department department) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Seller> sellers = new ArrayList<>();

		try {
			ps = con.prepareStatement("SELECT seller.*,department.Name as DepName\n"
					+ "FROM seller INNER JOIN department\n" + "ON seller.DepartmentId = department.Id\n"
					+ "WHERE DepartmentId = ?\n" + "ORDER BY Name");
			ps.setInt(1, department.getId());
			rs = ps.executeQuery();
			while (rs.next()) {
				Seller seller = this.instantiateSeller(rs, department);
				sellers.add(seller);
			}

			return sellers;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}

	}

}
