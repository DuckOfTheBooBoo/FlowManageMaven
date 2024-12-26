# Step 1: Use an official Maven image to build the project
FROM maven:latest AS builder

# Set the working directory in the container
WORKDIR /app

# Clone the repository
ARG REPO_URL
RUN git clone $REPO_URL .

# Build the project with Maven
RUN mvn clean package -DskipTests

FROM payara/server-full:5.193.1

# Set the working directory in the container
ENV DEPLOY_DIR /opt/payara/deployments
ENV ADMIN_PASSWORD admin
ENV ADMIN_USER admin

RUN echo "AS_ADMIN_PASSWORD=$ADMIN_PASSWORD" > /opt/payara/password.txt

COPY ./startup-script.sh /opt/payara/scripts/

# Expose the desired port
EXPOSE 8080

# Copy the WAR file from the Maven build stage to GlassFish autodeploy directory
COPY --from=builder /app/target/FlowManageMaven-1.0-SNAPSHOT.war $DEPLOY_DIR/FlowManageMaven.war

# Run Payara server on port 8081
CMD ["/opt/payara/scripts/startup-script.sh"]