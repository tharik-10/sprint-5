<p align="center">
  <img src="https://github.com/user-attachments/assets/41f6bab5-6364-4125-ba54-2570c23fee89" width="400"/>
  <img src="https://github.com/user-attachments/assets/0bb6d105-b2fd-45ca-8935-6fe6f82f1753" width="400"/>
</p>

# Terraform Module CI Shared Library Documentation
| Created        | Last updated      | Version         | author|  Internal Reviewer | L0 | L1 | L2|
|----------------|----------------|-----------------|-----------------|-----|------|----|----|
| 2025-07-01  | 2025-07-01   |     Version 1         |  Mohamed Tharik |Priyanshu|Khushi|Mukul Joshi |Piyush Upadhyay|

## Table of Contents 

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

