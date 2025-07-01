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

## Key Features
- Works with Terraform module directories
- Supports remote backend initialization
- Injects module input variables into plan
- Compatible with multi-module pipelines
- Fast validation during Pull Request (PR) or CI trigger
