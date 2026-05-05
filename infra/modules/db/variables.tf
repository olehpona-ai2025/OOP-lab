variable "instance_type" {
  type        = string
  default     = "db.t4g.micro"
  description = "The type of DB instance"
}

variable "db_name" {
  type = string
}

variable "db_user" {
  type = string
}

variable "db_space" {
  type = number
}

variable "db_subnet_group_name" {
  type = string
}

variable "db_security_group_id" {
  type = string
}