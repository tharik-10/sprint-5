<p align="center">
  <img src="https://github.com/user-attachments/assets/41f6bab5-6364-4125-ba54-2570c23fee89" width="400"/>
  <img src="https://github.com/user-attachments/assets/0bb6d105-b2fd-45ca-8935-6fe6f82f1753" width="400"/>
</p>

# Terraform Module CD Shared Library POC
| Created        | Last updated      | Version         | author|  Internal Reviewer | L0 | L1 | L2|
|----------------|----------------|-----------------|-----------------|-----|------|----|----|
| 2025-07-05  | 2025-07-05   |     Version 1         |  Mohamed Tharik |Priyanshu|Khushi|Mukul Joshi |Piyush Upadhyay|

## Table of Contents


- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Folder Structure](#folder-structure)
- [POC Steps](#poc-steps)
- [Best Practices](#best-practices)
- [Conclusion](#conclusion)
- [References](#references)

## Introduction

- This Proof of Concept (POC) demonstrates a Jenkins **CD Shared Library** that abstracts and standardizes Terraform operations for **Dev/QA infrastructure** environments.  
- It enables modular and reusable logic for `terraform apply` and `destroy` stages, ensuring clean, traceable, and auditable infrastructure deployments.

## Prerequisites

| Component              | Requirement                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Jenkins                | Installed and running                                                       |
| Jenkins Shared Library | GitHub repo with reusable CD logic (e.g., `terraformApply`, `terraformDestroy`) |
| Jenkins Credentials    | GitHub token and AWS credentials in Jenkins                                 |
| Jenkins Agent          | Terraform installed, Git installed                                          |
| Terraform              | Version 5.0                                                                 |
| GitHub Repo            | Terraform modules (VPC, Subnets, IGW) in `terraform/modules/`        |

## Folder Structure

### Jenkins Shared Library (`shared-library`)
```bash
shared-library/
├── vars/
│   └── terraformCDTemplate.groovy          # Template with apply/destroy logic
├── src/
│   └── org/
│       └── cloudninja/
│           └── TerraformCDUtils.groovy       # Shared helper class for Terraform commands
```
## Terraform Repository
```bash
terraform/
├── main.tf
├── variables.tf
├── outputs.tf
├── terraform.tfvars
├── modules/
|    └── network/
|       ├── main.tf
|       ├── variables.tf
|       └── outputs.tf
├── Jenkinsfile
```
## POC Steps for Terraform Modules CD
### Step 1: Jenkins Shared Library - Terraform Utils
`src/org/cloudninja/TerraformCDUtils.groovy`
```bash
package org.cloudninja

class TerraformCDUtils implements Serializable {
  def steps

  TerraformCDUtils(steps) {
    this.steps = steps
  }

  def terraformApply(Map config) {
    def dir = config.directory
    def varsArgs = config.vars?.collect { k, v -> "-var '${k}=${v}'" }?.join(' ') ?: ''
    steps.sh "cd ${dir} && terraform apply -auto-approve ${varsArgs}"
  }

  def terraformDestroy(Map config) {
    def dir = config.directory
    def varsArgs = config.vars?.collect { k, v -> "-var '${k}=${v}'" }?.join(' ') ?: ''
    steps.sh "cd ${dir} && terraform destroy -auto-approve ${varsArgs}"
  }
}
```
### Step 2: Jenkins Shared Library – Template
`vars/terraformCDTemplate.groovy`
```bash
def call(Map config = [:]) {
  def steps = this

  def MODULE_DIR = config.get('tfModuleDir', '')
  def TF_VARS    = config.get('tfVars', [:])
  def ACTION     = config.get('action', 'apply')  // accepts "apply" or "destroy"

  def tf = new org.cloudninja.TerraformUtils(steps)

  node {
    try {
      stage("Terraform ${ACTION.capitalize()}") {
        if (ACTION == 'apply') {
          tf.terraformApply(directory: MODULE_DIR, vars: TF_VARS)
        } else if (ACTION == 'destroy') {
          tf.terraformDestroy(directory: MODULE_DIR, vars: TF_VARS)
        } else {
          error "Unsupported action: ${ACTION}. Use 'apply' or 'destroy'."
        }
      }

      currentBuild.result = 'SUCCESS'
    } catch (err) {
      currentBuild.result = 'FAILURE'
      throw err
    }
  }
}
```
### Step 3: Jenkinsfile in Terraform Module Repo
`Jenkinsfile`
```bash
@Library('terraform-module-cd-lib@main') _

terraformCDTemplate(
  tfModuleDir: 'terraform',
  tfVars: [
    environment: 'dev',
    region: 'ap-south-1',
    vpc_cidr: '10.0.0.0/16',
    public_subnet_cidr: '10.0.1.0/24',
    az: 'ap-south-1a'
  ],
  action: 'apply'   // Change to 'destroy' to tear down
)
```
### Step 4: Trigger Jenkins Pipeline
- Navigate to Jenkins
- Create a Pipeline Job → SCM → point to the Terraform repo
- Set main as the branch
- Run the job
- Output should show:
  - Terraform Init successful
  - Terraform Validate passed
  - Terraform Plan generated plan with no errors

## Best Practices
| Best Practice                  | Why It Matters                                            |
| ------------------------------ | --------------------------------------------------------- |
| Use `-auto-approve` in CD only | Avoids manual prompts during automation                   |
| Parameterize `action`          | Enables flexibility for switching between apply/destroy   |
| Reuse CI logic from library    | Keeps pipeline clean and DRY                              |
| Secure backend (S3 + DynamoDB) | Prevents TF state corruption in team environments         |
| Enforce naming conventions     | Helps identify Dev/QA vs Prod stacks by tags and prefixes |

## Conclusion
- This POC successfully demonstrates a **CD Shared Library** for Terraform-based infrastructure.
- By defining and calling reusable `apply` and `destroy` functions, Jenkins pipelines can deploy or tear down cloud environments consistently, safely, and efficiently.

## Contact Information
| Name | Email address         |
|------|------------------------|
| Mohamed Tharik  | md.tharik.sanaatak@mygurukulam.co    |

## References
| Link                                                                                                | Description                          |
| --------------------------------------------------------------------------------------------------- | ------------------------------------ |
| [Jenkins Shared Libraries](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)              | How to create reusable Jenkins logic |
| [Terraform Commands](https://developer.hashicorp.com/terraform/cli/commands)                        | Full CLI documentation               |
| [Terraform Auto Approve](https://developer.hashicorp.com/terraform/cli/commands/apply#auto-approve) | Explanation of auto-approve flag     |



