//Manual creation of resources in AWS console everytime we login is a nightmare

provider "aws" 
{
    region = "ap-south-1"
} //This is the block that tells us the resource provider

resource "aws_key_pair" "deployer" { //Key Definition using which we can login to the server
  key_name   = "deployer-key"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDiZixIH+Iu/N3ssVdo3D51p+kEPKYU/efq8/9HG9ru2RRQrMARMjQ8w6zHkRfAv9Yu42Vo8lUQiFPyeV656WFT7Pdf/2+JLo/aB68/OMy2M2x6OFt4wRMlQnlkLSKGiW2bAXDBLM8dfSk4TgxIjNi1JyNE37zyFtVhY4pBFgslEQZi9rkiQfANP/O8wrYtTgYRZnowXjvUrvIjPkHZazYJe1euV4SYLN5IsA8LzsDfrabrQ/wl5NhfPM95aEALj8MzlIlHsuL2xeiC0O8AifTTWFYrlw6GMa53CpcRM16VnPD/lmw8GwXp7+PD7OODjq2EYc+vanr0N2MOui/j7Cnepeq6lY9N/ePxtB7ZflsV1Bll8BEr4BhqCfXOEYt5h3+unq6twaMg4YG8u3GK2ipnH2iFRnfv5r3Xwb85QzgkoW7YsP0baYr35d1+pq3n12QviJJR9HWLqrIFOl9HOwa2wYRpdsZKpGlS/9Rzz6boBY7lvUTKKFESPPPM22BV3Vk="
} //This is the password to login to the server

resource "aws_security_group" "allow_web" //second string is the name of the resource
//This resource allows communication with server, and server communication with the outside world(Internet)
//Imagine the server is a wall, we define holes(Port) to get inside and outside the wall
{
    name = "allow_web_traffic" //Name AWS sees in the console (GUI)
    description = "Allow web traffic" //Description of the resource

    ingress //Means incoming traffic (Block of rules for who can enter)
    {
        //Start and end of port range
        from_port = 22 //Standard port for SSH
        to_port = 22 //Implies only 1 port
        
        protocol = "tcp" //Protocol used by SSH
        cidr_blocks = ["0.0.0.0/0"] //Allow entire internet. Note that in a real job you would 
        //put your office IP address
    }

    ingress
    {
        from_port = 8080
        to_port = 8080

        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    egress //Means outgoing traffic (Block of rules for who can leave)
    {
        from_port = 0
        to_port = 0 //This means all ports

        protocol = "-1" //All Protocols
        cidr_blocks = ["0.0.0.0/0"] //Allow entire internet
    }
}

resource "aws_instance" "app_server" //The Server itself
{
    ami = "ami-0b424b262976e551a" //Every computer needs an OS. This AMI ID is specific to Mumbai
    instance_type = "t2.micro" //The size. We use this as its free for 12 months 
    //The Key (for logging in) is attached so that we can SSH into the server
    key_name = aws_key_pair.deployer.key_name
    # 4. The Firewall (Security Group)
    # Why: We attach the "holes" we defined so traffic can get in.
    vpc_security_group_ids = [aws_security_group.allow_web.id]
    //Automation Script (User Data)
    //This runs ONCE when the server starts. It installs Java for us.
    //Without this, we would have an empty Ubuntu server and would have to 
    //manually run these commands.
  user_data = <<-EOF # This is a heredoc. It allows us to write multi-line strings
              #!/bin/bash
              # This is a shebang. It tells the server to run this as a bash script
              sudo apt-get update 
              # Update the package list
              sudo apt-get install -y openjdk-17-jdk 
              # Install Java
              EOF
}

