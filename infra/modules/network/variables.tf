variable "load_balancer_security_group_id" {
  type = string
}

variable "health_check_path" {
  type = string
  default = "/farmArea"
}

variable "listener_port" {
  type = string
}

variable "compute_port" {
  type = number
}

variable "vpc_id" {
  type = string
}

variable "public_subnets" {
  type = list(string)
}

variable "db_subnets" {
  type = list(string)
}