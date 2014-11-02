package org.codersparks.bookmarks;

import java.util.Collection;

import org.codersparks.bookmarks.dao.AccountRepository;
import org.codersparks.bookmarks.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/account")
public class AccountRestController {

	private final AccountRepository accountRepository;

	@Autowired
	public AccountRestController(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	Collection<Account> readAccounts() {
		return this.accountRepository.findAll();
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	Account readAccount(@PathVariable String userId) {
		System.out.println("Read Account called: userId: " + userId);
		this.validateUser(userId);
		return this.accountRepository.findByUsername(userId);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	ResponseEntity<?> add(@RequestBody Account input) {
		
		Account account = new Account(input.getUsername(), input.getPassword());
		
		accountRepository.save(account);
		
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(
				ServletUriComponentsBuilder.fromCurrentRequest().path("/{userId}")
					.buildAndExpand(account.getUsername()).toUri()
				);
		
		return new ResponseEntity<>(null, httpHeader, HttpStatus.CREATED);
	}

	private void validateUser(String userId) {
		if (this.accountRepository.findByUsername(userId) == null) {
			throw new UserNotFoundException(userId);
		}
	}
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String userId) {
		super("could not find user '" + userId + "'.");
	}
}

