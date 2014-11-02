package org.codersparks.bookmarks.dao;

import org.codersparks.bookmarks.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{

	Account findByUsername(String username);
}
