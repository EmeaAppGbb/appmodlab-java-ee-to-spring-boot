FROM quay.io/wildfly/wildfly:26.1.3.Final

USER root

# Add PostgreSQL JDBC driver
RUN curl -o /opt/jboss/wildfly/standalone/deployments/postgresql-42.5.4.jar \
    https://jdbc.postgresql.org/download/postgresql-42.5.4.jar

# Copy standalone configuration
COPY --chown=jboss:jboss standalone.xml /opt/jboss/wildfly/standalone/configuration/

# Create admin user
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin123 --silent

USER jboss

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
