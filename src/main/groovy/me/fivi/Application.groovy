package me.fivi

import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

@CompileStatic
class Application {
    static void main(String[] args) {
        Micronaut.build(args)
                .packages("com.mobileapp")
                .start()
    }
}