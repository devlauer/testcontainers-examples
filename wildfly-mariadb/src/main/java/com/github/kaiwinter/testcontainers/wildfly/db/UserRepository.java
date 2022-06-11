package com.github.kaiwinter.testcontainers.wildfly.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.github.kaiwinter.testcontainers.wildfly.db.entity.User;

/**
 * A database repository which has test-worthy logic.
 */
public final class UserRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public User find(int id) {
		return entityManager.find(User.class, id);
	}

	public User findByUsername(String username) {
		Query query = entityManager.createQuery("SELECT u FROM User u WHERE username=?1");
		query.setParameter(1, username);
		return (User) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public Collection<User> findAll() {
		Query query = entityManager.createQuery("SELECT u FROM User u");
		return query.getResultList();
	}

	public User save(User user) {
		entityManager.persist(user);
		return user;
	}

	public void delete(User user) {
		entityManager.remove(user);
	}

	/**
	 * Resets the login count for other users than root and admin.
	 */
	public void resetLoginCountForUsers() {
		Query query = entityManager.createQuery("UPDATE User SET loginCount=0 WHERE username NOT IN ('root', 'admin')");
		query.executeUpdate();
	}
}
