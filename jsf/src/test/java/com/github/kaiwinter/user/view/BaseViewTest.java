package com.github.kaiwinter.user.view;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import com.github.kaiwinter.user.User;
import com.github.kaiwinter.user.service.UserService;

abstract class BaseViewTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive war = ShrinkWrap.create(WebArchive.class) //
				.addClasses(UserView.class, UserService.class, User.class) //
				.addAsWebResource(new File("src/main/webapp/users.xhtml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml")) //
				.addAsLibraries(Maven.resolver() //
						.loadPomFromFile("pom.xml") //
						.importCompileAndRuntimeDependencies() //
						.resolve().withoutTransitivity().asFile());

		return war;
	}

}
