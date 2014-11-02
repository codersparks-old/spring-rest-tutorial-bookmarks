package org.codersparks.bookmarks;

import java.util.Arrays;
import java.util.List;

import org.codersparks.bookmarks.dao.AccountRepository;
import org.codersparks.bookmarks.dao.BookmarkRepository;
import org.codersparks.bookmarks.model.Account;
import org.codersparks.bookmarks.model.Bookmark;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
	
	@Bean
	CommandLineRunner init(final AccountRepository accountRepository, final BookmarkRepository bookmarkRepository) {
		return new CommandLineRunner() {
			
			
			
			@Override
			public void run(String... arg0) throws Exception {
				
				List<String> usernames = Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","));
				
				for(String name : usernames) {
					Account account = accountRepository.save(new Account(name, name));
					bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + name, "A description by " + name));
					bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + name, "2nd description by " + name));
					
				}
			}
		};
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
