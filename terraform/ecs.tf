resource "aws_ecs_cluster" "main" {
  name = "dyplomowy-cluster"
}
# --- IAM ---
resource "aws_iam_role" "ecs_execution_role" {
  name = "fyplom-ecs-execution-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole", Effect = "Allow",
      Principal = {Service = "ecs-tasks.amazonaws.com"}}]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_cloudwatch_log_group" "app_logs" {
  name = "/ecs/dyplom-app"
  retention_in_days = 1
}










# --- BACKEND TASK ---
resource "aws_ecs_task_definition" "backend" {
  family                   = "dyplom-backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([
    {
      name  = "backend"
      image = "nginx:latest" # TODO: Zmień na URL swojego ECR po zbudowaniu obrazu!
      portMappings = [{ containerPort = 8080 }]
      logConfiguration = {
        logDriver = "awslogs",
        options = { "awslogs-group" = aws_cloudwatch_log_group.app_logs.name, "awslogs-region" = "us-east-1", "awslogs-stream-prefix" = "backend" }
      }
      environment = [
        # AUTOMATYZACJA: Terraform wstawia tu adres bazy utworzonej w database.tf!
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${aws_db_instance.main.address}:5432/projektdyplomowy" },
        { name = "SPRING_DATASOURCE_USERNAME", value = "postgres" },
        { name = "SPRING_DATASOURCE_PASSWORD", value = random_password.db_password.result }
      ]
    }
  ])
}

# --- FRONTEND TASK ---
resource "aws_ecs_task_definition" "frontend" {
  family                   = "dyplom-frontend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([
    {
      name  = "frontend"
      image = "nginx:latest" # TODO: Zmień na URL ECR z Twoim frontendem
      portMappings = [{ containerPort = 80 }]
      logConfiguration = {
        logDriver = "awslogs",
        options = { "awslogs-group" = aws_cloudwatch_log_group.app_logs.name, "awslogs-region" = "us-east-1", "awslogs-stream-prefix" = "frontend" }
      }
      # Frontend nie potrzebuje tu zmiennych, bo łączy się przez relatywną ścieżkę /api obsługiwaną przez ALB
    }
  ])
}

# --- SERWISY ---
resource "aws_ecs_service" "backend" {
  name            = "backend-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets         = [aws_subnet.private_1.id, aws_subnet.private_2.id] # PRYWATNE!
    security_groups = [aws_security_group.ecs_sg.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend.arn
    container_name   = "backend"
    container_port   = 8080
  }
}

resource "aws_ecs_service" "frontend" {
  name            = "frontend-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets         = [aws_subnet.private_1.id, aws_subnet.private_2.id] # PRYWATNE!
    security_groups = [aws_security_group.ecs_sg.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.frontend.arn
    container_name   = "frontend"
    container_port   = 80
  }
}