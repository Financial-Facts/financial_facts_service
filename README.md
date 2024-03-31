# Financial Facts Service</h1></u>

This Java Spring Boot service is a robust and secure solution designed to communicate with a PostgreSQL database hosted on Amazon Web Services (AWS) Relational Database Service (RDS). The service is configured to leverage AWS Parameter Store, enhancing security by storing sensitive parameters separately and dynamically pulling them when needed. It utilizes a basic authorization scheme on internal endpoints utilized by other services and enforces strict rate limits on public endpoints that pass data to the UI. It is optimized for high call volume and scalability. This service is designed with micro sized RDS instances in mind to optimize pricing and resource consumption. To this end, strict handlers are in place to manage more costly transactions and static data is fetched from the public U.S. Securities and Exchange Commission (SEC) EDGAR API and cached for less costly transactions. It also features thorough unit tests triggered during the build process to assure consistent behavior across all scenarios.

#### Dependency: [update facts lambda](https://github.com/Choochera/update_facts_lambda)

------

<u><h3>Primary Entities</h3></u>
<ul>
  <li>
    <h3>Facts</h3>
    The facts entity represents all the compiled financial data that is sourced from the public SEC EDGAR API. These can quickly become out of date, hence the architecture described throughout designed to ensure only the most up-to-date facts are fetched and stored. They are stored as json binary types in the database for storage optimization and data transfer sizes and pricing. The entities tend to quite large and parsing them is handled by the internal components that correspond to different taxonomies: the Gaap Retriever and the IFRS-full retriever, both of which utilize the component that interacts directly with these entities - the parser.
  </li>
  <li>
    <h3>Discounts</h3>
    Discounts represent the current public companies whose stock price has gone below the calculated discount price. This entity will primarily be handled by the Facts-Calculator-Service (coming soon!) however many endpoints are exposed within this service to offer asynchronous bulk updates, fetching, saving and deleting for these entities.
  </li>
  <li>
    <h3>Identities</h3>
    An identity is a mapping between the Central Index Key (CIK) of a company and their public name and stock ticker/symbol
  </li>
</ul>
<u><h3>Key Features</h3></u>
<ul>
  <li>
    <h3>Cloud Database Communication through AWS RDS Integration</h3>
    The service establishes a seamless connection using Java Persistence API (JPA) in conjunction with a PostgreSQL database hosted via AWS RDS, ensuring efficient schem structure, data storage and retrieval operations.
  </li> 
  <li>
    <h3>Parameter Security with AWS Parameter Store</h3>
    For enhanced security, sensitive configuration parameters, such as database credentials and API keys, are stored securely in the AWS Parameter Store. The service fetches these parameters at runtime, reducing the risk of exposure.
  </li>
  <li>
    <h3>Fetching SEC data with AWS API Gateway and S3 Integration</h3> 
    The service interacts with an AWS API Gateway, which acts as a proxy for an S3 bucket containing financial facts for publicly traded companies with a valid Central Index Key (CIK) sourced from the SEC public EDGAR API.
  </li>
  <li>
    <h3>Constant Up-To-Date Data with S3 Bucket Updates via Amazon EventBridge and AWS Lambda</h3> 
    The S3 bucket containing the financial data is updated periodically using an Amazon EventBridge cron job. This cron job triggers an asynchronously configured AWS Lambda function, ensuring the most up-to-date financial information is consistently available to the service.
  </li>
</ul>

<u><h3>Primary Responsibilities</h3></u>
<ul>
  <li>
    <h3>Data Synchronization</h3>
    The service is responsible for ensuring that only the most up-to-date financial facts for publicly traded companies are stored within the PostgreSQL database. It achieves this by regularly fetching the latest data from the S3 bucket via the AWS API Gateway.
  </li>
  <li>
    <h3>Discounts Management</h3>
    The service tracks and persists information about currently active or inactive discounts. When an active discount is detected, it offers the functionality to store the relevant data in the database. Conversely, if a discount is no longer valid, it offers the ability for deleting the outdated information.
  </li>
  <li>
    <h3>Public Company Identification</h3>
    The service ensures that essential identifying information for public companies, including their CIK, is available and accessible in the database. This information is crucial for various financial analysis and reporting processes.
  </li>
</ul>

<u><h3>Benefits</h3></u>
<ul>
  <li>
    <h3>Security</h3>
    Security features include strict rate limits implemented to protect again various forms of attacks, SSL certification, basic authentication for internal services, and secrets handling with AWS Parameter Store ensuring sensitive information remains secure and is accessed only when required, reducing the risk of unauthorized access.
  </li>
  <li>
    <h3>Scalability</h3>
    Leveraging AWS services like RDS, API Gateway, S3, and Lambda allows the service to scale effortlessly to handle increasing data loads and concurrent user requests.
  </li>
  <li>
    <h3>Reliability</h3>
    The service benefits from the robust infrastructure of AWS, ensuring high availability and minimal downtime.
  </li>
  <li>
    <h3>Automated Updates</h3>
    With Amazon EventBridge and AWS Lambda, the service automatically updates financial facts in the database, keeping the information current without manual intervention.
  </li>
</ul>

----------

<u><h2>Conclusion</h2></u>
<p>In summary, this Java Spring Boot service, integrated with AWS RDS, AWS Parameter Store, API Gateway, S3, and Lambda, provides a secure, scalable, and efficient solution to manage financial facts for publicly traded companies. It has the ability to synchronize data, handle discounts, and store public company information which ensures that the database contains accurate and up-to-date financial data for reliable analysis and reporting purposes.</p>

# Preview
![image](https://github.com/Choochera/financial_facts_service/assets/74555083/09d1cd00-e313-495d-81a4-a167b68a9ca3)

------------

Cloud Hosting
-------------------------
This service is hosted via AWS ECS using an AWS Fargate task definition. This blueprint describes the container in which the docker image will run for this serverless hosting solution. The task is handled by one or many services within an ECS cluster once or many times over simultaneously. For cost purposes, the capacity provider for financial facts service is Fargate Spot, which offers a reduced price in exchange for potentially limited processing and memory capacity in areas or times of higher demand. Within each service, there can be one or multiple tasks which are routed between by the application load balancer which does so based on routing rules defined by the admin. In this case, again for cost purposes, there is only a single task running and the load balancer serves primarily to reroute traffic from the HTTP port 80 to the one on which the service is hosted. 

Deployment
-------------------------
Deployment of new features is seamless with an AWS Pipeline configured to using AWS CodeCommit, AWS CodeBuild, and AWS CodeDeploy. With this CI/CD pipeline in place, deployments can be performed easily with every merged PR in a consistent and efficient way.

Technologies:
-------------------------
- Java
- Spring Boot
- Webflux
- JUnit 5
- Java Persistence API (JPA)
- Docker
- AWS SSM
- AWS API Gateway
- AWS RDS
- AWS Lambda
- AWS S3
- AWS Eventbridge
- AWS ECS
- AWS Fargate
- AWS Pipeline
- AWS CodeCommit
- AWS CodeBuild
- AWS CodeDeploy

---------------------------
Created and authored by Matthew Gabriel <br/>
Discounts calculated with data provided by [Financial Modeling Prep](https://financialmodelingprep.com/developer/docs/)
