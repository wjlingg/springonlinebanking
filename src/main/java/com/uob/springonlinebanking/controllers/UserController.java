package com.uob.springonlinebanking.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.UserRepository;

@Controller
public class UserController {
	
	@Autowired
	UserRepository userRepo;

	@GetMapping("/editrecord/{id}") // render the editUser.html based on record id
	public String editRecord(@PathVariable("id") Long id, Model model) {
		Optional<Users> user = userRepo.findById(id);
		model.addAttribute("user", user);
		return "editUser";
	}
	
	@PutMapping("/process_edit") // Update user record
	public String saveData(@Valid Users user) {
		userRepo.save(user);
		return "redirect:/"; // after update redirect to show record which is in the index.html
	}
}