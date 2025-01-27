def call(){
    def apiKey = env.NVD_API_KEY
    def nvdUrl = "https://api.nvd.nist.gov/vuln/detail"
    
    // Fetch vulnerability data (example with CVE-2021-44228)
    def cve = 'CVE-2021-44228' // Change to the CVE ID you're interested in
    def response = sh(script: """
        curl -X GET "${nvdUrl}/${cve}?apiKey=${apiKey}" -H "Accept: application/json"
    """, returnStdout: true).trim()
    
    echo "NVD Data for ${cve}: ${response}"
    
    // Proceed with the dependency check
    dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'OWASP'
    dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
}
