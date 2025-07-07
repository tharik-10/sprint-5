// Create the complete folder hierarchy step by step
folder('Terraform Infrastructure Jobs/env') {}
folder('Terraform Infrastructure Jobs/env/dev') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/database') {} 
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/database/scylladb') {}

pipelineJob('Terraform Infrastructure Jobs/env/dev/wrappercode/database/scylladb/dev-otms-seed-job-wrappercode-scylladb') {
    displayName('dev-otms-seed-job-wrappercode-scylladb')
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/Cloud-NInja-snaatak/jenkins-pipeline.git')
                        credentials('github-token')
                    }
                    branches('*/Tharik_SCRUM-534')
                }
            }
            scriptPath('terraform/wrappercode/env/dev/database/notification/Jenkinsfile')
        }
    }
}
