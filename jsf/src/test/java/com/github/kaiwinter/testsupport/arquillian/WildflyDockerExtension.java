package com.github.kaiwinter.testsupport.arquillian;

import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.config.descriptor.api.ProtocolDef;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.ContainerRegistry;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import com.github.kaiwinter.testsupport.arquillian.WildflyArquillianRemoteConfiguration.ContainerConfiguration;
import com.github.kaiwinter.testsupport.arquillian.WildflyArquillianRemoteConfiguration.ContainerConfiguration.ServletProtocolDefinition;
import com.github.kaiwinter.user.view.testcontainers.TestcontainersHelper;

/**
 * Starts a docker container and configures Arquillian to use Wildfly in the
 * docker container.
 */
public final class WildflyDockerExtension implements LoadableExtension {

	/** The base URL of Wildfly in the Docker container. */
	private static String baseUrl;
	private static String networkBaseUrl;

	@Override
	public void register(ExtensionBuilder builder) {
		builder.observer(LoadContainerConfiguration.class);
	}

	/**
	 * Helper class to register an Arquillian observer.
	 */
	public static final class LoadContainerConfiguration {

		private static final int WILDFLY_HTTP_PORT = 8080;
		private static final int WILDFLY_MANAGEMENT_PORT = 9990;

		/**
		 * Method which observes {@link ContainerRegistry}. Gets called by Arquillian at
		 * startup time.
		 *
		 * @param registry      contains containers defined in arquillian.xml
		 * @param serviceLoader
		 */
		public void registerInstance(@Observes ContainerRegistry registry, ServiceLoader serviceLoader) {
			@SuppressWarnings("resource")
			GenericContainer<?> dockerContainer = new GenericContainer<>(
					new ImageFromDockerfile()
							.withDockerfileFromBuilder(builder -> builder.from("quay.io/wildfly/wildfly:25.0.0.Final")
									.user("jboss").run("/opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent")
									.cmd("/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement",
											"0.0.0.0")
									.build()))
					.withExposedPorts(WILDFLY_MANAGEMENT_PORT, WILDFLY_HTTP_PORT)
					.withNetwork(TestcontainersHelper.network).withNetworkAliases("appserver");
			dockerContainer.start();

			configureArquillianForRemoteWildfly(dockerContainer, registry);
		}

		private void configureArquillianForRemoteWildfly(GenericContainer<?> dockerContainer,
				ContainerRegistry registry) {
			Integer wildflyHttpPort = dockerContainer.getMappedPort(WILDFLY_HTTP_PORT);
			Integer wildflyManagementPort = dockerContainer.getMappedPort(WILDFLY_MANAGEMENT_PORT);

			String containerIpAddress = dockerContainer.getHost();
			Container arquillianContainer = registry.getContainers().iterator().next();
			ContainerDef containerConfiguration = arquillianContainer.getContainerConfiguration();
			containerConfiguration.property(ContainerConfiguration.MANAGEMENT_ADDRESS_KEY, containerIpAddress);
			containerConfiguration.property(ContainerConfiguration.MANAGEMENT_PORT_KEY,
					String.valueOf(wildflyManagementPort));
			containerConfiguration.property(ContainerConfiguration.USERNAME_KEY, "admin");
			containerConfiguration.property(ContainerConfiguration.PASSWORD_KEY, "Admin#70365");

			ProtocolDef protocolConfiguration = arquillianContainer
					.getProtocolConfiguration(new ProtocolDescription(ServletProtocolDefinition.NAME));
			protocolConfiguration.property(ServletProtocolDefinition.HOST_KEY, containerIpAddress);
			protocolConfiguration.property(ServletProtocolDefinition.PORT_KEY, String.valueOf(wildflyHttpPort));

			WildflyDockerExtension.baseUrl = "http://" + containerIpAddress + ":" + wildflyHttpPort
					+ "/testcontainers-jsf/";
			WildflyDockerExtension.networkBaseUrl = "http://appserver:8080/testcontainers-jsf/";
		}

	}

	public static String getBaseUrl() {
		return baseUrl;
	}

	public static String getNetworkBaseUrl() {
		return networkBaseUrl;
	}

}