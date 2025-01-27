def call() {
    // Define the API key and NVD URL
    def apiKey = env.NVD_API_KEY  // Ensure you have this API key set in Jenkins environment variables
    def nvdUrl = "https://api.nvd.nist.gov/vuln/detail"
    
    // CVE ID to query
    def cve = 'CVE-2021-44228'  // Replace with the CVE you're interested in

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
