repositories.remote += %w[ http://repo1.maven.org/maven2 ]
LOMBOK = 'org.projectlombok:lombok:jar:1.14.4'
OLTU_OAUTH2 = 'org.apache.oltu.oauth2:org.apache.oltu.oauth2.client:jar:1.0.0'
OLTU_COMMON = 'org.apache.oltu.oauth2:org.apache.oltu.oauth2.common:jar:1.0.0'
dependencies = [ LOMBOK, OLTU_COMMON, OLTU_OAUTH2 ]

define 'pokitdok-java' do
	project.version = '0.1'
	compile.with dependencies
	package :jar
end
