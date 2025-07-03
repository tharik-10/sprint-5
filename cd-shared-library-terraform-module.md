<p align="center">
  <img src="https://github.com/user-attachments/assets/41f6bab5-6364-4125-ba54-2570c23fee89" width="400"/>
  <img src="https://github.com/user-attachments/assets/0bb6d105-b2fd-45ca-8935-6fe6f82f1753" width="400"/>
</p>

# Terraform Module CD Shared Library Documentation

| Created      | Last updated | Version     | Author         | Internal Reviewer | L0     | L1           | L2              |
|--------------|--------------|-------------|----------------|-------------------|--------|--------------|-----------------|
| 2025-07-02   | 2025-07-03   | Version 1.0 | Mohamed Tharik | Priyanshu         | Khushi | Mukul Joshi  | Piyush Upadhyay |

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Key Features](#key-features)
- [Supported Functions](#supported-functions)
- [Inputs](#inputs)
- [Outputs](#outputs)
- [Jenkinsfile Usage Example](#jenkinsfile-usage-example)
- [Folder Structure for terraform-module-cd-lib](#folder-structure-for-terraform-module-cd-lib)
- [Key File Descriptions](#key-file-descriptions)
- [Best Practices](#best-practices)
- [Conclusion](#conclusion)
- [Contact Information](#contact-information)
- [References](#references)

## Introduction

The **Terraform Module CD Shared Library** automates and standardizes the **deployment of Terraform modules** in Jenkins CD pipelines. It provides reusable functions for `apply` and `destroy`, allowing teams to safely and consistently deploy or tear down infrastructure in Dev, QA, or production environments.

## Prerequisites

| Component              | Requirement                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Jenkins Shared Library | Name: `terraform-module-cd-lib`<br>Points to the Git repo containing the shared library |
| Jenkins Agent          | - Terraform (v1.x)<br>- Git access to Terraform module repositories         |
| Permissions            | Required IAM or cloud credentials with permissions to apply/destroy resources |

## Key Features

- Automates Terraform apply and destroy operations
- Supports remote backend configuration
- Accepts variable injection for environment-specific deployment
- Prevents accidental destruction with confirmation flags
- Reusable across different module repositories

## Supported Functions

| Function         | Description                                                   |
|------------------|---------------------------------------------------------------|
| `terraformApply` | Applies the Terraform plan to create or update infrastructure |
| `terraformDestroy` | Destroys Terraform-managed infrastructure resources         |

## Inputs

| Key             | Type   | Description                                             |
|------------------|--------|---------------------------------------------------------|
| `directory`       | String | Path to the Terraform module                          |
| `vars`            | Map    | Terraform variables passed to apply/destroy            |
| `autoApprove`     | Bool   | If true, runs without interactive approval (default: true) |
| `backendConfig`   | Map    | Optional backend configuration for remote state        |

## Outputs

| Function         | Output                                                                   |
|------------------|---------------------------------------------------------------------------|
| `terraformApply`   | Provisions resources; pipeline proceeds if successful                   |
| `terraformDestroy` | Destroys resources; pipeline fails if destruction fails                 |

## Jenkinsfile Usage Example

```groovy
@Library('terraform-module-cd-lib') _

pipeline {
  agent any

  environment {
    TF_MODULE_DIR = 'terraform/modules/network'
  }

  stages {
    stage('Terraform Apply') {
      steps {
        script {
          terraformApply(
            directory: "${TF_MODULE_DIR}",
            vars: [
              environment: 'qa',
              region: 'ap-south-1'
            ],
            backendConfig: [
              bucket: 'tfstate-qa-bucket',
              key: 'network/qa/terraform.tfstate',
              region: 'ap-south-1'
            ],
            autoApprove: true
          )
        }
      }
    }

    stage('Terraform Destroy') {
      when {
        expression { params.DESTROY == true }
      }
      steps {
        script {
          terraformDestroy(
            directory: "${TF_MODULE_DIR}",
            vars: [
              environment: 'qa',
              region: 'ap-south-1'
            ],
            autoApprove: true
          )
        }
      }
    }
  }
}
```
## Folder Structure for terraform-module-cd-lib
```bash
terraform-module-cd-lib/
├── src/
│   └── org/
│       └── cloudninja/
│           └── TerraformCDUtils.groovy         # Contains apply/destroy logic
├── vars/
│   └── terraformApply.groovy                   # Wrapper for terraformApply
│   └── terraformDestroy.groovy                 # Wrapper for terraformDestroy
├── resources/
│   └── org/
│       └── cloudninja/
│           └── help.txt                        # (Optional) Help or logs
├── README.md                                   # CD Library documentation
├── Jenkinsfile                                 # Example usage
└── LICENSE                                     # Optional license file
```
## Key File Descriptions
| File/Directory            | Description                                                               |
| ------------------------- | ------------------------------------------------------------------------- |
| `TerraformCDUtils.groovy` | Contains core logic for `terraformApply` and `terraformDestroy` functions |
| `vars/`                   | Pipeline-accessible entrypoints for shared steps                          |
| `resources/`              | Optional help or error templates                                          |
| `README.md`               | Documentation for usage and configuration                                 |
| `Jenkinsfile`             | Sample usage of the shared CD library                                     |

## Best Practices
| Best Practice                      | Description                                                                |
| ---------------------------------- | -------------------------------------------------------------------------- |
| Use `autoApprove` only in CI/CD    | Prevent manual prompts during automated deployment                         |
| Lock Terraform version             | Ensure consistent behavior across pipelines                                |
| Use remote state for apply/destroy | Ensure consistent state tracking between teams                             |
| Protect destroy with flags         | Use `when` conditions and manual approvals before allowing destruction     |
| Inject variables from parameters   | Avoid hardcoded values; use Jenkins parameters to pass env-specific inputs |
| Apply in stages                    | Run `apply` only after validation and approval steps (if needed)           |

## Conclusion
The **Terraform Module CD Shared Library** helps teams deploy and destroy Terraform-managed infrastructure in a reliable and automated way. By reusing standardized functions in Jenkins pipelines, it ensures safe, consistent, and environment-aware deployments across your cloud environments.

## Contact Information
| Name           | Email                                                                         |
| -------------- | ----------------------------------------------------------------------------- |
| Mohamed Tharik | [md.tharik.sanaatak@mygurukulam.co](mailto:md.tharik.sanaatak@mygurukulam.co) |

## References
| Link                                                                                                   | Description                                                       |
| ------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------- |
| [Terraform CLI - Apply](https://developer.hashicorp.com/terraform/cli/commands/apply)                  | Official documentation for `terraform apply`                      |
| [Terraform CLI - Destroy](https://developer.hashicorp.com/terraform/cli/commands/destroy)              | Official documentation for `terraform destroy`                    |
| [Jenkins Shared Libraries](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)                 | Guide to creating and using shared libraries in Jenkins pipelines |
| [Terraform Remote Backends](https://developer.hashicorp.com/terraform/docs/language/settings/backends) | How to use remote backends like S3 for Terraform state management |
