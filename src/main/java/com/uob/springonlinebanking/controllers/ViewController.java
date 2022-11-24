package com.uob.springonlinebanking.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.UserRepository;


@Controller
public class ViewController {

	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/viewuser")
	public String showUserList(Users user, Model model) {
		List<Users> userList = (List<Users>) userRepo.findAll();
		model.addAttribute("userList",userList);
		return "viewUser";
	
	}
}
