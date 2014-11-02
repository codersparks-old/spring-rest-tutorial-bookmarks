package org.codersparks.bookmarks.dao;

import java.util.Collection;

import org.codersparks.bookmarks.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	Collection<Bookmark> findByAccountUsername(String username);
}
