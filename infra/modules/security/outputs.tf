output "app_sg_id" {
  value = aws_security_group.app_sg.id
}

output "db_sg_id" {
  value = aws_security_group.db_sg.id
}

output "ld_sg_id" {
  value = aws_security_group.alb_sg.id
}

output "vpc_id" {
  value = module.vpc.vpc_id
}

output "private_subnets" {
  value = module.vpc.private_subnets
}

output "public_subnets" {
  value = module.vpc.public_subnets
}

output "db_subnets" {
  value = module.vpc.database_subnets
}

output "user_access_secret_arn"{
  value = aws_secretsmanager_secret.user_access_secret.arn
}

output "user_access_secret_string" {
  value = "${random_password.john_password.result}=John, ${random_password.oleh_password.result}=Oleh"
}