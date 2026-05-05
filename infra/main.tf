variable "aws_region" {
  default = "us-east-1"
}

provider "aws" {
  region = var.aws_region
}

module "security" {
  source = "./modules/security"

  listener_port = 80
  compute_port = 8080
  db_port = 5432
}

module "network"{
  source = "./modules/network"

  listener_port = 80
  compute_port = 8080
  load_balancer_security_group_id = module.security.ld_sg_id
  public_subnets = module.security.public_subnets
  db_subnets = module.security.db_subnets
  vpc_id = module.security.vpc_id
}

module "db" {
  source = "./modules/db"

  db_name = "funfarm"
  instance_type = "db.t4g.micro"
  db_space = 20
  db_user = "funfarm"

  db_security_group_id = module.security.db_sg_id
  db_subnet_group_name = module.network.db_subnet_group_name
}

module "compute"{
  source = "./modules/compute"

  compute_container_image = "622623004059.dkr.ecr.${var.aws_region}.amazonaws.com/funfarm:latest"
  compute_os_family="LINUX"
  compute_cpu_arch="ARM64"

  db_endpoint = module.db.db_instance_endpoint
  db_name = module.db.db_instance_db_name
  db_user = module.db.db_instance_user
  db_password_arn = module.db.db_instance_master_key_arn

  private_subnets = module.security.private_subnets
  security_group_id = module.security.app_sg_id
  load_balancer_target_arn = module.network.target_arn

  aws_region = var.aws_region
}

output "website_url" {
  value = "http://${module.network.ld_dns}"
}