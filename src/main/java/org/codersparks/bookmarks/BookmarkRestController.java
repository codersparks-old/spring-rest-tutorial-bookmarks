package org.codersparks.bookmarks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codersparks.bookmarks.dao.AccountRepository;
import org.codersparks.bookmarks.dao.BookmarkRepository;
import org.codersparks.bookmarks.model.Account;
import org.codersparks.bookmarks.model.Bookmark;
import org.codersparks.bookmarks.resource.BookmarkResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/{userId}/bookmarks")
public class BookmarkRestController {

	private final BookmarkRepository bookmarkRepository;

	private final AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {
		this.validateUser(userId);
		
		
		Account account = this.accountRepository.findByUsername(userId);
		Bookmark result = bookmarkRepository.save(new Bookmark(account, input.getUri(), input.getDescription()));
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(
				ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(result.getId()).toUri());
		
		return new ResponseEntity<>(null, httpHeader, HttpStatus.CREATED);
		
		
	}

	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
	public BookmarkResource readBookmark(@PathVariable String userId,
			@PathVariable Long bookmarkId) {
		this.validateUser(userId);
		return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
	}

	@RequestMapping(method = RequestMethod.GET)
	Resources<BookmarkResource> readBookmarks(@PathVariable String userId) {
		this.validateUser(userId);
		
		Collection<Bookmark> bookmarks = this.bookmarkRepository.findByAccountUsername(userId);
		
		List<BookmarkResource> resources = new ArrayList<BookmarkResource>();
		
		if(bookmarks != null && bookmarks.size() > 0)
		{
			for(Bookmark bookmark : bookmarks) {
				resources.add(new BookmarkResource(bookmark));
			}
		}
		
		return new Resources<BookmarkResource>(resources);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	ResponseEntity<?> delete(@PathVariable Long id) {
		
		Bookmark bm = bookmarkRepository.findOne(id);
		
		if(bm == null) {
			throw new BookmarkNotFoundException(Long.toString(id));
		}
		
		bookmarkRepository.delete(bm);
		
		return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
	}

	@Autowired
	BookmarkRestController(BookmarkRepository bookmarkRepository,
			AccountRepository accountRepository) {
		this.bookmarkRepository = bookmarkRepository;
		this.accountRepository = accountRepository;
	}

	private void validateUser(String userId) {
		if(this.accountRepository.findByUsername(userId) == null) {
			throw new UserNotFoundException(userId);
		}
	}
	
	
}

@ControllerAdvice
class BookmarkControllerAdvice {
	
	@ResponseBody
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	VndErrors userNotFoundExceptionHandler(UserNotFoundException ex) {
		return new VndErrors("error", ex.getMessage());
	}
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class BookmarkNotFoundException extends RuntimeException {
	
	public BookmarkNotFoundException(String id) {
		super("could not find bookmark '" + id + "'");
	}
}


