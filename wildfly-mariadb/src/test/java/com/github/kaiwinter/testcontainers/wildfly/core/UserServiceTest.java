package com.github.kaiwinter.testcontainers.wildfly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.kaiwinter.testcontainers.wildfly.db.UserRepository;
import com.github.kaiwinter.testcontainers.wildfly.db.UserRepositoryTest;
import com.github.kaiwinter.testcontainers.wildfly.db.entity.User;
import com.github.kaiwinter.testsupport.db.DockerDatabaseTestUtil;

/**
 * Tests for a service layer which depends on the database.
 * 
 * @see {@link UserRepositoryTest}.
 */
@ExtendWith(ArquillianExtension.class)
public final class UserServiceTest {

	@Inject
	private UserService userService;

	@PersistenceContext
	private EntityManager entityManager;

	@Deployment
	public static WebArchive createDeployment() {
		File[] pomDependencies = Maven.resolver() //
				.loadPomFromFile("pom.xml").importDependencies(ScopeType.TEST) //
				.resolve().withTransitivity().asFile();

		WebArchive war = ShrinkWrap.create(WebArchive.class,"mytestapp.war") //
				.addClasses(UserService.class, UserRepository.class, User.class) //
				.addClasses(UserServiceTest.class, DockerDatabaseTestUtil.class) //
				.addAsResource("META-INF/persistence.xml") //
				.addAsResource("testdata.xml") //
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(pomDependencies);
//		war.as(ZipExporter.class).exportTo(new File("mytestapp.war"),true);
		return war;
	}

	@Test
	void testSumOfLogins() {
		assertNotNull(entityManager);
		DockerDatabaseTestUtil.insertDbUnitTestdata(entityManager, getClass().getResourceAsStream("/testdata.xml"));
		int sumOfLogins = userService.calculateSumOfLogins();
		assertEquals(9, sumOfLogins);
	}
}
