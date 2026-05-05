output "ld_dns" {
  value = aws_lb.app_alb.dns_name
}

output "db_subnet_group_name" {
  value = aws_db_subnet_group.db.name
}

output "target_arn" {
  value = aws_lb_target_group.app_tg.arn
}