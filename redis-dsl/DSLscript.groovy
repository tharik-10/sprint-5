// Create the complete folder hierarchy step by step
folder('Terraform Infrastructure Jobs/env') {}
folder('Terraform Infrastructure Jobs/env/dev') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode') {}
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/databse') {} 
folder('Terraform Infrastructure Jobs/env/dev/wrappercode/database/redis') {}

pipelineJob('Terraform Infrastructure Jobs/env/dev/wrappercode/database/redis/dev-otms-seed-job-wrappercode-redis') {
    displayName('dev-otms-seed-job-wrappercode-redis')
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/Cloud-NInja-snaatak/jenkins-pipeline.git')
                        credentials('github-token')
                    }
                    branches('*/Tharik_SCRUM-535')
                }
            }
            scriptPath('terraform/wrappercode/env/dev/database/redis/Jenkinsfile')
        }
    }
}
