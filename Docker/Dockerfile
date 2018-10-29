FROM ubuntu:17.10

RUN apt-get clean && apt-get update && apt-get install -y locales
RUN locale-gen en_US.UTF-8
RUN apt-get install -y build-essential automake autoconf libtool && \
    apt-get install -y libicu-dev libboost-regex-dev libboost-system-dev \
                       libboost-program-options-dev libboost-thread-dev \
                       zlib1g-dev


# Install packages necessary to run EAP
RUN apt-get -y install xmlstarlet bsdtar unzip curl

# Create a user and group used to launch processes
# The user ID 1000 is the default for the first "regular" user on Fedora/RHEL,
# so there is a high chance that this ID will be equal to the current user
# making it easier to use volumes (no permission issues)
RUN groupadd -r jboss -g 1000 && useradd -u 1000 -r -g jboss -m -d /opt/jboss -s /sbin/nologin -c "JBoss user" jboss

# Set the working directory to jboss' user home directory
WORKDIR /opt/jboss

# User root user to install software
USER root

# Install necessary packages
RUN apt-get -y install openjdk-8-jdk

# Switch back to jboss user
USER jboss

# Set the JAVA_HOME variable to make it clear where Java is located
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 12.0.0.Final

# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME && curl -O http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.zip && unzip wildfly-$WILDFLY_VERSION.zip && mv $HOME/wildfly-$WILDFLY_VERSION $HOME/wildfly && rm wildfly-$WILDFLY_VERSION.zip

# Set the JBOSS_HOME env variable
ENV JBOSS_HOME /opt/jboss/wildfly

#Tesis 

ADD tesis-backend.war /opt/jboss/wildfly/standalone/deployments/
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/
ADD src /opt/jboss/src
ADD modelos /opt/jboss/modelos
ADD models /opt/jboss/models
ADD libfreeling.so /opt/jboss/

ADD libfreeling-4.0.so /usr/lib/
ADD libfoma-0.9.18.so /usr/lib/
ADD libfoma.so /usr/lib/
ADD libtreeler-0.4.so /usr/lib/
ADD libtreeler.so /usr/lib/
ENV LD_LIBRARY_PATH=/usr/lib/

RUN mkdir /opt/jboss/temp
VOLUME [ "/opt/jboss/temp" ]

# Expose the ports we're interested in
EXPOSE 8080 9990

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]