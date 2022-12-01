package com.uob.springonlinebanking.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.RoleRepository;
import com.uob.springonlinebanking.repositories.UserRepository;

@Controller
public class UserController {

	@Autowired
	UserRepository userRepo;
	@Autowired
	RoleRepository roleRepo;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/")
	public String showMain() {
		return "index";
	}

	// ============================================= Register Page
	@GetMapping("/register") // used in index.html
	public String showRegistrationForm(HttpServletRequest request, Model model) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			model.addAttribute("msg", (String) flashMap.get("msg"));
			model.addAttribute("userNric", (String) flashMap.get("userNric"));
			model.addAttribute("contactNo", (String) flashMap.get("contactNo"));
			model.addAttribute("address", (String) flashMap.get("address"));
			model.addAttribute("email", (String) flashMap.get("email"));
			model.addAttribute("nomineeName", (String) flashMap.get("nomineeName"));
			model.addAttribute("nomineeNric", (String) flashMap.get("nomineeNric"));
		}
		return "addUser"; // render addUser.html
	}

	@PostMapping("/process_register") // used in addUser.html
	public String processRegister(Users user, RedirectAttributes redirectAttributes) {
		if (userRepo.getUserByUsername(user.getUserName()) != null) {
			redirectAttributes.addFlashAttribute("msg", "userNameExist");
			redirectAttributes.addFlashAttribute("userNric", user.getUserNric());
			redirectAttributes.addFlashAttribute("contactNo", user.getContactNo());
			redirectAttributes.addFlashAttribute("address", user.getAddress());
			redirectAttributes.addFlashAttribute("email", user.getEmail());
			redirectAttributes.addFlashAttribute("nomineeName", user.getNomineeName());
			redirectAttributes.addFlashAttribute("nomineeNric", user.getNomineeNric());
			return "redirect:/register";
		} else {
			if (!StringUtils.isEmpty(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			user.setRolesCollection(Arrays.asList(roleRepo.findRoleByRoleName("ROLE_USER")));
			userRepo.save(user); // save to user repository
//			Users userLocal = userRepo.getUserByUserId(user.getUserId()); // get the user that has been just saved
//			double interestRate; // set interest rate
//			if (accountType.equalsIgnoreCase("Savings")) {
//				interestRate = 0.05;
//			} else if (accountType.equalsIgnoreCase("Fixed Deposit")) {
//				interestRate = 0.10;
//			} else {
//				interestRate = 0.15;
//			}
//			Accounts account = new Accounts(accountType, 0.0, userLocal, false, interestRate, LocalDate.now()); // create																						// account
//			accountRepo.save(account); // save to account repository
			return "redirect:/";
		}
	}

	// ============================================= Welcome User Page
	@GetMapping("/welcomeuser") // used in components.html navbar
	public String welcomeUser(HttpServletRequest request, Model model) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			String txnType = (String) flashMap.get("msg");
			if (txnType.equals("deposit")) {
				model.addAttribute("balAfterDeposit", (Double) flashMap.get("balAfterDeposit"));
			} else if (txnType.equals("withdraw")) {
				model.addAttribute("balAfterWithdrawal", (Double) flashMap.get("balAfterWithdrawal"));
			}
			model.addAttribute("txnAmt", (Double) flashMap.get("txnAmt"));
			model.addAttribute("accountNo", (Long) flashMap.get("accountNo"));
			model.addAttribute("msg", txnType);
		}
		return "welcomeUser"; // render welcomeUser.html
	}

	// ============================================= Edit user record by id

	@GetMapping("/admin/editrecord/{id}") // render the editUser.html based on record id, used in viewUser.html
	public String editRecord(@PathVariable("id") Long id, Model model) {
		Optional<Users> user = userRepo.findById(id);
		model.addAttribute("user", user);
		return "editUser";
	}

	@PutMapping("/admin/process_edit") // Update user record, used in editUser.html
	public String saveData(@Valid Users user) {
		if (!StringUtils.isEmpty(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		userRepo.save(user);
		return "redirect:/viewUser"; // after update redirect to show record which is in the index.html
	}

	// ============================================= Delete user record by id
	@DeleteMapping("/admin/deleterecord/{id}") // delete record by id, used in viewUser.html
	public String deleteRecord(@PathVariable("id") Long id) {

		userRepo.deleteById(id);
		return "redirect:/admin/viewuser"; // once record is deleted redirect to index.html to show the final records
	}

	// ============================================= View all user records
	@GetMapping("/admin/viewuser") // used in components.html navbar
	public String showUserList(Users user, Model model, HttpServletRequest request) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap == null) {
			List<Users> userList = (List<Users>) userRepo.findAll();
			model.addAttribute("userList", userList);	
		} else {
			model.addAttribute("count", (Integer) flashMap.get("count"));
			model.addAttribute("userList", (List<Users>) flashMap.get("searchedUserList"));	
		}
		return "viewUser";
	}

	// ============================================= Search for user records by username
	@PostMapping("/admin/process_search") // used in viewUser.html
	public String showSearchedUserList(@RequestParam("searchString") String searchString, Users user, Model model,
			RedirectAttributes redirectAttributes) {
		System.out.println(searchString);
		List<Users> searchedUserList = (List<Users>) userRepo.getSearchedUserByUsername(searchString);
		Integer count = searchedUserList.size();
		redirectAttributes.addFlashAttribute("count", count);
		redirectAttributes.addFlashAttribute("searchedUserList", searchedUserList);
		return "redirect:/admin/viewuser";
	}
}
