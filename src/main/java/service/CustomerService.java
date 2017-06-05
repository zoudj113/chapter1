package service;

import helper.DatabaseHelper;
import model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */
public class CustomerService {
    private Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);


    public List<Customer> getCustomerList() {
        String sql = "select * from customer";
        List<Customer> customers =  DatabaseHelper.queryEntityList(Customer.class,sql,null);
        return customers;
    }

    public Customer getCustomerById(long id) {
        return null;
    }

    public boolean createCustomer(Customer customer) {
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        return false;
    }

    public boolean deleteCustomer(long id) {
        return false;
    }

    public int insertCustomer(){
        return 0;
    }
}
