resource "aws_security_group" "alb_sg" {
  name = "dyplom-alb-sg"
  description = "Allow http request from internet"
  vpc_id = aws_vpc.main.id

  ingress {
    description = "HTTP from internet"
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_sg" {
  name = "dyplom-ecs-sg"
  description = "Allow traffic only from LB"
  vpc_id = aws_vpc.main.id

  ingress {
    description = "Traffic from ALB"
    from_port = 0
    to_port = 65535
    protocol = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "db_sg" {
  name = "dyplom-db-sg"
  description = "Allow traffic from ECS"
  vpc_id = aws_vpc.main.id
  ingress {
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    security_groups = [aws_security_group.ecs_sg.id]
  }

}