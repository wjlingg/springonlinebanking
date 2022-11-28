package com.uob.springonlinebanking.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.UserRepository;

@Controller
public class UserController {
	
	@Autowired
	UserRepository userRepo;
	
	// ============================================= Edit user record by id

	@GetMapping("/editrecord/{id}") // render the editUser.html based on record id, used in viewUser.html
	public String editRecord(@PathVariable("id") Long id, Model model) {
		Optional<Users> user = userRepo.findById(id);
		model.addAttribute("user", user);
		return "editUser";
	}
	
	@PutMapping("/process_edit") // Update user record, used in editUser.html
	public String saveData(@Valid Users user) {
		userRepo.save(user);
		return "redirect:/viewUser"; // after update redirect to show record which is in the index.html
	}
	
	// ============================================= Delete user record by id
	@DeleteMapping("/deleterecord/{id}") // delete record by id, used in viewUser.html
	public String deleteRecord(@PathVariable("id") Long id){

		userRepo.deleteById(id);
		return "redirect:/viewuser"; // once record is deleted redirect to index.html to show the final records
	}
	
	// ============================================= View all user records
	@GetMapping("/admin/viewuser") // used in components.html navbar
	public String showUserList(Users user, Model model) {
		List<Users> userList = (List<Users>) userRepo.findAll();
		model.addAttribute("userList",userList);
		return "viewUser";
	}
}
