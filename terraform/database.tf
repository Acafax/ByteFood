resource "random_password" "db_password" {
  length = 16
  special = false
}

resource "aws_db_subnet_group" "main" {
  name = "dyplom-db-subnet"
  subnet_ids = [aws_subnet.private_1.id, aws_subnet.private_2.id]
}

resource "aws_db_instance" "main" {
  identifier = "dyplom-db"
  engine = "postgres"
  engine_version = "16.3"
  instance_class = "db.t3.micro"
  allocated_storage = 1
  db_name = "projektdyplomowy"
  username = "postgres"
  db_subnet_group_name = aws_db_subnet_group.main.id
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  skip_final_snapshot = true
  publicly_accessible = false
}