package org.aerogear.pushee.tests

import com.jayway.restassured.RestAssured
import groovy.json.JsonBuilder
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.test.api.ArquillianResource
import org.jboss.shrinkwrap.api.spec.WebArchive
import spock.lang.Specification

import spock.lang.Shared
import com.jayway.restassured.internal.TestSpecificationImpl

class RegisterMobileVariantsSpecification extends Specification {

    @Deployment(testable=false)
    def static WebArchive"create deployment"() {
        return Deployments.unifiedPushServer()
    }

    @ArquillianResource
    URL root;

    @Shared TestSpecificationImpl testSpec = RestAssured.createTestSpecification()
    @Shared def pushAppId
//    curl -v -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '{"name" : "MyApp", "description" :  "awesome app" }' http://localhost:8080/ag-push/rest/
    void "Registering a push application"() {

        given: "Application My App is about to be registered......"
        def json = new JsonBuilder()
        def request = testSpec.getRequestSpecification()
                .contentType("application/json")
                .header("Accept", "application/json")
                .body(json {
            name: "MyApp"
            description: "awesome app"
        })

        when: "Application is registered"
        def response = testSpec.getRequestSpecification().given().spec(request).post(root.toString() + "rest/applications")

        def maVar = response.body().jsonPath()
        pushAppId = maVar.get("pushAppId")

        then: "Response code 200 is returned"
        response.statusCode() == 200

        and: "Push App Id is not null"
        maVar.get("pushAppId") != null

        // Name must be not null ?
//        and: "AppName is not null"
//        maVar.get("name") != null

    }


    void "Registering a mobile variant instance"() {
//        curl -v -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '{"pushNetworkURL" : "http://localhost:7777/endpoint/"}' http://localhost:8080/ag-push/rest/applications/{PUSH_ID}/simplePush
        given: "Application My App is about to be registered......"
        def json = new JsonBuilder()
        def request = testSpec.getRequestSpecification()
                .contentType("application/json")
                .header("Accept", "application/json")
                .body(json {
            pushNetworkURL : "http://localhost:7777/endpoint/"
        })

        when: "Application is registered"
        def response = testSpec.getRequestSpecification().given().spec(request).post(root.toString() + "rest/applications/" + pushAppId + "/simplePush ")

        def maVar = response.body().jsonPath()

        then: "Response code 200 is returned"
        response.statusCode() == 200

        and: "Push App Id is not null"
        maVar.get("pushAppId") != null

        // Name must be not null ?
//        and: "AppName is not null"
//        maVar.get("name") != null

    }
}

