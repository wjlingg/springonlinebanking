package com.uob.springonlinebanking.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Transactions {
	
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private long transactionId;
    
    @ManyToMany
    @JoinColumn(name = "account_transact")
}
