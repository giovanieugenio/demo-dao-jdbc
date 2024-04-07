package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("TEST 1 : findById");
        Seller seller = sellerDao.findById(3);
        System.out.println(seller);

        System.out.println("TEST 2: findByDepartment");
        Department dp = new Department(2, null);
        List<Seller> list = sellerDao.findByDepartment(dp);

        for (Seller obj : list){
            System.out.println(obj);
        }

        System.out.println("TEST 3: findAll");
        list = sellerDao.findAll();

        for (Seller obj : list){
            System.out.println(obj);
        }

        System.out.println("TEST 4: insert");
        Seller seller1 = new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.0, dp);
        sellerDao.insert(seller1);
        System.out.println("Inserted. Id = " + seller1.getId());

        System.out.println("TEST 5: update");
        seller1 = sellerDao.findById(1);
        seller1.setName("Martha");
        sellerDao.update(seller1);
        System.out.println("Update Completed");

        System.out.println("TEST 6: deleteById");
        System.out.print("Enter id for delete seller: ");
        int id = sc.nextInt();
        sellerDao.deleteById(id);
        System.out.println("Delete completed");
        sc.close();
    }
}
