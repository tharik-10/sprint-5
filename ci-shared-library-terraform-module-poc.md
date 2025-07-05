<p align="center">
  <img src="https://github.com/user-attachments/assets/41f6bab5-6364-4125-ba54-2570c23fee89" width="400"/>
  <img src="https://github.com/user-attachments/assets/0bb6d105-b2fd-45ca-8935-6fe6f82f1753" width="400"/>
</p>

# Terraform Module CI Shared Library POC
| Created        | Last updated      | Version         | author|  Internal Reviewer | L0 | L1 | L2|
|----------------|----------------|-----------------|-----------------|-----|------|----|----|
| 2025-07-05  | 2025-07-05   |     Version 1         |  Mohamed Tharik |Priyanshu|Khushi|Mukul Joshi |Piyush Upadhyay|

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Folder Structure](#folder-structure)
- [POC Steps for Terraform Modules CI](#poc-steps-for-terraform-modules-ci)
- [Best Practices](#best-practices)
- [Conclusion](#conclusion)
- [Contact Information](#contact-information)
- [References](#references)

## Introduction

This Proof of Concept (POC) demonstrates the use of a Jenkins CI Shared Library for automating Terraform workflows for Dev/QA environments.  
The shared library standardizes and simplifies tasks such as `terraform init`, `validate`, and `plan` across all Terraform modules.

## Prerequisites

| Component              | Requirement                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Jenkins                | Installed and configured                                                    |
| Jenkins Shared Library | GitHub repo with CI logic (e.g., `terraformInit`, `terraformValidate`, etc) |
| Jenkins Credentials    | GitHub token for private repo (if applicable)                               |
| Jenkins Agent          | Terraform installed, Git installed                                          |
| Terraform              | Version 5.0                                                                 |
| GitHub Repo            | Terraform modules in a structure with VPC, Subnets, IGW      |

## Folder Structure

### Jenkins Shared Library (`terraform-module-ci-lib`)
```bash
terraform-module-ci-lib/
├── vars/
│   └── terraformCICheckTemplate.groovy  # Template calling init, validate, plan
├── src/
│   └── org/
│       └── snaatak/
│           └── TerraformCIUtils.groovy   # Shared reusable class for Terraform functions
```
### Terraform Modules Repository
```bash
terraform/
├── modules/
│   └── network/
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
├── Jenkinsfile
```
## POC Steps for Terraform Modules CI 
### Step 1: Clone the Terraform Module Repository
Ensure the Terraform code is under a valid module structure.

### Step 2: Jenkins Shared Library - Terraform Utils
`src/org/snaatak/TerraformUtils.groovy`
```bash
package org.snaatak

class TerraformCIUtils implements Serializable {
  def steps

  TerraformCIUtils(steps) {
    this.steps = steps
  }

  def terraformInit(Map config) {
    def dir = config.directory
    def backendConfig = config.backendConfig?.collect { k, v -> "-backend-config=${k}=${v}" }?.join(' ') ?: ''
    steps.sh "cd ${dir} && terraform init ${backendConfig}"
  }

  def terraformValidate(Map config) {
    def dir = config.directory
    steps.sh "cd ${dir} && terraform validate"
  }

  def terraformPlan(Map config) {
    def dir = config.directory
    def varsArgs = config.vars?.collect { k, v -> "-var '${k}=${v}'" }?.join(' ') ?: ''
    def outFileArg = config.outFile ? "-out=${config.outFile}" : ''
    steps.sh "cd ${dir} && terraform plan ${varsArgs} ${outFileArg}"
  }
}
```
### Step 3: Jenkins Shared Library - Template
`vars/terraformCICheckTemplate.groovy`
```bash
def call(Map config = [:]) {
  def steps = this

  def MODULE_DIR    = config.get('tfModuleDir', '')
  def BACKEND_CONF  = config.get('backendConfig', [:])
  def TF_VARS       = config.get('tfVars', [:])
  def PLAN_OUT_FILE = config.get('planOutFile', 'tfplan.out')

  def tf = new org.snaatak.TerraformCIUtils(steps)

  node {
    try {
      stage('Terraform Init') {
        tf.terraformInit(
          directory: MODULE_DIR,
          backendConfig: BACKEND_CONF
        )
      }

      stage('Terraform Validate') {
        tf.terraformValidate(
          directory: MODULE_DIR
        )
      }

      stage('Terraform Plan') {
        tf.terraformPlan(
          directory: MODULE_DIR,
          vars: TF_VARS,
          outFile: PLAN_OUT_FILE
        )
      }

      currentBuild.result = 'SUCCESS'
    } catch (err) {
      currentBuild.result = 'FAILURE'
      throw err
    }
  }
}
```
### Step 4: Jenkinsfile in Terraform Module Repo
```bash
@Library('terraform-module-ci-lib@main') _

terraformCICheckTemplate(
  tfModuleDir: 'terraform',
  backendConfig: [
    bucket: 'tfstate-demo-bucket',
    key: 'network/dev/terraform.tfstate',
    region: 'ap-south-1'
  ],
  tfVars: [
    environment: 'dev',
    region: 'ap-south-1',
    vpc_cidr: '10.0.0.0/16',
    public_subnet_cidr: '10.0.1.0/24',
    az: 'ap-south-1a'
  ],
  planOutFile: 'tfplan.out'
)
```
### Step 5: Trigger Jenkins Pipeline
- Navigate to Jenkins
- Create a Pipeline Job → SCM → point to the Terraform repo
- Set main as the branch
![Screenshot-267](https://github.com/user-attachments/assets/d3f7057f-78e0-40e0-8638-974ced2982da)

- Run the job
- Output should show:
  - Terraform Init successful
  - Terraform Validate passed
  - Terraform Plan generated plan with no errors
![Screenshot-266](https://github.com/user-attachments/assets/494b56c3-96c4-4809-8823-eb38a23c6010)

## Best Practices
| Practice                      | Description                                           |
| ----------------------------- | ----------------------------------------------------- |
| Use Shared Libraries          | Avoid duplication across pipelines                    |
| Lock Terraform Version        | Maintain consistent behavior across agents            |
| Validate before Plan          | Always validate Terraform code before planning        |
| Use `-backend-config`         | Externalize backend settings instead of hardcoding    |
| Commit Plan Output (Optional) | Review plan files during approval workflows           |
| Use Workspaces (Advanced)     | For managing multiple environments like dev, qa, prod |

## Conclusion
- This POC proves that a reusable Jenkins Shared Library can standardize CI processes across Terraform modules.
- It enables consistent init, validate, and plan stages — improving quality, automation, and productivity in Dev/QA infra setup.

## Contact Information
| Name | Email address         |
|------|------------------------|
| Mohamed Tharik  | md.tharik.sanaatak@mygurukulam.co    |

## References
| Link                                                                                               | Description                                   |
| -------------------------------------------------------------------------------------------------- | --------------------------------------------- |
| [Jenkins Shared Libraries](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)             | Use and structure of Jenkins Shared Libraries |
| [Terraform CLI](https://developer.hashicorp.com/terraform/cli/commands)                            | Terraform command documentation               |
| [Terraform Best Practices](https://developer.hashicorp.com/terraform/docs/language/best-practices) | Writing scalable, reusable Terraform modules  |


