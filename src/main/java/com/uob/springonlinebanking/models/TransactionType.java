package com.uob.springonlinebanking.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class TransactionType {

	@Id
	@Column(name="type_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String type;
	
}
