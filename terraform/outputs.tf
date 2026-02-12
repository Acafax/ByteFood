output "alb_dns_name" {
  description = "Address of application"
  value = "http://${aws_lb.main.dns_name}"
}

output "db_password" {
  description = "Password to database"
  value = random_password.db_password.result
  sensitive = true
}