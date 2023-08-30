FROM eclipse-temurin:17-jdk

USER root

# copy the bootstrap script
COPY bootstrap bootstrap
# make bootstrap script executable
RUN chmod +x bootstrap

# copy ssh-daemon configuration
COPY sshd_config /etc/ssh/sshd_config

#RUN apt update \
RUN apt update -y \
    && apt upgrade -y \
    && apt -y install openssh-server \
    && apt autoremove -y \
    && apt clean

# Set up SSH keys
RUN mkdir -p /root/.ssh
COPY id_rsa.pub.template /root/.ssh/authorized_keys

# Open port 2222 for SSH access
EXPOSE 80 2222

