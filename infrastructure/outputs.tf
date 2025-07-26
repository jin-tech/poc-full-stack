output "backend_url" {
  value = azurerm_app_service.backend_app.default_site_hostname
}

output "frontend_url" {
  value = azurerm_app_service.frontend_app.default_site_hostname
}

output "database_connection_string" {
  value = azurerm_sql_database.example.connection_string
}