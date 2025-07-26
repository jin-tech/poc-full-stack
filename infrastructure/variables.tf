variable "resource_group_name" {
  description = "The name of the resource group"
  type        = string
}

variable "location" {
  description = "The Azure location where resources will be created"
  type        = string
  default     = "East US"
}

variable "app_service_plan_name" {
  description = "The name of the App Service Plan"
  type        = string
}

variable "web_app_name" {
  description = "The name of the Web App"
  type        = string
}

variable "database_name" {
  description = "The name of the SQL Database"
  type        = string
}

variable "admin_username" {
  description = "The admin username for the SQL Database"
  type        = string
}

variable "admin_password" {
  description = "The admin password for the SQL Database"
  type        = string
  sensitive   = true
}

variable "sku" {
  description = "The SKU for the SQL Database"
  type        = string
  default     = "Basic"
}