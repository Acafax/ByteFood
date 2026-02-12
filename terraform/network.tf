# -- VPC --
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true
  tags = {Name = "dyplom-vpc"}
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
  tags = {Name ="dyplom-igw"}
}

resource "aws_subnet" "public_1" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "eu-north-1a"
  map_public_ip_on_launch = true
  tags = {Name="dyplom-public-1"}
}

resource "aws_subnet" "public_2" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.2.0/24"
  availability_zone = "eu-north-1b"
  map_public_ip_on_launch = true
  tags = {Name="dyplom-public-2"}
}

resource "aws_subnet" "private_1" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.10.0/24"
  availability_zone = "eu-north-1a"
  tags = {Name="dyplom-private-1"}
}

resource "aws_subnet" "private_2" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.11.0/24"
  availability_zone = "eu-north-1b"
  tags = {Name="dyplomowy-private-2"}
}
# --- NAT GATEWAY ---

resource "aws_eip" "nat" {
  domain = "vpc"
}

resource "aws_nat_gateway" "main" {
  allocation_id = aws_eip.nat.id
  subnet_id = aws_subnet.public_1.id
  tags = {Name="dyplom-nat"}
  depends_on = [aws_internet_gateway.igw]
}
#--- ROUTING ---
# Połączenie z internetem przez IGW
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
}
#Połączenie do internetu przez NAT
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id
  }
}

resource "aws_route_table_association" "public_1" {
  route_table_id = aws_route_table.public.id
  subnet_id = aws_subnet.public_1.id
}
resource "aws_route_table_association" "public_2" {
  route_table_id = aws_route_table.public.id
  subnet_id = aws_subnet.public_2.id
}
resource "aws_route_table_association" "private_1" {
  route_table_id = aws_route_table.private.id
  subnet_id = aws_subnet.private_1.id
}
resource "aws_route_table_association" "private_2" {
  route_table_id = aws_route_table.private.id
  subnet_id = aws_subnet.private_2.id
}