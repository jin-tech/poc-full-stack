provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "poc_rg" {
  name     = "poc-resource-group"
  location = "East US"
}

resource "azurerm_app_service_plan" "poc_app_service_plan" {
  name                = "poc-app-service-plan"
  location            = azurerm_resource_group.poc_rg.location
  resource_group_name = azurerm_resource_group.poc_rg.name
  sku {
    tier     = "Free"
    size     = "F1"
  }
}

resource "azurerm_app_service" "poc_backend" {
  name                = "poc-backend-app"
  location            = azurerm_resource_group.poc_rg.location
  resource_group_name = azurerm_resource_group.poc_rg.name
  app_service_plan_id = azurerm_app_service_plan.poc_app_service_plan.id

  app_settings = {
    "JAVA_OPTS" = "-Dserver.port=8080"
  }
}

resource "azurerm_app_service" "poc_frontend" {
  name                = "poc-frontend-app"
  location            = azurerm_resource_group.poc_rg.location
  resource_group_name = azurerm_resource_group.poc_rg.name
  app_service_plan_id = azurerm_app_service_plan.poc_app_service_plan.id
}

resource "azurerm_sql_server" "poc_sql_server" {
  name                         = "pocsqlserver"
  resource_group_name          = azurerm_resource_group.poc_rg.name
  location                     = azurerm_resource_group.poc_rg.location
  version                      = "12.0"
  administrator_login          = "sqladmin"
  administrator_login_password = "YourStrongPassword123!"
}

resource "azurerm_sql_database" "poc_sql_database" {
  name                = "pocdatabase"
  resource_group_name = azurerm_resource_group.poc_rg.name
  location            = azurerm_resource_group.poc_rg.location
  server_name         = azurerm_sql_server.poc_sql_server.name
  requested_service_objective_name = "S0"
}

output "backend_app_url" {
  value = azurerm_app_service.poc_backend.default_site_hostname
}

output "frontend_app_url" {
  value = azurerm_app_service.poc_frontend.default_site_hostname
}

output "sql_server_name" {
  value = azurerm_sql_server.poc_sql_server.name
}

output "sql_database_name" {
  value = azurerm_sql_database.poc_sql_database.name
}