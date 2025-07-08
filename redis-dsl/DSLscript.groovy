// Create the complete folder hierarchy step by step
folder('Terraform Infrastructure Jobs/env') {}
folder('Terraform Infrastructure Jobs/env/qa') {}
folder('Terraform Infrastructure Jobs/env/qa/wrappercode') {}
folder('Terraform Infrastructure Jobs/env/qa/wrappercode/middleware') {} 
folder('Terraform Infrastructure Jobs/env/qa/wrappercode/middleware/redis') {}

pipelineJob('Terraform Infrastructure Jobs/env/qa/wrappercode/middleware/redis/dev-otms-seed-job-wrappercode-redis') {
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
            scriptPath('terraform/wrappercode/env/qa/middleware/redis/Jenkinsfile')
        }
    }
}
