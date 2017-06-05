package org.smart4j.test;

import model.Customer;
import org.junit.Test;
import service.CustomerService;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */
public class CustomerServiceTest {

    private CustomerService customerService;

    public CustomerServiceTest() {
        customerService = new CustomerService();

    }

    @Test
    public void getCustomerList() {
        List<Customer> customers = customerService.getCustomerList();
        for (Customer customer :customers) {
            System.out.println(customer.getId() + " == " + customer.getName());
        }
    }
    @Test
    public void insertCustomer(){
        customerService.in
    }
}
