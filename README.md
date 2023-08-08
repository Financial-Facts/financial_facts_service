<u><h1>Financial Facts Service</h1></u>

This Java Spring Boot service is a robust and secure solution designed to communicate with a PostgreSQL database hosted on Amazon Web Services (AWS) Relational Database Service (RDS). The service is configured to leverage AWS Parameter Store, enhancing security by storing sensitive parameters separately and dynamically pulling them when needed. It utilizes a basic authorization scheme as the intention is for this to be an intermediary service that does interact directly with the UI.

<u><h2>Key Features</h2></u>
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

<u><h2>Primary Responsibilities</h2></u>
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

<u><h2>Benefits</h2></u>
<ul>
  <li>
    <h3>Security</h3>
    By utilizing AWS Parameter Store, sensitive information remains secure and is accessed only when required, reducing the risk of unauthorized access.
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

<u><h2>Conclusion</h2></u>
  <p>In summary, this Java Spring Boot service, integrated with AWS RDS, AWS Parameter Store, API Gateway, S3, and Lambda, provides a secure, scalable, and efficient solution to manage financial facts for publicly traded companies. Its ability to synchronize data, handle discounts, and store public company information ensures that the database contains accurate and up-to-date financial data for reliable analysis and reporting purposes.</p>