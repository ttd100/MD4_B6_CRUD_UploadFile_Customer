package rikkei.academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import rikkei.academy.model.Customer;
import rikkei.academy.model.CustomerForm;
import rikkei.academy.service.customer.ICustomerService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private ICustomerService customerService;

    @GetMapping("/create-customer")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/customer/create");
        modelAndView.addObject("customer", new CustomerForm());
        return modelAndView;
    }
    @Value("${file-upload}")
    private String fileUpload;
    @PostMapping("/create-customer")
    public ModelAndView saveCustomer(@ModelAttribute CustomerForm customerForm){
        MultipartFile multipartFile = customerForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(customerForm.getImage().getBytes(),new File(fileUpload+fileName));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Customer customer = new Customer(customerForm.getId(),customerForm.getFirstName(),customerForm.getLastName(),fileName);
        customerService.save(customer);
        ModelAndView modelAndView = new ModelAndView("/customer/create");
        modelAndView.addObject("customer",customerForm);
        modelAndView.addObject("message","Create new product successfully !");
        return modelAndView;
    }




    @GetMapping("/detail-customer/{id}")
    public ModelAndView showDetailForm(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if(customer != null) {
            ModelAndView modelAndView = new ModelAndView("/customer/detail");
            modelAndView.addObject("customersForm", customer);
            return modelAndView;
        }else {
            ModelAndView modelAndView = new ModelAndView("/customer/error404");
            return modelAndView;
        }
    }

    @GetMapping("/edit-customer/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            ModelAndView modelAndView = new ModelAndView("/customer/edit");
            CustomerForm customerForm = new CustomerForm(customer.getId(),customer.getFirstName(),customer.getLastName(),null);
            modelAndView.addObject("customerForm", customerForm);
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/customer/error404");
            return modelAndView;
        }
    }

    @PostMapping("/edit-customer")
    public ModelAndView updateCustomer(CustomerForm customerForm) {
        Customer customer = customerService.findById(customerForm.getId());
        String oldAvatar = customer.getAvatar();
        MultipartFile multipartFile = customerForm.getImage();
        String nameNewAvatar = multipartFile.getOriginalFilename();
        if (customerForm.getImage().isEmpty()){
            customer.setAvatar(oldAvatar);
        }else {
            customer.setAvatar(nameNewAvatar);
        }
        customerService.save(customer);
        ModelAndView modelAndView = new ModelAndView("/customer/edit");
        modelAndView.addObject("customerForm", customerForm);
        modelAndView.addObject("message", "Customer updated successfully");
        return modelAndView;
    }

    @GetMapping("/delete-customer/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            ModelAndView modelAndView = new ModelAndView("/customer/delete");
            modelAndView.addObject("customer", customer);
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/customer/error404");
            return modelAndView;
        }
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@ModelAttribute("customer") Customer customer) {
        customerService.remove(customer.getId());
        return "redirect:customers";
    }
    @GetMapping("/search")
    public String search(Model model, @RequestParam String search){
        List<Customer> customerList = customerService.search(search);
        model.addAttribute("formList",customerList);
        model.addAttribute("search",search);
        return "/customer/list";
    }
    @GetMapping(value = {"/", "/customers"})
    public String listCustomers(Model model) {
        model.addAttribute("formList",customerService.findAll());
        return "/customer/list";
    }
}
