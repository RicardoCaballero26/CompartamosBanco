package com.compartamos.entites;




import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

@Entity 
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String accountNumber;
    private String names;
    private String surname;
    private String phone;
    private String address;
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Product> products;

}
