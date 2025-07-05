![image](https://github.com/user-attachments/assets/1c5a6d42-857c-434c-b66b-76134b964179)

# Immutable Infra Rollout for Blue Green
| Created        | Last updated      | Version         | author|  Internal Reviewer | L0 | L1 | L2|
|----------------|----------------|-----------------|-----------------|-----|------|----|----|
| 2025-07-05  | 2025-07-05   |     Version 1         |  Mohamed Tharik |Priyanshu|Khushi|Mukul Joshi |Piyush Upadhyay|

## Table of Contents

## Introduction
This document explains how to implement **Blue-Green Deployment using Terraform** with an **Immutable Infrastructure** approach. It describes how two separate environments (blue and green) are used to deploy and switch application versions safely without downtime. Terraform automates the provisioning, switching, and removal of infrastructure components.

## Overview of Blue-Green Deployment
Blue-Green Deployment strategy involves maintaining **two identical environments**:
- **Blue** – The currently running production environment.
- **Green** – The new version of the application deployed separately.

Once the green environment is tested and verified, the **traffic is switched** from blue to green via **ALB (Application Load Balancer)** or **Route53**. The **old (blue) environment can then be terminated**, ensuring **zero-downtime** and full rollback capability.

## Prerequisites
| **Category**        | **Requirement**                                                                  |
| ------------------- | -------------------------------------------------------------------------------- |
| **AWS Access**      | Active AWS account with permissions for EC2, ALB, ASG, IAM, VPC                  |
| **Terraform**       | Installed (v1.x or above), configured with AWS credentials                       |
| **Pre-built AMI**   | AMI available for deploying application immutably (e.g., built via Packer)       |
| **VPC Setup**       | VPC, subnets, security groups, and internet/NAT gateway configured               |
| **CI/CD Tool**      | Optional: Jenkins, GitHub Actions, etc. for automation                           |
| **Terraform Code**  | Modules/files: `main.tf`, `variables.tf`, `outputs.tf`, etc. structured properly |
| **Basic Knowledge** | Understanding of Terraform, AWS, Blue-Green Deployment, and Immutable Infra      |
| **Version Control** | Git repository for managing and tracking Terraform code                          |

## Flow Diagram of Infrastructure Rollout for Blue Green 
```mermaid
graph TD
    A[Developer Pushes Code] --> B[CI/CD Triggers Terraform Plan]
    B --> C[Provision Green Infra via Launch Template]
    C --> D[Attach Green Instances to New Target Group]
    D --> E[Test Green Infra via ALB Test Listener]
    E --> F{Green Infra Verified?}
    F -- Yes --> G[Switch ALB Listener to Green Target Group]
    G --> H[Remove Blue Instances]
    F -- No --> I[Abort and Keep Blue Infra]
```
## Terraform Implementation Strategy
### Key Terraform Components:
| Resource                           | Purpose                                                                  |
| ---------------------------------- | ------------------------------------------------------------------------ |
| `aws_launch_template`              | Defines immutable server config for app (AMI, userdata)                  |
| `aws_autoscaling_group`            | Maintains EC2 instances for blue/green                                   |
| `aws_lb` and `aws_lb_target_group` | ALB with two Target Groups (Blue, Green)                                 |
| `aws_lb_listener`                  | Routes traffic to the active TG (blue/green)                             |
| `aws_route53_record` (Optional)    | Switch DNS if Route53 is used instead of ALB                             |
| `count` or `for_each`              | Used to dynamically create blue or green infra based on deployment phase |

## Implementation for Blue Green Deployment 
### Step 1: Initialize Terraform Project
1. Create a folder:
```bash
mkdir blue-green-deployment && cd blue-green-deployment
```
2. Initialize Terraform files:
```bash
terraform init
```
### Step 2: Define AWS Provider
**File**: `provider.tf`
```bash
provider "aws" {
  region = var.aws_region
}
```
### Step 3: Create Variables
**File**: `variables.tf`
```bash
variable "aws_region" {}
variable "vpc_id" {}
variable "subnet_ids" {
  type = list(string)
}
variable "security_group_id" {}
variable "ami_id" {}
variable "instance_type" {
  default = "t2.micro"
}
```
### Step 4: Define ALB and Target Groups
**File**: `alb.tf`
```bash
resource "aws_lb" "app_alb" {
  name               = "blue-green-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = var.subnet_ids
}

resource "aws_lb_target_group" "blue_tg" {
  name     = "blue-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = var.vpc_id
}

resource "aws_lb_target_group" "green_tg" {
  name     = "green-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = var.vpc_id
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.app_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.blue_tg.arn
  }
}
```
###  Step 5: Blue Environment Setup
**File**: `asg_blue.tf`
```bash
resource "aws_launch_template" "blue_lt" {
  name_prefix   = "blue-lt-"
  image_id      = var.ami_id
  instance_type = var.instance_type
  user_data     = filebase64("userdata.sh")
  vpc_security_group_ids = [var.security_group_id]
}

resource "aws_autoscaling_group" "blue_asg" {
  desired_capacity     = 1
  max_size             = 1
  min_size             = 1
  vpc_zone_identifier  = var.subnet_ids
  target_group_arns    = [aws_lb_target_group.blue_tg.arn]

  launch_template {
    id      = aws_launch_template.blue_lt.id
    version = "$Latest"
  }

  health_check_type = "EC2"
}
```
### Step 6: Green Environment Setup
**File**: `asg_green.tf`
```bash
resource "aws_launch_template" "green_lt" {
  name_prefix   = "green-lt-"
  image_id      = var.ami_id
  instance_type = var.instance_type
  user_data     = filebase64("userdata.sh")
  vpc_security_group_ids = [var.security_group_id]
}

resource "aws_autoscaling_group" "green_asg" {
  desired_capacity     = 1
  max_size             = 1
  min_size             = 1
  vpc_zone_identifier  = var.subnet_ids
  target_group_arns    = [aws_lb_target_group.green_tg.arn]

  launch_template {
    id      = aws_launch_template.green_lt.id
    version = "$Latest"
  }

  health_check_type = "EC2"
}
```
### Step 7: User Data Script
**File**: `userdata.sh`
```bash
#!/bin/bash
yum update -y
yum install -y httpd
echo "Welcome to $(hostname)!" > /var/www/html/index.html
systemctl start httpd
systemctl enable httpd
```
### Step 8: Output the ALB DNS
**File**: `outputs.tf`
```bash 
output "alb_dns" {
  value = aws_lb.app_alb.dns_name
}
```
## Best Practices
| Best Practice                       | Description                                                                                               |
| ----------------------------------- | --------------------------------------------------------------------------------------------------------- |
| **Use Immutable Infrastructure**    | Always deploy new versions using new launch templates and EC2 instances—never update in place.            |
| **Separate Target Groups**          | Maintain distinct ALB target groups for Blue and Green to safely switch traffic and rollback if needed.   |
| **Validate Green Before Switching** | Always test the green environment using a test listener or direct IP before routing live traffic.         |
| **Use Remote State with Locking**   | Store Terraform state in S3 and enable DynamoDB locking to avoid concurrent update issues.                |
| **Clean Up Old Environments**       | After successful switch to green, destroy the blue environment to save cost and avoid confusion.          |
| **Integrate with CI/CD**            | Automate Terraform apply/switch/destroy steps using Jenkins or GitHub Actions with manual approval gates. |

## Conclusion
Blue-Green Deployment with Immutable Infrastructure provides **safe, zero-downtime deployments**. Using Terraform ensures consistent and repeatable rollouts. This strategy improves reliability, allows quick rollback, and aligns with modern DevOps practices.

## Contact Information
| Name | Email address         |
|------|------------------------|
| Mohamed Tharik  | md.tharik.sanaatak@mygurukulam.co    |

