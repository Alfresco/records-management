### AGS community repo image
FROM alfresco/alfresco-governance-repository-community:latest

### Install the jRebel agent
RUN yum install -y unzip
RUN curl http://dl.zeroturnaround.com/jrebel-stable-nosetup.zip --output /usr/local/jrebel-stable-nosetup.zip
RUN unzip /usr/local/jrebel-stable-nosetup.zip

# This is needed to avoid "access denied" issues with AccessControlException.
RUN echo -e "grant {\n    permission java.security.AllPermission;\n};" >> /usr/local/tomcat/conf/catalina.policy
