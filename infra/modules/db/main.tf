resource "aws_db_instance" "postgres" {
  identifier             = "funfarm"
  engine                 = "postgres"
  engine_version         = "18.3"
  instance_class         = "db.t4g.micro"
  allocated_storage      = var.db_space
  db_name                = var.db_name
  username               = var.db_user
  manage_master_user_password = true
  db_subnet_group_name   = var.db_subnet_group_name
  vpc_security_group_ids = [var.db_security_group_id]
  skip_final_snapshot    = true
}
