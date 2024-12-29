FROM payara/server-full:5.193.1

# Set the working directory in the container
ENV DEPLOY_DIR /opt/payara/deployments
ENV ADMIN_PASSWORD admin
ENV ADMIN_USER admin

RUN echo "AS_ADMIN_PASSWORD=$ADMIN_PASSWORD" > /opt/payara/password.txt

COPY ./startup-script.sh /opt/payara/scripts/startup-script.sh

RUN sed -i 's/\r//' /opt/payara/scripts/startup-script.sh

# Expose the desired port
EXPOSE 8100

# Copy the WAR file from the Maven build stage to GlassFish autodeploy directory
COPY ./target/FlowManageMaven-1.war $DEPLOY_DIR/FlowManageMaven.war

# Run Payara server on port 8080
CMD ["bash", "/opt/payara/scripts/startup-script.sh"]
