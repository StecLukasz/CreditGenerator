**Credit Calculator**
_Overview_
This is a simple Java application designed to calculate credit offers based on the user's financial details. 

The application asks for the user's monthly income, living expenses, and existing credit commitments to generate tailored credit offers.

_Requirements_
- Java 8 or higher
- Docker

**Running the Application with Docker**

Building the Docker Image
1. First, ensure Docker is installed and running on your system.

2. Clone or download this repository to your local machine.

3. Navigate to the directory containing the Dockerfile.

4. Build the Docker image using the following command:
`docker build -t creditgenerator .`

This command builds a Docker image named credit-calculator based on the instructions in your Dockerfile.

Running the Application in a Docker Container 
After building the image, you can run the application in a Docker container using the following command:

`docker-compose up --build`

This command starts a container named my-credit-calculator using the credit-calculator image. The -it flag attaches an interactive terminal to the container, allowing you to interact with the application. The --rm flag automatically removes the container when it stops.

**Using the Application**

After starting the application, follow the on-screen prompts to enter your financial details. 
The application will then display the available credit offers based on the provided information.

