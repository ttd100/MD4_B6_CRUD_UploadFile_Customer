package rikkei.academy.service.customer;

import rikkei.academy.model.Customer;
import rikkei.academy.service.IGenericService;

import java.util.List;

public interface ICustomerService  extends IGenericService<Customer> {
    List<Customer>search(String search);
}
