// Create the complete folder hierarchy step by step
folder('Terraform Infrastructure Jobs/env') {}
folder('Terraform Infrastructure Jobs/env/dev') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/database') {} 
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/database/postgresql') {}

pipelineJob('Terraform Infrastructure Jobs/env/dev/wrappercode/database/postgresql/dev-otms-seed-job-wrappercode-postgresql') {
    displayName('dev-otms-seed-job-wrappercode-postgresql')
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/Cloud-NInja-snaatak/jenkins-pipeline.git')
                        credentials('github-token')
                    }
                    branches('*/Tharik_SCRUM-536')
                }
            }
            scriptPath('terraform/wrappercode/env/dev/database/postgresql/Jenkinsfile')
        }
    }
}
