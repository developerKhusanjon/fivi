package me.fivi

import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

@CompileStatic
class FiviApp {
    static void main(String[] args) {
        Micronaut.build(args)
                .packages("me.fivi")
                .start()
    }
}