output "db_instance_user" {
  value       = aws_db_instance.postgres.username
  description = "Username of db"
}

output "db_instance_endpoint" {
  value = aws_db_instance.postgres.endpoint
  description = "Db endpoint"
}

output "db_instance_db_name" {
  value = aws_db_instance.postgres.db_name
  description = "Db name"
}

output "db_instance_master_key_arn" {
  value = aws_db_instance.postgres.master_user_secret[0].secret_arn
}
