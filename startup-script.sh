#!/bin/bash

# Start the Payara domain
echo "Starting Payara domain..."
asadmin start-domain

# Wait for the domain to be fully started
echo "Waiting for Payara to be ready..."
until asadmin list-domains | grep "production running"; do
    printf '.'
    sleep 5
done
echo "Payara is ready."

# Deploy the WAR file using asadmin
echo "Deploying the application..."
asadmin --user=admin --passwordfile=/opt/payara/password.txt deploy --contextroot / /opt/payara/deployments/FlowManageMaven.war

# Keep the container running (optional if Payara is in foreground mode)
tail -f /opt/payara/appserver/glassfish/domains/production/logs/server.log
