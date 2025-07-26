# Proof of Concept Project

This project is a Proof of Concept (POC) designed to demonstrate best practices in software design architecture. It incorporates various technologies and frameworks to create a full-stack application.

## Project Structure

The project is organized into the following main directories:

- **backend**: Contains the Java Spring Boot application.
- **frontend**: Contains the Angular application.
- **infrastructure**: Contains Terraform scripts for managing cloud resources.
- **docker-compose.yml**: Defines services for running the application using Docker.

## Technologies Used

1. **Terraform**: For managing cloud infrastructure on Azure.
2. **Azure**: Utilized for hosting services and databases.
3. **Java Spring Boot**: For building the backend RESTful API.
4. **Angular**: For creating the frontend web application.
5. **Docker**: For containerizing the applications.
6. **Unit Testing**: Implemented for both backend and frontend components.
7. **Integration Testing**: Ensures that different parts of the application work together.
8. **End-to-End Testing**: Validates the complete flow of the application.
9. **Logging and Monitoring**: Implemented for tracking application performance and issues.
10. **Security**: Best practices followed to secure the application.

## Database

The project uses **Azure SQL Database** as the RDBMS, which offers a free tier suitable for development and testing purposes.

## Setup Instructions

### Prerequisites

- Java JDK 11 or higher
- Node.js and npm
- Docker
- Terraform
- Azure account

### Backend Setup

1. Navigate to the `backend` directory.
2. Build the application using Maven:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the `frontend` directory.
2. Install dependencies:
   ```
   npm install
   ```
3. Run the application:
   ```
   ng serve
   ```

### Infrastructure Setup

1. Navigate to the `infrastructure` directory.
2. Initialize Terraform:
   ```
   terraform init
   ```
3. Apply the Terraform configuration:
   ```
   terraform apply
   ```

## Testing

- **Unit Tests**: Run using Maven for the backend and using Angular CLI for the frontend.
- **Integration Tests**: Execute the integration tests in the backend.
- **End-to-End Tests**: Run Protractor tests in the frontend.

## Docker

To build and run the application using Docker, use the provided `docker-compose.yml` file.

## Conclusion

This POC project serves as a comprehensive guide to understanding best practices in software design architecture while utilizing modern technologies. It provides a solid foundation for further exploration and development.