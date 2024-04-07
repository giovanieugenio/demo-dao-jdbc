package model.dao;

import db.DB;
import db.DBException;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao{
    private Connection conn;
    public SellerDaoJDBC(Connection conn){
        this.conn = conn;
    }
    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "INSERT INTO seller "
                    + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                    + "VALUES "
                    + "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getSalary());
            st.setInt(5, obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DBException("Error: No rows affected");
            }
        } catch (SQLException e){
            throw new DBException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }
    @Override
    public void update(Seller obj) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "UPDATE seller "
                            + "(SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?) "
                            + "WHERE Id = ?");

            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getSalary());
            st.setInt(5, obj.getDepartment().getId());
            st.setInt(6, obj.getId());

        } catch (SQLException e){
            throw new DBException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }
    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e){
            throw new DBException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }
    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.DepartmentId = department.Id "
                    + "WHERE seller.Id = ?");

            st.setInt(1 ,id);
            rs = st.executeQuery();
            if (rs.next()){
                Department dp = instanciateDepartment(rs);
                Seller obj = instanciateSeller(rs, dp);
                obj.setDepartment(dp);
                return obj;
            }
            return null;
        } catch (SQLException e){
            throw new DBException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "ORDER BY Name");

            rs = st.executeQuery();

            List<Seller> sellerList = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){
                Department dpMap = map.get(rs.getInt("DepartmentId"));

                if (dpMap == null){
                    dpMap = instanciateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dpMap);
                }

                Seller obj = instanciateSeller(rs, dpMap);
                sellerList.add(obj);
            }
            return sellerList;
        } catch (SQLException e){
            throw new DBException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department dp) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE DepartmentId = ? "
                            + "ORDER BY Name");

            st.setInt(1 ,dp.getId());
            rs = st.executeQuery();

            List<Seller> sellerList = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){
                Department dpMap = map.get(rs.getInt("DepartmentId"));

                if (dpMap == null){
                    dpMap = instanciateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dpMap);
                }

                Seller obj = instanciateSeller(rs, dpMap);
                sellerList.add(obj);
            }
            return sellerList;
        } catch (SQLException e){
            throw new DBException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Department instanciateDepartment(ResultSet rs) throws SQLException {
        Department dp = new Department();
        dp.setId(rs.getInt("DepartmentId"));
        dp.setName(rs.getString("DepName"));
        return dp;
    }
    private Seller instanciateSeller(ResultSet rs, Department dp) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setSalary(rs.getDouble("BaseSalary"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        return obj;
    }
}
