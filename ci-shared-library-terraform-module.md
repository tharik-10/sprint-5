<p align="center">
  <img src="https://github.com/user-attachments/assets/41f6bab5-6364-4125-ba54-2570c23fee89" width="400"/>
  <img src="https://github.com/user-attachments/assets/0bb6d105-b2fd-45ca-8935-6fe6f82f1753" width="400"/>
</p>

# Terraform Module CI Shared Library Documentation
| Created        | Last updated      | Version         | author|  Internal Reviewer | L0 | L1 | L2|
|----------------|----------------|-----------------|-----------------|-----|------|----|----|
| 2025-07-01  | 2025-07-03   |     Version 1         |  Mohamed Tharik |Priyanshu|Khushi|Mukul Joshi |Piyush Upadhyay|

## Table of Contents 

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Key Features](#key-features)
- [Supported Functions](#supported-functions)
- [Inputs](#inputs)
- [Outputs](#outputs)
- [Jenkinsfile Usage Example](#jenkinsfile-usage-example)
- [Folder Structure for terraform-module-ci-lib](#folder-structure-for-terraform-module-ci-lib)
- [Key File Descriptions](#key-file-descriptions)
- [Best Practices](#best-practices)
- [Conclusion](#conclusion)
- [Contact Information](#contact-information)
- [References](#references)

## Introduction
The Terraform Module CI Shared Library enables automated validation of Terraform modules in Jenkins pipelines. It provides reusable functions for init, validate, and plan, ensuring consistency, accuracy, and best practices during CI workflows.

## Prerequisites

| Component              | Requirement                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Jenkins Shared Library | Name: `terraform-module-ci-lib`<br>Points to the Git repo containing the shared library |
| Jenkins Agent          | - Terraform (v1.x)<br>- Git access to Terraform module repositories         |
| Optional               | AWS credentials via IAM, Jenkins credentials plugin, or environment variables |

## Key Features
- Works with Terraform module directories
- Supports remote backend initialization
- Injects module input variables into plan
- Compatible with multi-module pipelines
- Fast validation during Pull Request (PR) or CI trigger

## Supported Functions
| Function            | Description                                                    |
| ------------------- | -------------------------------------------------------------- |
| `terraformInit`     | Initializes the Terraform backend and working directory        |
| `terraformValidate` | Validates module structure, syntax, and configuration          |
| `terraformPlan`     | Shows a dry-run of changes, using module variables if provided |

## Inputs 
| Key             | Type   | Description                                          |
| --------------- | ------ | ---------------------------------------------------- |
| `directory`     | String |Path to the Terraform module                         |
| `backendConfig` | Map    | Key-value pairs for remote backend (e.g., S3 config) |
| `vars`          | Map    | Terraform variables passed to the plan step          |
| `outFile`       | String |  Optional file to save the plan output                |

## Outputs
| Function            | Output                                                   |
| ------------------- | -------------------------------------------------------- |
| `terraformInit`     | Runs `terraform init` with optional remote backend setup |
| `terraformValidate` | Fails the build if the module is invalid                 |
| `terraformPlan`     | Shows Terraform changes and optionally writes to a file  |

## Jenkinsfile Usage Example
```bash
@Library('terraform-module-ci-lib') _

pipeline {
  agent any

  environment {
    TF_MODULE_DIR = 'terraform/modules/network'
  }

  stages {
    stage('Terraform Init') {
      steps {
        script {
          terraformInit(
            directory: "${TF_MODULE_DIR}",
            backendConfig: [
              bucket: 'tfstate-dev-bucket',
              key: 'network/dev/terraform.tfstate',
              region: 'ap-south-1'
            ]
          )
        }
      }
    }

    stage('Terraform Validate') {
      steps {
        script {
          terraformValidate(directory: "${TF_MODULE_DIR}")
        }
      }
    }

    stage('Terraform Plan') {
      steps {
        script {
          terraformPlan(
            directory: "${TF_MODULE_DIR}",
            vars: [
              environment: 'dev',
              region: 'ap-south-1'
            ],
            outFile: 'tfplan.out'
          )
        }
      }
    }
  }
}
```
## Folder Structure for terraform-module-ci-lib
```bash
terraform-module-ci-lib/
├── src/
│   └── org/
│       └── cloudninja/
│           └── TerraformUtils.groovy         # Core Groovy file with init, validate, plan functions
├── vars/
│   └── terraformInit.groovy                  # (Optional) Wrapper for terraformInit
│   └── terraformValidate.groovy              # (Optional) Wrapper for terraformValidate
│   └── terraformPlan.groovy                  # (Optional) Wrapper for terraformPlan
├── resources/
│   └── org/
│       └── cloudninja/
│           └── help.txt                      # (Optional) Help or default messages
├── README.md                                 # Library documentation (purpose, usage, examples)
├── Jenkinsfile                               # Example Jenkins pipeline using this library
└── LICENSE                                   # (Optional) License file if open sourced
```
## Key File Descriptions
| File/Directory          | Description                                                                       |
| ----------------------- | --------------------------------------------------------------------------------- |
| `src/org/terraformci/`  | Main namespace for all reusable Terraform functions                               |
| `TerraformUtils.groovy` | Contains functions like `terraformInit`, `terraformValidate`, and `terraformPlan` |
| `vars/`                 | (Optional) Global pipeline steps that act as simplified entrypoints               |
| `resources/`            | (Optional) For text, help messages, templates if needed                           |
| `README.md`             | Documentation covering purpose, inputs, outputs, Jenkins usage                    |
| `Jenkinsfile.example`   | Demonstrates how to use the shared library in a real CI pipeline                  |

## Best Practices

| Best Practice                                       | Description                                                                 |
|-----------------------------------------------------|-----------------------------------------------------------------------------|
| Run `init`, `validate`, and `plan` in CI           | Ensure these steps are always part of the CI pipeline to catch errors early |
| Enforce `terraform fmt -check`                     | Maintain consistent Terraform code formatting across all modules            |
| Lock Terraform version                             | Use a specific version (e.g., `~> 1.5.0`) to avoid compatibility issues      |
| Fail pipeline on validation or plan errors         | Prevent broken code from being merged by enforcing failure on CI errors     |
| Use remote backend for plan (optional)             | Simulate real-world planning using shared backends like S3 + DynamoDB       |
| Pass required variables to `plan`                  | Avoid using unsafe defaults by injecting environment-specific variables      |
| Keep library logic clean and reusable              | Use parameters, not hardcoded values, for flexibility across different modules |

## Conclusion
- The Terraform Module CI Shared Library helps automate and simplify the validation of Terraform modules in Jenkins. It makes sure your code is correct, follows best practices, and works as expected — all before deployment.

- By using this shared library, teams can save time, avoid mistakes, and keep their Terraform pipelines clean, consistent, and reliable.

## Contact Information
| Name | Email address         |
|------|------------------------|
| Mohamed Tharik  | md.tharik.sanaatak@mygurukulam.co    |

## References
| Link                                                                                               | Description                                                                       |
| -------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| [Jenkins Shared Libraries](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)             | How to structure and use shared libraries in Jenkins pipelines                    |
| [Terraform CLI - Commands](https://developer.hashicorp.com/terraform/cli/commands)                 | Official documentation for Terraform commands like `init`, `validate`, and `plan` |
| [Terraform Best Practices](https://developer.hashicorp.com/terraform/docs/language/best-practices) | Recommended practices for writing clean and reusable Terraform modules            |

