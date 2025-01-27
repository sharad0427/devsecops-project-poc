def call(cve) {
    // Define the API key and NVD URL
    def apiKey = env.NVD_API_KEY  // Ensure you have this API key set in Jenkins environment
    def nvdUrl = "https://services.nvd.nist.gov/rest/json/cve/1.0"
    
    // Fetch vulnerability data from the NVD API
    def response = sh(script: """
        curl -X GET "${nvdUrl}/${cve}?apiKey=${apiKey}" -H "Accept: application/json"
    """, returnStdout: true).trim()

    // Output the NVD data
    echo "NVD Data for ${cve}: ${response}"

    // Run OWASP Dependency Check
    dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'OWASP'

    // Publish the OWASP Dependency Check report
    dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
}
