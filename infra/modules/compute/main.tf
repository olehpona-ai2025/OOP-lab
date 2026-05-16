resource "aws_ecr_repository" "app_repo" {
  name = "funfarm_repo"
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "ecs_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "ecs_execution_secret" {
  role= aws_iam_role.ecs_execution_role.name
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "secretsmanager:GetSecretValue"
        Effect = "Allow"
        Resource = [var.db_password_arn, var.user_access_secret_arn]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecs_cluster" "main" {
  name = "funfarm-fargate-cluster"
}

resource "aws_cloudwatch_log_group" "ecs_logs" {
  name = "funfarm-ecs"
}

resource "aws_ecs_task_definition" "app" {
  family                   = "funfarm-app-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  runtime_platform {
    operating_system_family = var.compute_os_family
    cpu_architecture = var.compute_cpu_arch
  }

  container_definitions = jsonencode([
    {
      name      = "funfarm-app-container"
      image     = var.compute_container_image
      cpu       = 256
      memory    = 512
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_endpoint}/${var.db_name}" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.db_user },
        {name = "SPRING_DATASOURCE_DRIVER_CLASS_NAME", value = "org.postgresql.Driver"}
      ]
      secrets = [
        {
          name="SPRING_DATASOURCE_PASSWORD", valueFrom = "${var.db_password_arn}:password::",
        }, {
          name="APP_API_SECRETS", valueFrom = var.user_access_secret_arn
        }]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-region = var.aws_region
          awslogs-group = aws_cloudwatch_log_group.ecs_logs.name
          awslogs-stream-prefix = "funfarm"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "app_service" {
  name            = "funfarm-app-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = 2
  launch_type     = "FARGATE"


  network_configuration {
    subnets          = var.private_subnets
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }
  load_balancer {
    target_group_arn = var.load_balancer_target_arn
    container_name   = "funfarm-app-container"
    container_port   = 8080
  }

}

//Github action upload and deploy

resource "aws_iam_openid_connect_provider" "github" {
  client_id_list  = ["sts.amazonaws.com"]
  url             = "https://token.actions.githubusercontent.com"
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1", "1c58a3a8518e8759bf075b76b750d4f2df264fcd"]
}

resource "aws_iam_role" "github_action_role" {
  name = "github_action_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.github.arn
        }
        Condition = {
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:${var.github_repo_name}:*"
          }
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          }
        }
      }
    ]
  })
}

data "aws_iam_policy_document" "ecs_policy_data" {
  statement {
    effect    = "Allow"
    actions   = ["ecs:RegisterTaskDefinition", "ecs:DescribeTaskDefinition"]
    resources = ["*"]
  }

  statement {
    effect    = "Allow"
    actions   = ["ecs:UpdateService", "ecs:DescribeServices"]
    resources = [aws_ecs_service.app_service.arn]
  }

  statement {
    effect    = "Allow"
    actions   = ["iam:PassRole"]
    resources = [aws_iam_role.ecs_execution_role.arn]
  }
}

resource "aws_iam_policy" "ecs_policy" {
  name        = "ecs_policy_data"
  description = "Ecs policy for github action"
  policy      = data.aws_iam_policy_document.ecs_policy_data.json
}

resource "aws_iam_policy_attachment" "github_action_ecr_policy_attachment" {
  name       = "github_action_ecr_policy_attachment"
  roles      = [aws_iam_role.github_action_role.name]
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryPowerUser"
}

resource "aws_iam_policy_attachment" "github_action_ecs_policy_attachment" {
  name       = "github_action_ecs_policy_attachment"
  roles      = [aws_iam_role.github_action_role.name]
  policy_arn = aws_iam_policy.ecs_policy.arn
}