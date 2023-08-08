# Commands
MVN = mvn

# Targets
.PHONY: compile

compile:
	@$(MVN) clean package

run-app:
	@$(MVN) exec:java -Dexec.mainClass="com.killbills.App"

test:
	@$(MVN) test

