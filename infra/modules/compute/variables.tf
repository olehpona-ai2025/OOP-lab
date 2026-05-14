variable "compute_container_image" {
  type = string
  description = "Container image for deploy"
}

variable "compute_os_family" {
  type = string
  default = "LINUX"
  description = "OS family"
}

variable "compute_cpu_arch" {
  type = string
  default = "ARM64"
  description = "Cpu arch"
}

variable "db_endpoint" {
  type = string
}

variable "db_name" {
  type = string
}

variable "db_user" {
  type = string
}

variable "db_password_arn" {
  type = string
}

variable "private_subnets" {
  type = list(string)
}

variable "security_group_id" {
  type = string
}

variable "load_balancer_target_arn" {
  type = string
}

variable "aws_region" {
  type = string
}

variable "user_access_secret_arn" {
  type = string
}

variable "github_repo_name" {
  type = string
}