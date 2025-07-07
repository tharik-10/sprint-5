// Create the complete folder hierarchy step by step
folder('Terraform Infrastructure Jobs/env') {}
folder('Terraform Infrastructure Jobs/env/dev') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/applications') {} 
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/applications/notification') {}

pipelineJob('Terraform Infrastructure Jobs/env/dev/wrappercode/applications/notification/dev-otms-seed-job-wrappercode-notification') {
    displayName('dev-otms-seed-job-wrappercode-notification')
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/Cloud-NInja-snaatak/jenkins-pipeline.git')
                        credentials('github-token')
                    }
                    branches('*/Tharik_SCRUM-533')
                }
            }
            scriptPath('terraform/wrappercode/env/dev/application/notification/Jenkinsfile')
        }
    }
}
