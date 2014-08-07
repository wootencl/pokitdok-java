require 'buildr/gpg'
require 'buildr/custom_pom'

VERSION_NUMBER = "0.3-SNAPSHOT"

repositories.remote += %w[ http://repo1.maven.org/maven2 http://mvnrepository.com ]

HTTP_CLIENT = transitive('org.apache.httpcomponents:httpclient:jar:4.3.4')
JSON_SIMPLE = 'com.googlecode.json-simple:json-simple:jar:1.1.1'
BETAMAX = transitive('co.freeside:betamax:jar:1.1.2')
GROOVY = transitive('org.codehaus.groovy:groovy:jar:2.3.4')

build_dependencies = [ HTTP_CLIENT, JSON_SIMPLE ]
test_dependencies = build_dependencies + [BETAMAX, GROOVY]

define 'pokitdok-java' do
	project.group = 'com.pokitdok'
	project.version = VERSION_NUMBER
	pom.add_mit_license
  pom.add_github_project('poktidok/pokitdok-java')
  pom.add_developer('riney', 'John Riney')
	compile.with build_dependencies
	test.using :testng
	test.with test_dependencies
	package :jar
	package :sources
  package :javadoc
end
