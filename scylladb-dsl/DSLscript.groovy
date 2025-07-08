// Create the complete folder hierarchy step by step
folder('Terraform Infrastructure Jobs/env') {}
folder('Terraform Infrastructure Jobs/env/qa') {}
folder('Terraform Infrastructure Jobs/env/qa/wrappercode') {}
folder('Terraform Infrastructure Jobs/env/qa/wrappercode/database') {} 
folder('Terraform Infrastructure Jobs/env/qa/wrappercode/database/scylladb') {}

pipelineJob('Terraform Infrastructure Jobs/env/qa/wrappercode/database/scylladb/dev-otms-seed-job-wrappercode-scylladb') {
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
            scriptPath('terraform/wrappercode/env/qa/database/scylladb/Jenkinsfile')
        }
    }
}
