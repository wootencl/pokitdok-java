repositories.remote += %w[ http://repo1.maven.org/maven2 http://mvnrepository.com ]

HTTP_CORE = 'org.apache.httpcomponents:httpcore:jar:4.3.2'
HTTP_CLIENT = 'org.apache.httpcomponents:httpclient:jar:4.3.4'
COMMONS_CODEC = 'commons-codec:commons-codec:jar:1.9'
COMMONS_LOGGING = 'commons-logging:commons-logging:jar:1.2'
JSON_SIMPLE = 'com.googlecode.json-simple:json-simple:jar:1.1.1'

dependencies = [ COMMONS_CODEC, COMMONS_LOGGING, HTTP_CORE, HTTP_CLIENT, JSON_SIMPLE ]

define 'pokitdok-java' do
	project.version = '0.1'
	compile.with dependencies
	test.using :testng
	test.with dependencies
	package :jar
end
