FROM mcr.microsoft.com/openjdk/jdk:17-mariner

USER root

# copy the bootstrap script
COPY bootstrap bootstrap
# make bootstrap script executable
RUN chmod +x bootstrap

# copy ssh-daemon configuration
COPY sshd_config /etc/ssh/sshd_config

#RUN apk update \
RUN yum update -y \
    && yum clean all

# Install OpenSSH and set the password for root to "Docker!".
RUN yum -y install openssh \
     && echo "root:Docker!" | chpasswd

# Open port 2222 for SSH access
EXPOSE 80 2222

