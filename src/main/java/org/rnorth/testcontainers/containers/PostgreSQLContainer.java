package org.rnorth.testcontainers.containers;

import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;

/**
 * @author richardnorth
 */
public class PostgreSQLContainer extends AbstractContainer implements DatabaseContainer {

    private static final String IMAGE = "postgres";
    private final String tag;
    private String postgresPort;

    public PostgreSQLContainer() {
        this(null);
    }

    public PostgreSQLContainer(String tag) {
        if (tag == null) {
            this.tag = "latest";
        } else {
            this.tag = tag;
        }
    }

    @Override
    protected void containerIsStarting(ContainerInfo containerInfo) {
        postgresPort = containerInfo.networkSettings().ports().get("5432/tcp").get(0).hostPort();
    }

    @Override
    protected String getLivenessCheckPort() {
        return postgresPort;
    }

    @Override
    protected ContainerConfig getContainerConfig() {
        return ContainerConfig.builder()
                    .image(getDockerImageName())
                    .exposedPorts("5432")
                    .env("POSTGRES_DATABASE=test", "POSTGRES_USER=test", "POSTGRES_PASSWORD=test")
                    .cmd("postgres")
                    .build();
    }

    @Override
    protected String getDockerImageName() {
        return IMAGE + ":" + tag;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:postgresql://" + dockerHostIpAddress + ":" + postgresPort + "/test";
    }

}