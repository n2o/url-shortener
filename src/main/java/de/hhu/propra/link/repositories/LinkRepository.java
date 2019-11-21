package de.hhu.propra.link.repositories;

import de.hhu.propra.link.entities.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends CrudRepository<Link, String> {
}